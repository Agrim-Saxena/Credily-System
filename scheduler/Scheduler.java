package com.credv3.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.credv3.common.entities.Expirable;
import com.credv3.common.entities.Notification;
import com.credv3.common.entities.OnGoingMonitoring;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantKey;

@Component
public class Scheduler extends CentralService {
	@Scheduled(cron = "0 0/1 * * * ?")
	private void expirableNotificationScheduler() {

		List<Expirable> weeklyExpirables = expirableJpaRepo.findExpirablesToNotifyWeekly(7,
				ConstantKey.WEEKLY_FREQUENCY);

		for (Expirable expirable : weeklyExpirables) {
			Notification notification = new Notification();
			notification.setCreatedDate(new Date());
			notification.setType("expirable");
			notification.setName(expirable.getStateName() + " " + expirable.getDocType());
			SimpleDateFormat DateFor = new SimpleDateFormat("MM-dd-yyyy");
			notification.setDescription("The " + expirable.getDocType() + " of state '" + expirable.getStateName()
					+ "' will expire on " + DateFor.format( expirable.getExpirationDate()));
			notification.setProvider(expirable.getProvider());
			expirable.setNotificationDate(notification.getCreatedDate());
			expirableJpaRepo.save(expirable);
			
			String tag = notification.getType() + " " + notification.getName()+" "+ notification.getDescription();
			notification.setSearchTag(tag);
			
			notificationJpaRepository.save(notification);
		}
		List<Expirable> monthlyExpirables = expirableJpaRepo.findExpirablesToNotifyMonthly(1, ConstantKey.MONTHLY_FREQUENCY);

		for (Expirable expirable : monthlyExpirables) {
			Notification notification = new Notification();
			notification.setCreatedDate(new Date());
			notification.setType("expirable");
			notification.setName(expirable.getStateName() + " " + expirable.getDocType());
			SimpleDateFormat DateFor = new SimpleDateFormat("MM-dd-yyyy");
			notification.setDescription("The " + expirable.getDocType() + " of state '" + expirable.getStateName()
					+ "' will expire on " + DateFor.format( expirable.getExpirationDate()));;
			notification.setProvider(expirable.getProvider());
			expirable.setNotificationDate(notification.getCreatedDate());
			expirableJpaRepo.save(expirable);
			
			String tag = notification.getType() + " " + notification.getName()+" "+ notification.getDescription();
			notification.setSearchTag(tag);
			
			notificationJpaRepository.save(notification);
		}
		List<Expirable> yearlyExpirables = expirableJpaRepo.findExpirablesToNotifyYearly(1, ConstantKey.YEARLY_FREQUENCY);

		for (Expirable expirable : yearlyExpirables) {
			Notification notification = new Notification();
			notification.setCreatedDate(new Date());
			notification.setType("expirable");
			notification.setName(expirable.getStateName() + " " + expirable.getDocType());
			SimpleDateFormat DateFor = new SimpleDateFormat("MM-dd-yyyy");
			notification.setDescription("The " + expirable.getDocType() + " of state '" + expirable.getStateName()
					+ "' will expire on " + DateFor.format( expirable.getExpirationDate()));;
			notification.setProvider(expirable.getProvider());
			expirable.setNotificationDate(notification.getCreatedDate());
			expirableJpaRepo.save(expirable);
			String tag = notification.getType() + " " + notification.getName()+" "+ notification.getDescription();
			notification.setSearchTag(tag);
			notificationJpaRepository.save(notification);
		}
	}
	
	@Scheduled(cron = "0 0/1 * * * ?")
	private void ongoingMonitoringNotificationScheduler() {
		List<OnGoingMonitoring> weeklyMonitorings = onGoingMonitoringJPARepo.findMonitoringsToNotifyWeekly(7,
				ConstantKey.WEEKLY_FREQUENCY);
		
		for (OnGoingMonitoring onGoingMonitoring : weeklyMonitorings) {
			
			CompletableFuture.runAsync(() -> {
				restTemplateHandler.initiateCrawler(onGoingMonitoring.getId());
			});
			
		}
		List<OnGoingMonitoring> monthlyMonitorings = onGoingMonitoringJPARepo.findMonitoringsToNotifyMonthly(1, ConstantKey.MONTHLY_FREQUENCY);

		for (OnGoingMonitoring onGoingMonitoring : monthlyMonitorings)  {
			
			CompletableFuture.runAsync(() -> {
				restTemplateHandler.initiateCrawler(onGoingMonitoring.getId());
			});
		}
		List<OnGoingMonitoring> yearlyMonitorings = onGoingMonitoringJPARepo.findMonitoringsToNotifyYearly(1, ConstantKey.YEARLY_FREQUENCY);

		for (OnGoingMonitoring onGoingMonitoring : yearlyMonitorings) {
			
			CompletableFuture.runAsync(() -> {
				restTemplateHandler.initiateCrawler(onGoingMonitoring.getId());
			});
		}
	}
	
	
	@Scheduled(cron = "0 0/5 * * * ?")
	private void ongoingMonitoringPendingStatusScheduler() {
		
		List<OnGoingMonitoring> pendingStatusMonitorings = onGoingMonitoringJPARepo.findByMonitoringStatusAndUpdatedDateIsNull(ConstantKey.PENDING);
	
	
		for (OnGoingMonitoring onGoingMonitoring : pendingStatusMonitorings) {
			
			
			
//			CompletableFuture.runAsync(() -> {
//				restTemplateHandler.initiateCrawler(onGoingMonitoring.getId());
//			});
			
		}
	}
	
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void MonitoringQueueStatusScheduler() {
		
		if(!onGoingMonitoringJPARepo.existsByStatus(ConstantKey.INPROCESS)) {
			List<OnGoingMonitoring> monitoringQueueList = onGoingMonitoringJPARepo.findTopOneByStatus(ConstantKey.INQUEUE);
			
			for (OnGoingMonitoring onGoingMonitoring : monitoringQueueList) {
				
				CompletableFuture.runAsync(() -> {
					restTemplateHandler.initiateCrawler(onGoingMonitoring.getId());
				});
				
			}
			
		}
	  }
	}