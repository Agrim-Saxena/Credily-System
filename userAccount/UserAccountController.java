package com.credv3.userAccount;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.common.entities.UserAccount;
import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.helper.RequestInterceptorResponse;

@RestController
@RequestMapping(value = "/user-account", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAccountController {

	@Autowired
	private UserAccountHandler userAccountHandler;

	// open API
	@RequestMapping(value = "/login", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> loginRequest(
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = false) String password) {
		return userAccountHandler.loginUser(username, password);
	}

	@RequestMapping(value = "/refresh-token", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getIdTokenFromRefreshToken(
			@RequestParam(value = "refreshToken", required = false) String refreshToken) {
		return userAccountHandler.getIdTokenFromRefreshToken(refreshToken);
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> logoutRequest(
			@RequestInterceptor("value") RequestInterceptorResponse req ) {
		return userAccountHandler.logoutRequest(req);
	}

	// open API
	@RequestMapping(value = "/otp/{username}", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getForgetPasswordOTP(@PathVariable(value = "username") String emailId) {
		return userAccountHandler.getForgetPasswordOTP(emailId);
	}

	// open API
	@RequestMapping(value = "/verify/otp", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> verifyForgetPasswordOTP(
			@RequestParam(value = "username") String userName, 
			@RequestParam(value = "otp") String otp)
			throws ExecutionException {
		return userAccountHandler.verifyForgetPasswordOTP(userName, otp);
	}

	@RequestMapping(value = "/reset", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public ResponseEntity<Object> resetFirstPassword(@RequestParam(value = "newPassword") String userName,
			@RequestParam(value = "userUUID") UUID userUUID) throws ExecutionException {
		return userAccountHandler.resetFirstPassword(userName, userUUID);
	}

	@RequestMapping(value = "/update-token", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public ResponseEntity<Object> updateToken(@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "clientBusinessUuid", required = false) String clientBusinessUuid,
			@RequestParam(value = "providerUuid", required = false) String providerUuid) throws ExecutionException {
		return userAccountHandler.updateToken(clientBusinessUuid, providerUuid, req);
	}

//	 adding user on firebase manually
	@RequestMapping(value = "/token", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public Boolean createToken(@RequestParam(value = "uuid") String uuid,
			@RequestParam(value = "userUuid") String userUuid,
			@RequestParam(value = "clientBusinessUuid", required = false) String clientBusinessUuid,
			@RequestParam(value = "email") String email, @RequestParam(value = "role") String role,
			@RequestParam(value = "type") String roleType) throws ExecutionException {

		return userAccountHandler.saveUserOnFirebase(uuid, userUuid, clientBusinessUuid, email, role, roleType);
	}

	@RequestMapping(value = "/{type}", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Map<String, Object>> getUsers(@RequestInterceptor("value") RequestInterceptorResponse req,
			@PathVariable(value = "type") String type) {
		return userAccountHandler.getUsers(type, req.getRole(), req.getUserUuid());
	}
	
	@RequestMapping(value="", method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<ProfileDTO> getProfileData(
			@RequestInterceptor("value") RequestInterceptorResponse req){
		return userAccountHandler.getUserProfileData(req.getUserUuid());
	}
	
	@RequestMapping(value="", method = {RequestMethod.OPTIONS,RequestMethod.PATCH})
	public ResponseEntity<ProfileDTO> updateProfile(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestBody ProfileDTO request){
		return userAccountHandler.updateProfile(req.getUserUuid(), request);
	}
	
	@RequestMapping(value="/password-update", method = {RequestMethod.OPTIONS,RequestMethod.PATCH})
	public ResponseEntity<UserAccount> updateProfilePassword(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(name = "oldPassword") String oldPassword,
			@RequestParam(name = "newPassword") String newPassword){
		return userAccountHandler.updateProfilePassword(req.getUserUuid(), oldPassword, newPassword);
	}
	
	@RequestMapping(value="/details", method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<UserDetailsDTO> getUserDetail(
			@RequestInterceptor("value") RequestInterceptorResponse req){
		return userAccountHandler.getUserDetail(req);
	}
	
	@RequestMapping(value = "/update-user-password", method = { RequestMethod.OPTIONS, RequestMethod.PATCH})
	public ResponseEntity<Object> updatePassword(
			@RequestParam(value = "username") String userName, 
			@RequestParam(value = "password") String password)
		throws ExecutionException {
			return userAccountHandler.updatePassword(userName, password);
	}
	
	@RequestMapping(value = "/enterprise-leader", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Map<String, Object>> getEnterpriseLeaders() {
		return userAccountHandler.getEnterpriseLeaders();
	}
	
	@RequestMapping(value = "/duplicate/{username}", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> checkDuplicateUser(@PathVariable(value = "username") String emailId) {
		return userAccountHandler.checkDuplicateUser(emailId);
	}

}
