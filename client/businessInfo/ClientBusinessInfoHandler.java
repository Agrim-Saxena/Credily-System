package com.credv3.client.businessInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.client.businessAddress.ClientBusinessAddressMapper;
import com.credv3.client.businessLocation.ClientBusinessLocationMapper;
import com.credv3.client.contact.ClientContactMapper;
import com.credv3.client.contact.ClientContactRequest;
import com.credv3.common.entities.Attachment;
import com.credv3.common.entities.CLIAAttachment;
import com.credv3.common.entities.CLIAWaiverAttachment;
import com.credv3.common.entities.ClientBusinessAddress;
import com.credv3.common.entities.ClientBusinessContact;
import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.common.entities.ClientBusinessLocation;
import com.credv3.common.entities.ClientContact;
import com.credv3.common.entities.EnterpriseLeader;
import com.credv3.common.entities.Expirable;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.ProviderAddress;
import com.credv3.common.entities.UserAccount;
import com.credv3.expirable.ExpirableDTO;
import com.credv3.expirable.ExpirableDTOMapper;
import com.credv3.expirable.ExpirableDataRequest;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ConstantKey;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;
import com.credv3.passport.PassportAccessRequestJPARepo;
import com.credv3.passport.sharedDocument.SharedPassportDocJPARepo;
import com.credv3.provider.ProviderDTO;
import com.credv3.provider.ProviderDTOMapper;
import com.credv3.roleManagement.client.ClientRoleModuleHandler;
import com.credv3.userAccount.UserAccountHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientBusinessInfoHandler extends CentralService {

	@Autowired
	private UserAccountHandler userAccountHandler;
	
	@Autowired
	private ClientRoleModuleHandler clientRoleModuleHandler;
	
	@Autowired
    private	PassportAccessRequestJPARepo passportAccessRequestJPARepo;
	
	@Autowired
	private SharedPassportDocJPARepo sharedPassportDocJPARepo;

	ClientBusinessInfoMapper businessMapper = new ClientBusinessInfoMapper();

	@Transactional(readOnly = false)
	public ResponseEntity<Object> createClientBusinessInfo(ClientBusinessInfoRequest request, RequestInterceptorResponse req) {
		try {

			if (helperExtension.isNullOrEmpty(request.getLegalBusinessName()) || request.getContactReqList() == null
					|| (request.getContactReqList() != null && request.getContactReqList().size() == 0)) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.BAD_REQUEST), HttpStatus.OK);
			}
			
			for (ClientContactRequest contactReq : request.getContactReqList()) {
				if (contactReq.getIsPrimary() == 1 && userAccountJpaRepo.existsOneByUserName(contactReq.getEmail())) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.USER_EXIST), HttpStatus.OK);
				}
			}

			ClientBusinessInfo clientBusinessInfo = businessMapper.mapBusinessInfo(request);
			
			if(!helperExtension.isNullOrEmpty(request.getUserAccountUuid())){	
				UserAccount userAccount = new UserAccount();
				userAccount.setUuid(UUID.fromString(request.getUserAccountUuid()));
				clientBusinessInfo.setOwner(userAccount);
			}
			
			UserAccount updatedByAccount = new UserAccount();
			updatedByAccount.setUuid(req.getAccountUuid());
			clientBusinessInfo.setUpdatedBy(updatedByAccount);
			
			clientBusinessInfoJpaRepo.save(clientBusinessInfo);
			
