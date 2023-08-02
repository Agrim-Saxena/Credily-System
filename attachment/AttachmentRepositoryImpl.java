package com.credv3.attachment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.credv3.common.entities.Attachment;
import com.credv3.helper.BaseRepository;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.HelperExtension;

@Repository
public class AttachmentRepositoryImpl extends BaseRepository<Attachment> {

	HelperExtension helperExtension = new HelperExtension();
	
public List<Attachment> getList(UUID uuid, UUID clientBusinessUuid, List<UUID> userUuids, DatabaseHelper databaseHelper, String type, int isTrackable) {
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Attachment> criteriaQuery = criteriaBuilder.createQuery(Attachment.class);
		Root<Attachment> root = criteriaQuery.from(Attachment.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		if (!helperExtension.isNullOrEmpty(uuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("provider").get("uuid"), uuid));
		}
		
		if (!helperExtension.isNullOrEmpty(clientBusinessUuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("clientBusinessInfo").get("uuid"), clientBusinessUuid));
		}
		
		if (userUuids != null && userUuids.size()>0 ) {
			predicateList.add(root.get("clientBusinessInfo").get("owner").get("userUuid").in(userUuids));
		}
		
		if(isTrackable > 0) {
			predicateList.add(criteriaBuilder.equal(root.get("isTrackable"), isTrackable));
		}
		if (!helperExtension.isNullOrEmpty(type)) {
			
			Calendar calendar = Calendar.getInstance();
			Date startDate = null;
			Date endDate = null;
			
			switch (type) {
			case "upToDate":
				calendar.add(Calendar.DAY_OF_MONTH, 90);
				startDate = calendar.getTime();
				predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), startDate));
				break;
			case "aboutToExpire":
				startDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_MONTH, 90);
				endDate = calendar.getTime();
				predicateList.add(criteriaBuilder.between(root.get("expirationDate"), startDate, endDate));

				break;
			case "expired":
				startDate = calendar.getTime();
				predicateList.add(criteriaBuilder.lessThan(root.get("expirationDate"), startDate));
				break;

			}

		}
		
		predicateList.add(criteriaBuilder.equal(root.get("isFlag"), 1));
		
		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {
				
				if(!helperExtension.isNullOrEmpty(databaseHelper.getSearchBy())) {
					predicateList.add(criteriaBuilder.like(root.get(databaseHelper.getSearchBy()).as(String.class),
							"%" + databaseHelper.getSearch() + "%"));
				}else {
					predicateList.add(criteriaBuilder.like(root.get("fileType").as(String.class),
							"%" + databaseHelper.getSearch() + "%"));
				}
				
			}
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSortOrder())
					&& !helperExtension.isNullOrEmpty(databaseHelper.getSortBy())) {
				// Sorting Starts
				if (databaseHelper.getSortOrder().equalsIgnoreCase("asc")) {
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get(databaseHelper.getSortBy())));
				} else if (databaseHelper.getSortOrder().equalsIgnoreCase("desc")) {
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get(databaseHelper.getSortBy())));
				}
			}
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
			// Pagination Starts
			if (databaseHelper!=null && databaseHelper.getCurrentPage() != 0 && databaseHelper.getItemsPerPage() != 0) {
				final TypedQuery<Attachment> typedQuery = entityManager.createQuery(criteriaQuery);
				typedQuery.setFirstResult((databaseHelper.getCurrentPage() - 1) * databaseHelper.getItemsPerPage());
				typedQuery.setMaxResults(databaseHelper.getItemsPerPage());
				return typedQuery.getResultList();
			}
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}
		return entityManager.createQuery(criteriaQuery).getResultList();
	}
	
	
	public long getCount(UUID uuid, UUID clientBusinessUuid, List<UUID> userUuids, DatabaseHelper databaseHelper, String type, int isTrackable) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Attachment> root = criteriaQuery.from(Attachment.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		criteriaQuery.select(criteriaBuilder.count(root));

		if (!helperExtension.isNullOrEmpty(uuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("provider").get("uuid"), uuid));
		}
		
		if (!helperExtension.isNullOrEmpty(clientBusinessUuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("clientBusinessInfo").get("uuid"), clientBusinessUuid ));
		}
		
		if (userUuids != null && userUuids.size()>0 ) {
			predicateList.add(root.get("clientBusinessInfo").get("owner").get("userUuid").in(userUuids));
		}
		if(isTrackable > 0) {
			predicateList.add(criteriaBuilder.equal(root.get("isTrackable"), isTrackable));
		}
		if (!helperExtension.isNullOrEmpty(type)) {
			
			Calendar calendar = Calendar.getInstance();
			Date startDate = null;
			Date endDate = null;
			
			switch (type) {
			case "upToDate":
				calendar.add(Calendar.DAY_OF_MONTH, 90);
				startDate = calendar.getTime();
				predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), startDate));
				break;
			case "aboutToExpire":
				startDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_MONTH, 90);
				endDate = calendar.getTime();
				predicateList.add(criteriaBuilder.between(root.get("expirationDate"), startDate, endDate));

				break;
			case "expired":
				startDate = calendar.getTime();
				predicateList.add(criteriaBuilder.lessThan(root.get("expirationDate"), startDate));
				break;

			}

		}
		
		predicateList.add(criteriaBuilder.equal(root.get("isFlag"), 1));
		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {
				
				if(!helperExtension.isNullOrEmpty(databaseHelper.getSearchBy())) {
					predicateList.add(criteriaBuilder.like(root.get(databaseHelper.getSearchBy()).as(String.class),
							"%" + databaseHelper.getSearch() + "%"));
				}else {
					predicateList.add(criteriaBuilder.like(root.get("fileType").as(String.class),
							"%" + databaseHelper.getSearch() + "%"));
				}
			}
			
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}
		return entityManager.createQuery(criteriaQuery).getSingleResult();

	}

	
}
