package com.credv3.userAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.credv3.common.entities.ClientBusinessContact;
import com.credv3.common.entities.ClientContact;
import com.credv3.common.entities.ClientUser;
import com.credv3.common.entities.Enterprise;
import com.credv3.common.entities.EnterpriseLeader;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.Role;
import com.credv3.common.entities.Status;
import com.credv3.common.entities.UserAccount;
import com.credv3.customAnnotation.ExecutionTimeAnnotation;
import com.credv3.emailNotification.EmailNotificationHandler;
import com.credv3.emailNotification.NotificationMessageRequest;
import com.credv3.firebase.FbAuthToken;
import com.credv3.firebase.RefreshAccessRes;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ConstantKey;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;
import com.credv3.helper.TokenResponse;
import com.credv3.roleManagement.RoleManagementHandler;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserAccountHandler extends CentralService {
	
	@Autowired
	private RoleManagementHandler roleManagementHandler;
	
	@Autowired
	private EmailNotificationHandler emailNotificationHandler;
	
	private LoadingCache<String, Integer> otpCache;

	public UserAccountHandler() {
		super();
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(ConstantKey.OTP_EXPIRY_TIME, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	public void createUser(String userName, String password, UUID userUuid, long roleId, String type) {

		UserAccount userAccount = new UserAccount();
		userAccount.setUserName(userName);
		userAccount.setPassword(password);
		userAccount.setUserUuid(userUuid);
		userAccount.setLastLoginDt(new Date());
		userAccount.setCreatedDate(new Date());
		userAccount.setUpdatedDate(new Date());
		userAccount.setType(type);
		if (roleId > 0) {
			Role role = roleJpaRepo.findById(roleId).orElse(null);
			userAccount.setRole(role);
		}
		Status status = new Status();
		status.setId(ConstantKey.ACTIVE);
		userAccount.setStatus(status);
		userAccountJpaRepo.save(userAccount);

		try {
			String clientBusinessUuid = "";
			if (type.equalsIgnoreCase(ConstantKey.CLIENT)) {

				ClientBusinessContact cbc = clientBusinessContactJpaRepo.findTopOneByClientContact_Uuid(userUuid);
				if (cbc != null && cbc.getClientBusinessInfo() != null) {
					clientBusinessUuid = cbc.getClientBusinessInfo().getUuid().toString();
				}

			}
			saveUserOnFirebase(userAccount, clientBusinessUuid);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	 adding user on firebase
	
	public boolean saveUserOnFirebase(UserAccount user, String clientBusinessUuid) {

		try {

			UserRecord userRecord = FbAuthToken.createEmailUser(user.getUuid().toString(), user.getUserName());

			String role = "";
			if (user.getRole() != null) {
				role = user.getRole().getName();
			}
			FbAuthToken.setCustomUserClaims(userRecord.getUid(), user.getUuid().toString(),
					user.getUserUuid().toString(), clientBusinessUuid, role, user.getType());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//	 adding user on firebase manually
	public boolean saveUserOnFirebase(String uuid, String userUuid, String clientBusinessUuid, String email,
			String role, String roleType) {

		try {

			UserRecord userRecord = FbAuthToken.createEmailUser(uuid, email);

			FbAuthToken.setCustomUserClaims(userRecord.getUid(), uuid, userUuid, clientBusinessUuid, role, roleType);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ResponseEntity<Map<String, Object>> getUsers(String type, String role, UUID userUuid) {
		Map<String, Object> map = new HashMap<>();
		List<UserAccountReflection> userAccounts = null;
		
		if (type.equalsIgnoreCase("enterprise-user") && role.equalsIgnoreCase("Enterprise Leader")) {
			
			List<String> userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_UuidInString(userUuid.toString());
			
			userAccounts = userAccountJpaRepo.findByEnterpirseUserAndUuids(userUuids);
		}else if(type.equalsIgnoreCase("enterprise-user") && role.equalsIgnoreCase("Enterprise User")) {
			List<String> userUuids = new ArrayList<>();
			userUuids.add(userUuid.toString());
			
			userAccounts = userAccountJpaRepo.findByEnterpirseUserAndUuids(userUuids);
		}else {
			userAccounts = userAccountJpaRepo.findByEnterpirseUser();
		}
		
		map.put(ConstantExtension.RETURN_STATUS_KEY, true);
		map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);
		map.put(ConstantExtension.RETURN_OBJECT_KEY, userAccounts);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
	
	public ResponseEntity<Object> loginUser(String userName, String password) {
		try {

			UserAccount userAccount = userAccountJpaRepo.findTopOneByUserNameAndPasswordAndIsFlag(userName, password, 1);
			if (userAccount == null) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INVALID_USER_OR_PASSWORD), HttpStatus.OK);
			}
			
			System.out.println("GET FIREBASE USER : " + userName);

			String uid = FbAuthToken.getUserByEmail(userName).getUid();
			String clientBusinessUuid = null;
			TokenResponse tokenResponse = new TokenResponse();
			
			if (userAccount.getType().equalsIgnoreCase(ConstantKey.CLIENT)
					&& userAccount.getRole().getId() == 3) {
				ClientBusinessContact cbc = clientBusinessContactJpaRepo
						.findTopOneByClientContact_Uuid(userAccount.getUserUuid());

				if (cbc != null && cbc.getClientBusinessInfo() != null) {
					clientBusinessUuid = cbc.getClientBusinessInfo().getUuid().toString();
					tokenResponse.setClientBusinessUuid(cbc.getClientBusinessInfo().getUuid().toString());
				}
			} else if (userAccount.getType().equalsIgnoreCase(ConstantKey.CLIENT)
					&& userAccount.getRole().getId() == 4) {

				ClientUser cu = clientUserJPARepo.findByUuid(userAccount.getUserUuid());
				if (cu != null) {
					clientBusinessUuid = cu.getClientBusinessInfo().getUuid().toString();
					tokenResponse.setClientBusinessUuid(cu.getClientBusinessInfo().getUuid().toString());
				}

			}

			try {
				
				FbAuthToken.setCustomUserClaims(uid, userAccount.getUuid().toString(), userAccount.getUserUuid().toString(), clientBusinessUuid,
						userAccount.getRole().getName(), userAccount.getType());
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			String customToken = FirebaseAuth.getInstance().createCustomToken(uid);

			IdTokenResponse idTokenResponse = firebaseRestTemplateHandler.getIdTokenFromCustomToken(customToken);

			System.out.println("FIREBASE TOKEN : "+ idTokenResponse);

			tokenResponse.setIdToken(idTokenResponse.getUnknownKeys().get("idToken").toString());
			tokenResponse.setRefreshToken(idTokenResponse.getUnknownKeys().get("refreshToken").toString());
			tokenResponse.setAccountUuid(userAccount.getUuid().toString());
			tokenResponse.setUserUuid(userAccount.getUserUuid().toString());
			tokenResponse.setEmail(userAccount.getUserName());
			tokenResponse.setIsPasswordReset(userAccount.getIsPasswordReset());
			tokenResponse.setIsInternalRole(userAccount.getRole().getIsInternal());

			List<Map<String, Object>> respList = new ArrayList<>();

			tokenResponse.setModules(respList);
			tokenResponse.setRole(userAccount.getRole().getName());
			tokenResponse.setRoleType(userAccount.getType());
			
			/*********************** save role module on redis  *************************/
//			try {
//				
//				List<RoleModuleDTO> modList = roleManagementHandler.getUserModuleList(userAccount.getUuid().toString());
//				
//				if(modList!=null) {
//					
//					RequestInterceptorReq req = new RequestInterceptorReq();
//					req.setRole(userAccount.getRole().getName());
//					req.setUuid(userAccount.getUuid());						
//					req.setModules(modList);
//					helperExtension.setRedisDataWithoutTimeOut(userAccount.getUuid().toString(), req.toString());
//				}
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.info("PROVIDER HANDLER :: error in login due to redis : " + e.getMessage());
//			}

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, tokenResponse), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("PROVIDER HANDLER :: ERROR IN METHOD 'getAllProvider' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ExecutionTimeAnnotation
	public ResponseEntity<Object> getIdTokenFromRefreshToken(String refreshToken) {
		try {
			RefreshAccessRes response = new RefreshAccessRes();
			if (!helperExtension.isNullOrEmpty(refreshToken)) {
				response = firebaseRestTemplateHandler.getIdTokenFromRefreshToken(refreshToken);
			}

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, response), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'getIdTokenFromRefreshToken' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<Object> logoutRequest(RequestInterceptorResponse req) {
		try {

			helperExtension.deletDataFromRedis(req.getAccountUuid().toString());

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS), HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'logoutRequest' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public String serverOTP(String userName, int otp) {
		log.error("OTP for phone number " + userName + " : " + otp);
		String key = userName;
		otpCache.put(key, otp);
		return key;
	}

	public int getServerOTP(String userName) throws ExecutionException {
		int serverOTP = 0;
		String key = userName;
		serverOTP = otpCache.get(key);
		return serverOTP;
	}

	public ResponseEntity<Object> getForgetPasswordOTP(String userName) {
		try {

			if (!userAccountJpaRepo.existsOneByUserNameAndIsFlag(userName, 1)) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.USER_NOT_EXIST), HttpStatus.OK);
			}

			String otp = helperExtension.getSixDigitOtp();

			System.out.println("login otp : " + otp);
			serverOTP(userName, Integer.valueOf(otp));
			
			NotificationMessageRequest msgReq = new NotificationMessageRequest(); 
			msgReq.setOtp(otp);
			emailNotificationHandler.microsoftEmailWithTemplate(ConstantKey.OTP_TEMPLATE, userName, msgReq);

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'getForgetPasswordOTP' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<Object> verifyForgetPasswordOTP(String userName, String otp) {
		try {
			String serverOTP = String.valueOf(getServerOTP(userName));
			if (!otp.equalsIgnoreCase(serverOTP)) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INVALID_OTP), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.VERIFY_OTP), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'verifyForgetPasswordOTP' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> resetFirstPassword(String password, UUID userUUID) {
		try {

			UserAccount userAccount = userAccountJpaRepo.findByUuid(userUUID);
			if (userAccount != null) {
				userAccount.setPassword(password);
				userAccount.setIsPasswordReset(1);
				userAccount.setUpdatedDate(new Date());
				userAccountJpaRepo.save(userAccount);
			}

			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_UPDATED), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'resetFirstPassword' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_UPDATED), HttpStatus.OK);
		}
	}

	public ResponseEntity<Object> updateToken(String clientBusinessUuid, String providerUuid,
			RequestInterceptorResponse req) {
		try {

//			UserAccount userAccount = userAccountJpaRepo.findByUuid(UUID.fromString(accountUuid));

//			UserRecord userRecord = FbAuthToken.getUserByEmail(req.getUserName());

			String uid = FbAuthToken.getUserByEmail(req.getUserName()).getUid();

			if (!helperExtension.isNullOrEmpty(clientBusinessUuid)) {
				req.setClientBusinessUuid(UUID.fromString(clientBusinessUuid));
			} else {
				req.setClientBusinessUuid(null);
			}

			if (!helperExtension.isNullOrEmpty(providerUuid)) {
				req.setProviderUuid(UUID.fromString(providerUuid));
			} else {
				req.setProviderUuid(null);
			}

			FbAuthToken.updateCustomUserClaims(uid, req);

			String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
			IdTokenResponse idTokenResponse = firebaseRestTemplateHandler.getIdTokenFromCustomToken(customToken);

			Map<String, String> resp = new HashMap<>();
			resp.put("idToken", idTokenResponse.getUnknownKeys().get("idToken").toString());
			resp.put("refreshToken", idTokenResponse.getUnknownKeys().get("refreshToken").toString());

			return new ResponseEntity<Object>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_UPDATED, resp),
					HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'resetFirstPassword' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ProfileDTO> getUserProfileData(UUID userUUID) {
		try {
			UserAccount userAccount = userAccountJpaRepo.findByUserUuid(userUUID);
			ProfileDTO profileDTO = new ProfileDTO();
			if (userAccount.getType().equalsIgnoreCase("ENTERPRISE")) {
				if (userAccount.getRole().getName().equalsIgnoreCase("Super Admin")) {
					Enterprise enterprise = enterpriseJpaRepository.findByUuid(userUUID);

					profileDTO.setUserUUID(userUUID);
					profileDTO.setEmail(enterprise.getEmail());
					profileDTO.setFirstName(enterprise.getFirstName());
					profileDTO.setLastName(enterprise.getLastName());
					profileDTO.setPhone(enterprise.getPhone());
					profileDTO.setRoleId(userAccount.getRole().getId());
					profileDTO.setRoleName(userAccount.getRole().getName());
					profileDTO.setExtNumber(enterprise.getExtensionNumber());
					profileDTO.setTitle(enterprise.getTitle());
					profileDTO.setBusinessPhone(enterprise.getBusinessPhone());
				} else {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo.findByUuid(userUUID);

					profileDTO.setUserUUID(userUUID);
					profileDTO.setEmail(enterpriseLeader.getEmail());
					profileDTO.setFirstName(enterpriseLeader.getFirstName());
					profileDTO.setLastName(enterpriseLeader.getLastName());
					profileDTO.setPhone(enterpriseLeader.getPhone());
					profileDTO.setRoleId(userAccount.getRole().getId());
					profileDTO.setRoleName(userAccount.getRole().getName());
					profileDTO.setExtNumber(enterpriseLeader.getExtensionNumber());
					profileDTO.setTitle(enterpriseLeader.getTitle());
					profileDTO.setBusinessPhone(enterpriseLeader.getCompanyPhone());
				}

			} else if (userAccount.getType().equalsIgnoreCase("CLIENT")) {
				if (userAccount.getRole().getName().equalsIgnoreCase("Client")) {
					ClientContact clientContact = clientContactJpaRepo.findByUuid(userUUID);
					profileDTO.setUserUUID(userUUID);
					profileDTO.setEmail(clientContact.getEmail());
					profileDTO.setFirstName(clientContact.getFirstName());
					profileDTO.setLastName(clientContact.getLastName());
					profileDTO.setPhone(clientContact.getPhone());
					profileDTO.setRoleId(userAccount.getRole().getId());
					profileDTO.setRoleName(userAccount.getRole().getName());
					profileDTO.setExtNumber(clientContact.getExtensionNumber());
					profileDTO.setTitle(clientContact.getTitle());
					profileDTO.setBusinessPhone(clientContact.getBusinessPhone());
				} else {
					ClientUser clientUser = clientUserJPARepo.findByUuid(userUUID);
					profileDTO.setUserUUID(userUUID);
					profileDTO.setEmail(clientUser.getEmail());
					profileDTO.setFirstName(clientUser.getFirstName());
					profileDTO.setLastName(clientUser.getLastName());
					profileDTO.setPhone(clientUser.getPhone());
					profileDTO.setRoleId(userAccount.getRole().getId());
					profileDTO.setRoleName(userAccount.getRole().getName());
					profileDTO.setExtNumber(clientUser.getExtentionNumber());
					profileDTO.setTitle(clientUser.getTitle());
					profileDTO.setBusinessPhone(clientUser.getCompanyPhone());
				}

			} else if (userAccount.getType().equalsIgnoreCase("PROVIDER")) {
				Provider provider = providerJpaRepo.findByUuid(userUUID);
				profileDTO.setUserUUID(userUUID);
				profileDTO.setEmail(provider.getEmail());
				profileDTO.setFirstName(provider.getFirstName());
				profileDTO.setLastName(provider.getLastName());
				profileDTO.setPhone(provider.getPhone());
				profileDTO.setRoleId(userAccount.getRole().getId());
				profileDTO.setRoleName(userAccount.getRole().getName());
				profileDTO.setExtNumber(provider.getExtensionNumber());
				profileDTO.setTitle(provider.getTitle());
				profileDTO.setBusinessPhone(provider.getBusinessPhone());
			}

			return new ResponseEntity(profileDTO, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'getUserProfileData' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<ProfileDTO> updateProfile(UUID userUuid, ProfileDTO request) {
		try {
			UserAccount userAccount = userAccountJpaRepo.findByUserUuid(userUuid);

			if (userAccount.getType().equalsIgnoreCase("ENTERPRISE")) {
				if (userAccount.getRole().getName().equalsIgnoreCase("Super Admin")) {
					Enterprise enterprise = enterpriseJpaRepository.findByUuid(userUuid);

					enterprise.setEmail(request.getEmail());
					enterprise.setFirstName(request.getFirstName());
					enterprise.setLastName(request.getLastName());
					enterprise.setPhone(request.getPhone());
					enterprise.setExtensionNumber(request.getExtNumber());
					enterprise.setTitle(request.getTitle());
					enterprise.setBusinessPhone(request.getBusinessPhone());

//					Role role = new Role();
//					role.setId(request.getRoleId());					
//					userAccount.setRole(role);

					enterpriseJpaRepository.save(enterprise);
				} else {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo.findByUuid(userUuid);

					enterpriseLeader.setEmail(request.getEmail());
					enterpriseLeader.setFirstName(request.getFirstName());
					enterpriseLeader.setLastName(request.getLastName());
					enterpriseLeader.setPhone(request.getPhone());
					enterpriseLeader.setExtensionNumber(request.getExtNumber());
					enterpriseLeader.setTitle(request.getTitle());
					enterpriseLeader.setCompanyPhone(request.getBusinessPhone());

					enterpriseLeaderJpaRepo.save(enterpriseLeader);
				}

			} else if (userAccount.getType().equalsIgnoreCase("CLIENT")) {
				if (userAccount.getRole().getName().equalsIgnoreCase("Client")) {
					ClientContact clientContact = clientContactJpaRepo.findByUuid(userUuid);

					clientContact.setEmail(request.getEmail());
					clientContact.setFirstName(request.getFirstName());
					clientContact.setLastName(request.getLastName());
					clientContact.setPhone(request.getPhone());
					clientContact.setExtensionNumber(request.getExtNumber());
					clientContact.setTitle(request.getTitle());
					clientContact.setBusinessPhone(request.getBusinessPhone());

					clientContactJpaRepo.save(clientContact);
				} else {
					ClientUser clientUser = clientUserJPARepo.findByUuid(userUuid);
//					clientUser.setUserUUID(userUUID);
					clientUser.setEmail(request.getEmail());
					clientUser.setFirstName(request.getFirstName());
					clientUser.setLastName(request.getLastName());
					clientUser.setPhone(request.getPhone());
//					profileDTO.setRoleId(userAccount.getRole().getId());
//					profileDTO.setRoleName(userAccount.getRole().getName());
					clientUser.setExtentionNumber(request.getExtNumber());
					clientUser.setTitle(request.getTitle());
					clientUser.setCompanyPhone(request.getBusinessPhone());

					clientUserJPARepo.save(clientUser);
				}

			} else if (userAccount.getType().equalsIgnoreCase("PROVIDER")) {
				Provider provider = providerJpaRepo.findByUuid(userUuid);

				provider.setEmail(request.getEmail());
				provider.setFirstName(request.getFirstName());
				provider.setLastName(request.getLastName());
				provider.setPhone(request.getPhone());
				provider.setExtensionNumber(request.getExtNumber());
				provider.setTitle(request.getTitle());
				provider.setBusinessPhone(request.getBusinessPhone());

				providerJpaRepo.save(provider);
			}

			return new ResponseEntity(null, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'updateProfile' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<UserAccount> updateProfilePassword(UUID userUuid, String oldPassword, String newPassword) {
		try {
			Map<String, Object> map = new HashMap<>();

			if (userAccountJpaRepo.existsByUserUuidAndPassword(userUuid, oldPassword)) {
				UserAccount userAccount = userAccountJpaRepo.findByUserUuid(userUuid);

				userAccount.setPassword(newPassword);
				userAccountJpaRepo.save(userAccount);

				map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.SUCCESS_UPDATED);
				map.put(ConstantExtension.RETURN_STATUS_KEY, true);

				return new ResponseEntity(map, HttpStatus.OK);
			} else {
				map.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.INVALID_USER_OR_PASSWORD);
				map.put(ConstantExtension.RETURN_STATUS_KEY, false);
				return new ResponseEntity(map, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'updateProfilePassword' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<UserDetailsDTO> getUserDetail(RequestInterceptorResponse req) {
		try {

			Map<String, Object> map = new HashMap<>();
			UserDetailsDTO dto = new UserDetailsDTO();

			String userName = "";
			String userEmail = "";

			if (req.getRoleType().equalsIgnoreCase("ENTERPRISE")) {
				if (req.getRole().equalsIgnoreCase("Super Admin")) {
					Enterprise enterprise = enterpriseJpaRepository.findByUuid(req.getUserUuid());
					if (enterprise != null) {
						userName = enterprise.toString();
						userEmail = enterprise.getEmail();
					}
				} else {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo.findByUuid(req.getUserUuid());
					if (enterpriseLeader != null) {
						userName = enterpriseLeader.toString();
						userEmail = enterpriseLeader.getEmail();
					}
				}
			} else if (req.getRoleType().equalsIgnoreCase("CLIENT")) {

				if (req.getRole().equalsIgnoreCase("Client")) {
					ClientContact clientContact = clientContactJpaRepo.findByUuid(req.getUserUuid());
					if (clientContact != null) {
						userName = clientContact.toString();
						userEmail = clientContact.getEmail();
					}
				} else {
					ClientUser clientUser = clientUserJPARepo.findByUuid(req.getUserUuid());
					if (clientUser != null) {
						userName = clientUser.toString();
						userEmail = clientUser.getEmail();
					}
				}

			} else if (req.getRoleType().equalsIgnoreCase("PROVIDER")) {
				Provider provider = providerJpaRepo.findByUuid(req.getUserUuid());
				if (provider != null) {
					userName = provider.toString();
					userEmail = provider.getEmail();
				}

			}

			dto.setName(userName);
			dto.setEmail(userEmail);

			map.put(ConstantExtension.RETURN_OBJECT_KEY, dto);
			map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.SUCCESS);
			map.put(ConstantExtension.RETURN_STATUS_KEY, true);
			return new ResponseEntity(map, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'updateProfilePassword' : " + e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> updatePassword(String userName, String password) {
		try {
			UserAccount userAccount = userAccountJpaRepo.findTopOneByUserNameAndIsFlag(userName, 1);
			if (userAccount != null) {
				userAccount.setPassword(password);
				userAccountJpaRepo.save(userAccount);

				return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_UPDATED), HttpStatus.OK);
			} else {

				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.USER_NOT_EXIST), HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'updatePassword' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<Map<String, Object>> getEnterpriseLeaders(){
		
		try {
			Map<String, Object> map = new HashMap<>();
			List<UserAccountReflection> userAccounts = null;
			
			userAccounts = userAccountJpaRepo.findByEnterpirseLeader();
			
			map.put(ConstantExtension.RETURN_STATUS_KEY, true);
			map.put(ConstantExtension.RETURN_MESSAGE_KEY, ConstantExtension.FETCHED);
			map.put(ConstantExtension.RETURN_OBJECT_KEY, userAccounts);
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'getEnterpriseLeaders' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public ResponseEntity<Object> checkDuplicateUser(String userName) {
		try {

			if (userAccountJpaRepo.existsOneByUserNameAndIsFlag(userName, 1)) {
				return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.USER_EXIST), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.USER_NOT_EXIST), HttpStatus.OK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("USER ACCOUNT HANDLER :: ERROR IN METHOD 'getForgetPasswordOTP' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	

}
