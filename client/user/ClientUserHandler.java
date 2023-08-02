package com.credv3.client.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.common.entities.ClientContact;
import com.credv3.common.entities.ClientUser;
import com.credv3.common.entities.EnterpriseLeader;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.Status;
import com.credv3.common.entities.UserAccount;
import com.credv3.enterprise.leader.EnterpriseLeaderDetailsDTO;
import com.credv3.enterprise.leader.EnterpriseLeaderDetailsDTOMapper;
import com.credv3.enterprise.user.EnterpriseUserDTO;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ConstantKey;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.HelperExtension;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;
import com.credv3.userAccount.UserAccountHandler;
import com.credv3.userAccount.UserAccountReflection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientUserHandler extends CentralService {
	
	HelperExtension helperExtension = new HelperExtension();
	
	@Autowired
	private UserAccountHandler userAccountHandler;
	
	@Transactional(readOnly = false)
	public ResponseEntity<Object> createClientUser(ClientUserRequest request, RequestInterceptorResponse req){
	
		try {
			
			if(helperExtension.isNullOrEmpty(request.getEmail()) && helperExtension.isNullOrEmpty(req.getClientBusinessUuid())
					&& helperExtension.isNullOrEmpty(req.getClientUuid())) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.BAD_REQUEST), HttpStatus.OK);
			}
			
			ClientUser clientUser = new ClientUser();
			clientUser.setFirstName(request.getFirstName());
			clientUser.setLastName(request.getLastName());
			clientUser.setTitle(request.getTitle());
			clientUser.setPhone(request.getPhone());
			clientUser.setEmail(request.getEmail());
			clientUser.setTimezone(request.getTimeZone());
			clientUser.setExtentionNumber(request.getExtensionNumber());
			clientUser.setCompanyPhone(request.getCompanyPhone());
			Status status = new Status();
			status.setId(1l);
			clientUser.setStatus(status);
			
			if(!helperExtension.isNullOrEmpty(req.getClientBusinessUuid())) {
				ClientBusinessInfo cbc = new ClientBusinessInfo();
				cbc.setUuid(req.getClientBusinessUuid());
				clientUser.setClientBusinessInfo(cbc);
			}
			
			if(!helperExtension.isNullOrEmpty(req.getClientUuid())) {
				ClientContact cc = new ClientContact();
				cc.setUuid(req.getClientUuid());
				clientUser.setClientContact(cc);
			}
			
			clientUser.setCreatedDate(new Date());
			
			clientUserJPARepo.save(clientUser);
			
			userAccountHandler.createUser(request.getEmail(), request.getPassword(), clientUser.getUuid(),
					ConstantKey.ROLE_CLIENT_USER, ConstantKey.CLIENT);
			
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT USER HANDLER :: ERROR IN METHOD 'createClientUser' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getClientUser(RequestInterceptorResponse req, DatabaseHelper databaseHelper){
	
		try {
			
			List<ClientUserResp> respList = new ArrayList<>();
			
			List<ClientUser> expirableList = clientUserRepository.getList(databaseHelper, req.getUserUuid());
			Long totalItemsCount = clientUserRepository.getCount(databaseHelper, req.getUserUuid());
			
			for (ClientUser clientUser : expirableList) {
				ClientUserResp resp = new ClientUserResp();
				resp.setUuid(clientUser.getUuid());
				resp.setCompanyPhone(clientUser.getCompanyPhone());
				resp.setEmail(clientUser.getEmail());
				resp.setExtensionNumber(clientUser.getExtentionNumber());
				resp.setFirstName(clientUser.getFirstName());
				resp.setLastName(clientUser.getLastName());
				resp.setPhone(clientUser.getPhone());
				resp.setTimeZone(clientUser.getTimezone());
				resp.setTitle(clientUser.getTitle());
				resp.setStatus(clientUser.getStatus().getType());
				resp.setCreatedDate(clientUser.getCreatedDate());
				
				respList.add(resp);
			}
			
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED, respList, totalItemsCount), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT USER HANDLER :: ERROR IN METHOD 'getClientUser' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	public ResponseEntity<ClientUserResp> getClientUserByUuid(UUID clientUserUUID) {
		try {

			ClientUser clientUser = clientUserJPARepo.findByUuid(clientUserUUID);

			ClientUserResp resp = new ClientUserResp();
			resp.setUuid(clientUser.getUuid());
			resp.setCompanyPhone(clientUser.getCompanyPhone());
			resp.setEmail(clientUser.getEmail());
			resp.setExtensionNumber(clientUser.getExtentionNumber());
			resp.setFirstName(clientUser.getFirstName());
			resp.setLastName(clientUser.getLastName());
			resp.setPhone(clientUser.getPhone());
			resp.setTimeZone(clientUser.getTimezone());
			resp.setTitle(clientUser.getTitle());
			resp.setStatus(clientUser.getStatus().getType());
			resp.setCreatedDate(clientUser.getCreatedDate());

			return new ResponseEntity<ClientUserResp>(resp, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ClientUser> updateClientUserStatus(UUID clientUserUUID, String status) {
		try {

			Map<String, Object> map = new HashMap<>();
			
			UserAccount userAccount = userAccountJpaRepo.UserUuid(clientUserUUID);
			
			ClientUser clientUser = clientUserJPARepo.findByUuid(clientUserUUID);
			if (userAccount != null && clientUser != null) {
				if(status.equalsIgnoreCase("active")) {
					Status status2 = new Status();
					status2.setId(1);
					userAccount.setStatus(status2);
					userAccountJpaRepo.save(userAccount);
					
					clientUser.setStatus(status2);
					clientUserJPARepo.save(clientUser);
				}
				else if(status.equalsIgnoreCase("inactive")) {
					Status status2 = new Status();
					status2.setId(2);
					userAccount.setStatus(status2);
					userAccountJpaRepo.save(userAccount);
					
					clientUser.setStatus(status2);
					clientUserJPARepo.save(clientUser);
				}
				
				map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.STATUS_UPDATED);
				map.put(ConstantExtension.RETURN_STATUS_KEY, true);
				
				return new ResponseEntity(map, HttpStatus.OK);
			} else {
				map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.USER_NOT_EXIST);
				map.put(ConstantExtension.RETURN_STATUS_KEY, false);
				return new ResponseEntity(map, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<ClientUser>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ClientUser> updateClientuser(ClientUserRequest request) {
		try {
			Map<String, Object> map = new HashMap<>();
			
			ClientUser clientUser = clientUserJPARepo.findByUuid(request.getUuid());
			clientUser.setCompanyPhone(request.getCompanyPhone());
			clientUser.setEmail(request.getEmail());
			clientUser.setExtentionNumber(request.getExtensionNumber());
			clientUser.setFirstName(request.getFirstName());
			clientUser.setLastName(request.getLastName());
			clientUser.setPhone(request.getPhone());
			clientUser.setTimezone(request.getTimeZone());
			clientUser.setUpdatedDate(new Date());
			clientUser.setTitle(request.getTitle());
			
			clientUserJPARepo.save(clientUser);
			
			map.put(ConstantExtension.RETURN_STATUS_KEY, true);
			map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
			map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.SUCCESS_UPDATED);
			
			return new ResponseEntity(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(" ENTERPRISE LEADER HANDLER :: ERROR IN METHOD 'createEnterpriseLeader' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<Object> getUserByClient(RequestInterceptorResponse req){
		Map<String, Object> map = new HashMap<>();
		
		List<UserAccountReflection> clientUserAccounts = new ArrayList<>();
		
		if(req.getRoleType().equalsIgnoreCase(ConstantKey.ENTERPRISE)) {
			
			if (req.getRole().equalsIgnoreCase("Enterprise Leader")) {
				
				List<String> userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_UuidInString(req.getUserUuid().toString());
				
				clientUserAccounts = userAccountJpaRepo.findByEnterpirseUserAndUuids(userUuids);
			}else if(req.getRole().equalsIgnoreCase("Enterprise User")) {
				List<String> userUuids = new ArrayList<>();
				userUuids.add(req.getUserUuid().toString());
				
				clientUserAccounts = userAccountJpaRepo.findByEnterpirseUserAndUuids(userUuids);
			}else {
				clientUserAccounts = userAccountJpaRepo.findByEnterpirseUser();
			}
		} else {
			clientUserAccounts = clientUserJPARepo.findClientUserByClient(req.getUserUuid().toString());
		}
		
		map.put(ConstantExtension.RETURN_STATUS_KEY, true);
		map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);
		map.put(ConstantExtension.RETURN_OBJECT_KEY, clientUserAccounts);
		return new ResponseEntity<>(map, HttpStatus.OK);		
	}
	
	
	public ResponseEntity<Object> updateProviderOwner(UUID providerUuid, UUID ownerUuid, RequestInterceptorResponse req){
		try {
			
			Map<String, Object> map = new HashMap<>();

			Provider provider = providerJpaRepo.findTOP1ByUuid(providerUuid);
			
			UserAccount userAccount = new UserAccount();
			userAccount.setUuid(ownerUuid);
			provider.setOwner(userAccount);
			
			UserAccount updatedByAccount = new UserAccount();
			updatedByAccount.setUuid(req.getAccountUuid());
			provider.setUpdatedBy(updatedByAccount);

			providerJpaRepo.save(provider);
			
			map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
			map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.SUCCESS_UPDATED);
			map.put(ConstantExtension.RETURN_STATUS_KEY, true);
			return new ResponseEntity(map, HttpStatus.OK);
			
		}catch (Exception e) {
				e.printStackTrace();
				log.info("CLIENT USER HANDLER :: ERROR IN METHOD 'updateUserByClient' : " + e.getMessage());
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	
	
}