//			if(!helperExtension.isNullOrEmpty(request.getUserUuid())) {
//				updateClientOwner(clientBusinessInfo.getUuid(), UUID.fromString(request.getUserUuid()) , req);
//			}

			if (request.getAddressReq() != null) {
				ClientBusinessAddressMapper addMappper = new ClientBusinessAddressMapper();
				request.getAddressReq().setClientBusinessInfoUuid(clientBusinessInfo.getUuid().toString());
				ClientBusinessAddress businessAddress = addMappper.mapClientBusinessAddress(request.getAddressReq());
				clientBusinessAddressJpaRepo.save(businessAddress);
			}

			if (request.getLocationReq() != null) {
				ClientBusinessLocationMapper locationMapper = new ClientBusinessLocationMapper();
				request.getLocationReq().setClientBusinessInfoUuid(clientBusinessInfo.getUuid().toString());
				ClientBusinessLocation businessLocation = locationMapper
						.mapClientBusinessLocation(request.getLocationReq());
				clientBusinessLocationJpaRepo.save(businessLocation);

				if (request.getCliaUrlList() != null && request.getCliaUrlList().size() > 0) {
					for (String url : request.getCliaUrlList()) {
						if(!helperExtension.isNullOrEmpty(url)) {
							CLIAAttachment cliaAttachment = new CLIAAttachment();
							cliaAttachment.setUrl(url);

							ClientBusinessLocation clientBusinessLocation = new ClientBusinessLocation();
							clientBusinessLocation.setId(businessLocation.getId());
							cliaAttachment.setClientBusinessLocation(clientBusinessLocation);
							cliaAttachmentJpaRepo.save(cliaAttachment);
						}
						
					}
				}

				if (request.getCliaWaiverUrlList() != null && request.getCliaWaiverUrlList().size() > 0) {
					for (String waiverUrl : request.getCliaWaiverUrlList()) {
						if(!helperExtension.isNullOrEmpty(waiverUrl)) {
							CLIAWaiverAttachment waiverAttachment = new CLIAWaiverAttachment();
							waiverAttachment.setUrl(waiverUrl);

							ClientBusinessLocation clientBusinessLocation = new ClientBusinessLocation();
							clientBusinessLocation.setId(businessLocation.getId());
							waiverAttachment.setClientBusinessLocation(clientBusinessLocation);
							cliaWaiverAttachmentJpaRepo.save(waiverAttachment);
						}
					}
				}

			}

			if (request.getContactReqList() != null && request.getContactReqList().size() > 0) {
				ClientContactMapper contactMapper = new ClientContactMapper();
				for (ClientContactRequest contactReq : request.getContactReqList()) {
					ClientContact contact = contactMapper.mapContact(contactReq);
					clientContactJpaRepo.save(contact);
					ClientBusinessContact businessCon = new ClientBusinessContact();

					ClientBusinessInfo bInfo = new ClientBusinessInfo();
					bInfo.setUuid(clientBusinessInfo.getUuid());
					businessCon.setClientBusinessInfo(clientBusinessInfo);
					businessCon.setClientContact(contact);
					clientBusinessContactJpaRepo.save(businessCon);

					try {
						if (contact.getIsPrimary() == 1) {
							
							userAccountHandler.createUser(contactReq.getEmail(), contactReq.getPassword(), contact.getUuid(), ConstantKey.ROLE_CLIENT, ConstantKey.CLIENT);
							
							clientRoleModuleHandler.createClientModule(contact.getUuid(), request.getModuleRequestList());
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}

			}
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'createClientBusinessInfo' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<Object> getAllClientBusinessInfo() {
		try {
			List<ClientBusinessInfo> responseList = clientBusinessInfoJpaRepo.findAllByIsFlag(1);
			
			ClientBusinessInfoDTOMapper dtoMapper = Mappers.getMapper(ClientBusinessInfoDTOMapper.class);
			List<ClientBusinessInfoDTO> dtoMapperList = dtoMapper.map(responseList);

			for (ClientBusinessInfoDTO clientBusinessInfoDTO : dtoMapperList) {
				ClientBusinessLocation clientBusinessLocation = clientBusinessLocationJpaRepo
						.findByClientBusinessInfo_Uuid(clientBusinessInfoDTO.getUuid());
				if (clientBusinessLocation != null) {
					clientBusinessInfoDTO.setClientBusinessLocation(clientBusinessLocation.toString());
				}
				if (clientBusinessInfoDTO.getOwner() != null) {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo
							.findTopOneByUuid(clientBusinessInfoDTO.getOwner().getUserUuid());
					Map<String, Object> ownerMap = new HashMap<>();
					ownerMap.put("id", clientBusinessInfoDTO.getOwner().getUuid());
					ownerMap.put("itemName", enterpriseLeader.toString());
					clientBusinessInfoDTO.setClientOwner(ownerMap);
				}
				clientBusinessInfoDTO.setOwner(null);
			}

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, dtoMapperList), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientBusinessInfo' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getAllClientBusinessInfo1(DatabaseHelper databaseHelper, RequestInterceptorResponse req) {
		try {
			
			List<UUID> userUuids = new ArrayList<>();
			
			if(req.getRole().equalsIgnoreCase("Enterprise User")){
				userUuids.add(req.getUserUuid());
			} 
//			else if(req.getRole().equalsIgnoreCase("Enterprise Leader")) {
//				userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_Uuid(req.getUserUuid().toString());
//			}
			
			List<ClientBusinessInfo> responseList = clientBusinessInfoRepository.get(databaseHelper, userUuids, req.getClientBusinessUuid());
			Long totalItemsCount = clientBusinessInfoRepository.getCount(databaseHelper, userUuids, req.getClientBusinessUuid());
			
			
			ClientBusinessInfoDTOMapper dtoMapper = Mappers.getMapper(ClientBusinessInfoDTOMapper.class);
			List<ClientBusinessInfoDTO> dtoMapperList = dtoMapper.map(responseList);

			for (ClientBusinessInfoDTO clientBusinessInfoDTO : dtoMapperList) {
				ClientBusinessLocation clientBusinessLocation = clientBusinessLocationJpaRepo
						.findByClientBusinessInfo_Uuid(clientBusinessInfoDTO.getUuid());
				if (clientBusinessLocation != null) {
					clientBusinessInfoDTO.setClientBusinessLocation(clientBusinessLocation.toString());
				}
				if (clientBusinessInfoDTO.getOwner() != null) {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo
							.findTopOneByUuid(clientBusinessInfoDTO.getOwner().getUserUuid());
					Map<String, Object> ownerMap = new HashMap<>();
					ownerMap.put("id", clientBusinessInfoDTO.getOwner().getUuid());
					ownerMap.put("itemName", enterpriseLeader.toString());
					clientBusinessInfoDTO.setClientOwner(ownerMap);
				}
				clientBusinessInfoDTO.setOwner(null);
			}
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, dtoMapperList, totalItemsCount), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientBusinessInfo' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<Object> getAllClientInfo(String bussinessName,RequestInterceptorResponse req) {
		try {

			List<Object> respList = new ArrayList<>();
			List<ClientBusinessContact> clientBusinessContacts = new ArrayList<>();
			
//			if(req.getRole().equalsIgnoreCase(ConstantKey.ENTERPRISE_LEADER_ROLE)){
//				
//				List<UUID> userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_Uuid(req.getUserUuid().toString());
//				
//				clientBusinessContacts = clientBusinessContactJpaRepo
//						.findTop10ByClientContact_isPrimaryAndClientBusinessInfo_Owner_UserUuidInAndClientBusinessInfo_LegalBusinessNameContaining(1, userUuids, bussinessName);
//			}
			
			if(req.getRole().equalsIgnoreCase(ConstantKey.ENTERPRISE_USER_ROLE)) {
				clientBusinessContacts = clientBusinessContactJpaRepo
						.findTop10ByClientContact_isPrimaryAndClientBusinessInfo_Owner_UserUuidAndClientBusinessInfo_LegalBusinessNameContainingOrderByClientBusinessInfo_LegalBusinessNameAsc(1,req.getUserUuid(), bussinessName);
			}else {
				clientBusinessContacts = clientBusinessContactJpaRepo
						.findTop10ByClientContact_isPrimaryAndClientBusinessInfo_LegalBusinessNameContainingOrderByClientBusinessInfo_LegalBusinessNameAsc(1, bussinessName);
			}
			 

			for (ClientBusinessContact clientBusinessContact : clientBusinessContacts) {
				Map<String, Object> map = new HashMap<>();
				map.put("businessContactId", clientBusinessContact.getId() + "");
				map.put("clientUuid", clientBusinessContact.getClientContact().getUuid().toString());
				map.put("clientBusinessUuid", clientBusinessContact.getClientBusinessInfo().getUuid().toString());
				map.put("legalBusinessName", clientBusinessContact.getClientBusinessInfo().getLegalBusinessName());
				respList.add(map);
				}

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, respList, respList.size()), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientBusinessInfo' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Map<String, Object>> updateClientOwner(UUID clientUUID, UUID userUUID,
			RequestInterceptorResponse req) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			UserAccount userAccount = new UserAccount();
			userAccount.setUuid(userUUID);
			ClientBusinessInfo clientBusinessInfo = clientBusinessInfoJpaRepo.findTopOneByUuid(clientUUID);
			clientBusinessInfo.setOwner(userAccount);

			UserAccount updatedByAccount = new UserAccount();
			updatedByAccount.setUuid(req.getAccountUuid());
			clientBusinessInfo.setUpdatedBy(updatedByAccount);
			clientBusinessInfoJpaRepo.save(clientBusinessInfo);

			responseMap.put(ConstantExtension.RETURN_STATUS_KEY, true);
			responseMap.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.UPDATED);
			responseMap.put(ConstantExtension.RETURN_OBJECT_KEY, null);
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientBusinessInfo' : " + e.getMessage());
			responseMap.put(ConstantExtension.RETURN_STATUS_KEY, false);
			responseMap.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.INTERNAL_SERVER_ERROR);
			responseMap.put(ConstantExtension.RETURN_OBJECT_KEY, null);
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
//	@Transactional
	public ResponseEntity<Object> deleteClientInfo(UUID clientUuid) {
		try {
			
			List<Attachment> attachment = attachmentJpaRepository.findAllByClientBusinessInfoUuid(clientUuid);
			for (Attachment att : attachment) {
				
				documentShareJpaRepository.deleteByDocument_Id(att.getId());
			}
			attachmentJpaRepository.deleteByClientBusinessInfo_Uuid(clientUuid);

			sharedPassportDocJPARepo.deleteByExpirableIdIn(expirableJpaRepo.getExpirableIdsByClientBusinessUuid(clientUuid.toString()));
			
			List<Provider> listProvider = providerJpaRepo.findByClientBusinessInfo_Uuid(clientUuid);
			for(Provider provider : listProvider) {
				expirableJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerAddressJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerCaqhInfoJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerCdsJpaRepository.deleteByProvider_Uuid(provider.getUuid());
				providerCertificationJpaRepository.deleteByProvider_Uuid(provider.getUuid());
				providerDeaJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerEcfmgJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerExperienceOrSkillJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerFifthPathwayJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerHospitalAffilationJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerIdNumberJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerInsuranceJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerLicenseJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerMedicaidJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerMedicareJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				practiceDetailJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerPracticeLocationSetupJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerProfessionalLicenseJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerProfessionalSchoolInfoJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerProfessionalTrainingJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerRoleModuleJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerSpecialityJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerUndergraduateEducationJPARepo.deleteByProvider_Uuid(provider.getUuid());
				providerUsmleJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				providerWorkerCompJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				caqhRosterBatchesRepository.deleteByProvider_Uuid(provider.getUuid());
				employmentRecordsJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				gapRecordsJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				militaryJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				onGoingMonitoringJPARepo.deleteByProvider_Uuid(provider.getUuid());
				auditTrailJpaRepo.deleteByProvider_Uuid(provider.getUuid());
				notificationJpaRepository.deleteByProvider_Uuid(provider.getUuid());
				passportAccessRequestJPARepo.deleteByProviderUuid(provider.getUuid().toString());
				
				try {
					String uuid = userAccountJpaRepo.getAccountUuid(provider.getUuid().toString());
					if(!helperExtension.isNullOrEmpty(uuid)){
						firebaseAuth.deleteUser(uuid);						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				userAccountJpaRepo.deleteByUserUuid(provider.getUuid());
			}
			providerJpaRepo.deleteByClientBusinessInfo_Uuid(clientUuid);
			
			List<ClientBusinessLocation> clientLocation = clientBusinessLocationJpaRepo.findAllByClientBusinessInfo_Uuid(clientUuid);
			for (ClientBusinessLocation clientBusinessLocation : clientLocation) {
				cliaAttachmentJpaRepo.deleteByClientBusinessLocation_Id(clientBusinessLocation.getId());
				cliaWaiverAttachmentJpaRepo.deleteByClientBusinessLocation_Id(clientBusinessLocation.getId());
			}
			clientBusinessLocationJpaRepo.deleteByClientBusinessInfo_Uuid(clientUuid);
			
			clientBusinessAddressJpaRepo.deleteByClientBusinessInfo_Uuid(clientUuid);
			clientUserJPARepo.deleteByClientBusinessInfo_Uuid(clientUuid);
			inviteJpaRepository.deleteByClientBusinessInfo_Uuid(clientUuid);
			
			try {
				String uuid = userAccountJpaRepo.getAccountUuid(clientBusinessContactJpaRepo.getClientContactUuid(clientUuid.toString()));
				if(!helperExtension.isNullOrEmpty(uuid)){
					firebaseAuth.deleteUser(uuid);						
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<ClientBusinessContact> clientBusinessContactList = clientBusinessContactJpaRepo.findByClientBusinessInfo_Uuid(clientUuid);
			List<UUID> uuids = new ArrayList<>();
 			for (ClientBusinessContact clientBusinessContact : clientBusinessContactList) {
 				uuids.add(clientBusinessContact.getClientContact().getUuid());
 				
				if(clientBusinessContact.getClientContact().getIsPrimary() == 1) {
					clientRoleModuleJPARepo.deleteByClientContact_Uuid(clientBusinessContact.getClientContact().getUuid());
//					userAccountJpaRepo.deleteByUserUuid(clientBusinessContact.getClientContact().getUuid());
				}
			}
			clientBusinessContactJpaRepo.deleteByClientBusinessInfo_Uuid(clientUuid);
			clientContactJpaRepo.deleteAllByUuidIn(uuids);			
			clientBusinessInfoJpaRepo.deleteByUuid(clientUuid);
			
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS INFO HANDLER :: ERROR IN METHOD 'deleteClientInfo' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ClientBusinessInfoDTO> getClientBusinessInfo(UUID clientUuid) {
		try {
			ClientBusinessInfo clientBusinessInfo = clientBusinessInfoJpaRepo.findTopOneByUuid(clientUuid);
			
			Map<String, Object> map = new HashMap<>();
			
			if (clientBusinessInfo != null) {
				
				ClientBusinessInfoDTOMapper dtoMapper = Mappers.getMapper(ClientBusinessInfoDTOMapper.class);
				ClientBusinessInfoDTO dto = dtoMapper.map(clientBusinessInfo);
				
				ClientBusinessLocation clientBusinessLocation = clientBusinessLocationJpaRepo
						.findByClientBusinessInfo_Uuid(dto.getUuid());
				if (clientBusinessLocation != null) {
					dto.setClientBusinessLocation(clientBusinessLocation.toString());
				}
				if (dto.getOwner() != null) {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo
							.findTopOneByUuid(dto.getOwner().getUserUuid());
					Map<String, Object> ownerMap = new HashMap<>();
					ownerMap.put("id", dto.getOwner().getUuid());
					ownerMap.put("itemName", enterpriseLeader.toString());
					dto.setClientOwner(ownerMap);
				}
				dto.setOwner(null);
				
				
				map.put(ConstantExtension.RETURN_STATUS_KEY, true);
				map.put(ConstantExtension.RETURN_OBJECT_KEY, dto);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);	
			} else {
				map.put(ConstantExtension.RETURN_STATUS_KEY, false);
				map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.USER_NOT_EXIST);	
			}
			
			return new ResponseEntity(map, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
	
	
	
	
	public ResponseEntity<Map<String, Object>> getClientExpirable(ExpirableDataRequest expirableDataRequest, RequestInterceptorResponse req) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<UUID> userUuids = new ArrayList<>();
 			
			if(req.getRoleType().equalsIgnoreCase(ConstantKey.CLIENT) && helperExtension.isNullOrEmpty(expirableDataRequest.getClientBusinessUuid())) {
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);
				map.put(ConstantExtension.RETURN_OBJECT_KEY, new ArrayList<>());
				map.put(ConstantExtension.TOTAL_ITEMS, 0);
				return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
			}
			
			if(req.getRole().equalsIgnoreCase("Enterprise User")){
				userUuids.add(req.getUserUuid());
			}
//			else if(req.getRole().equalsIgnoreCase("Enterprise Leader")) {
//				userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_Uuid(req.getUserUuid().toString());
//			}
			
			List<Expirable> expirableList = expirableRepository.get(expirableDataRequest, userUuids);
			Long totalItemsCount = expirableRepository.getCount(expirableDataRequest, userUuids);

			ExpirableDTOMapper dtoMapper = Mappers.getMapper(ExpirableDTOMapper.class);
			List<ExpirableDTO> dtoList = dtoMapper.map(expirableList);
			for (ExpirableDTO dto : dtoList) {
				Expirable expirable = expirableJpaRepo.findById(dto.getId()).orElse(null);

				Provider provider = expirable.getProvider();
				ProviderDTOMapper mapper = Mappers.getMapper(ProviderDTOMapper.class);
				ProviderDTO providerDTO = mapper.map(provider);

				ProviderAddress add = providerAddressJPARepo.findTop1ByProvider_UuidAndTypeIgnoreCase(providerDTO.getUuid(), "Primary");
				if (add != null) {
					providerDTO.setLocation(add.toString());
					providerDTO.setAddressLine1(add.getAddressLine1());
				}

				String specialty = providerSpecialityJPARepo.getDescription(providerDTO.getUuid().toString());
				providerDTO.setSpecialty(specialty);
				
				Calendar calendar = Calendar.getInstance();
				Date todayDate = calendar.getTime();
				Date startDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_MONTH, 90);
				Date endDate = calendar.getTime();
				
				
				if(expirableJpaRepo.existsByProvider_UuidAndExpirationDateLessThanEqual(providerDTO.getUuid(), todayDate)) {
					providerDTO.setExpirableStatus(ConstantKey.EXPIRED);
				} else if(expirableJpaRepo.existsByProvider_UuidAndExpirationDateBetween(providerDTO.getUuid(),startDate, endDate)) {
					providerDTO.setExpirableStatus(ConstantKey.ABOUT_TO_EXPIRE);
				} else {
					providerDTO.setExpirableStatus(ConstantKey.UP_TO_DATE);
				}

				dto.setProvider(providerDTO);

			}
			map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);
			map.put(ConstantExtension.RETURN_OBJECT_KEY, dtoList);
			map.put(ConstantExtension.TOTAL_ITEMS, totalItemsCount);
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("EXPIRABLE HANDLER :: ERROR IN METHOD 'getAllExpirables' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
