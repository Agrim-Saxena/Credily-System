package com.credv3.client.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.common.entities.ClientUser;
import com.credv3.common.entities.EnterpriseLeader;
import com.credv3.common.entities.UserAccount;
import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.enterprise.leader.EnterpriseLeaderDetailsDTO;
import com.credv3.enterprise.leader.EnterpriseLeaderRequest;
import com.credv3.enterprise.user.EnterpriseUserDTO;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;

@RestController
@RequestMapping(value = "/client-user", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientUserController {

	@Autowired
	private ClientUserHandler clientUserHandler;
	
	@RequestMapping(value="", method = {RequestMethod.OPTIONS,RequestMethod.POST})
	public ResponseEntity<Object> createClientContact(@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestBody ClientUserRequest request){
		return clientUserHandler.createClientUser(request, req);
	}
	
	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getAllExpirables(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "search", defaultValue = "", required = false) String search,
			@RequestParam(value = "searchBy", defaultValue = "", required = false) String searchBy,
			@RequestParam(value = "currentPage", required = false,defaultValue = "0") int currentPage,
			@RequestParam(value = "itemsPerPage", required = false,defaultValue = "10") int itemsPerPage,
			@RequestParam(value = "sortBy", defaultValue = "createdDate", required = false) String sortBy,
			@RequestParam(value = "sortOrder", defaultValue = "desc", required = false) String sortOrder) {

		DatabaseHelper databaseHelper = new DatabaseHelper(search, searchBy, currentPage, itemsPerPage, sortBy, sortOrder);
		
		return clientUserHandler.getClientUser(req, databaseHelper);
	
	}
	
	@RequestMapping(value = "/{clientUserUUID}", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<ClientUserResp> getClientUserByUuid(
			@PathVariable(name = "clientUserUUID") UUID clientUserUUID) {
		return clientUserHandler.getClientUserByUuid(clientUserUUID);
	}

	@RequestMapping(value = "/status-update", method = { RequestMethod.OPTIONS, RequestMethod.PATCH })
	public ResponseEntity<ClientUser> updateClientUserStatus(
			@RequestParam(name = "clientUserUUID") UUID clientUserUUID, @RequestParam(name = "status") String status) {
		return clientUserHandler.updateClientUserStatus(clientUserUUID, status);
	}
	
	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.PATCH })
	public ResponseEntity<ClientUser> updateClientuser(@RequestBody ClientUserRequest request) {
		return clientUserHandler.updateClientuser(request);
	}
	
	@RequestMapping(value="/user",method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<Object> getUserByClient(@RequestInterceptor("value") RequestInterceptorResponse req){
		return clientUserHandler.getUserByClient(req);
	}
	
	@RequestMapping(value = "/owner", method = { RequestMethod.OPTIONS, RequestMethod.PUT })
	public ResponseEntity<Object> updateProviderOwner(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "providerUuid") UUID providerUuid,
			@RequestParam(value = "ownerUuid") UUID ownerUuid) {
		return clientUserHandler.updateProviderOwner(providerUuid, ownerUuid, req);
	}
	
	
	
	
}
