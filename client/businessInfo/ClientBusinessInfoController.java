package com.credv3.client.businessInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.expirable.ExpirableDataRequest;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;

@RestController
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientBusinessInfoController {

	@Autowired
	private ClientBusinessInfoHandler clientHandler;

	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public ResponseEntity<Object> createClientBusinessInfo(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestBody ClientBusinessInfoRequest request) {
		return clientHandler.createClientBusinessInfo(request, req);
	}

	@RequestMapping(value = "/all", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getAllClientBusinessInfo() {
		return clientHandler.getAllClientBusinessInfo();
	}
	
	@RequestMapping(value = "/by-page", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getAllClientBusinessInfo1(@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "search", defaultValue = "", required = false) String search,
			@RequestParam(value = "searchBy", defaultValue = "", required = false) String searchBy,
			@RequestParam(value = "currentPage") int currentPage,
			@RequestParam(value = "itemsPerPage") int itemsPerPage,
			@RequestParam(value = "sortBy", defaultValue = "createdDate", required = false) String sortBy,
			@RequestParam(value = "sortOrder", defaultValue = "desc", required = false) String sortOrder) {

		DatabaseHelper databaseHelper = new DatabaseHelper(search, searchBy, currentPage, itemsPerPage, sortBy,
				sortOrder);
		return clientHandler.getAllClientBusinessInfo1(databaseHelper, req);
	}

	@RequestMapping(value = "/delete-client", method = { RequestMethod.OPTIONS, RequestMethod.DELETE })
	public ResponseEntity<Object> deleteClientBusinessInfo(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(name = "clientUuid") UUID clientUuid) {
		return clientHandler.deleteClientInfo(clientUuid);
	}

	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getAllClientInfo(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "legalBusinessName") String bussinessName) {
		return clientHandler.getAllClientInfo(bussinessName,req);
	}

	@RequestMapping(value = "/owner", method = { RequestMethod.OPTIONS, RequestMethod.PUT })
	public ResponseEntity<Map<String, Object>> getAllClientInfo(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "clientUUID") UUID clientUUID,
			@RequestParam(value = "ownerUUID") UUID ownerUUID) {
		return clientHandler.updateClientOwner(clientUUID, ownerUUID, req);
	}
	
	@RequestMapping(value = "/by-uuid", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<ClientBusinessInfoDTO> getClientBusinessInfo(
			@RequestParam(name = "clientUUID") UUID clientUuid) {
		return clientHandler.getClientBusinessInfo(clientUuid);
	}
	
	
	@RequestMapping(value = "/expirable", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Map<String, Object>> getClientExpirable(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "stateNameList", defaultValue = "", required = false) List<String> stateNameList,
			@RequestParam(value = "providerUuids", defaultValue = "", required = false) List<UUID> providerUuids,
			@RequestParam(value = "specialty", defaultValue = "", required = false) String specialty,
			@RequestParam(value = "providerAddress", defaultValue = "", required = false) String providerAddress,
			@RequestParam(value = "docType", defaultValue = "", required = false) String docType,
			@RequestParam(name = "type", required = false, defaultValue = "") String type,
			@RequestParam(value = "search", defaultValue = "", required = false) String search,
			@RequestParam(value = "searchBy", defaultValue = "", required = false) String searchBy,
			@RequestParam(value = "currentPage", required = false,defaultValue = "0") int currentPage,
			@RequestParam(value = "itemsPerPage", required = false,defaultValue = "10") int itemsPerPage,
			@RequestParam(value = "sortBy", defaultValue = "createdDate", required = false) String sortBy,
			@RequestParam(value = "sortOrder", defaultValue = "desc", required = false) String sortOrder,
			@RequestParam(name = "isGroupByProvider", defaultValue = "0", required = false) int isGroupByProvider) {

		DatabaseHelper databaseHelper = new DatabaseHelper(search, searchBy, currentPage, itemsPerPage, sortBy,
				sortOrder);
		ExpirableDataRequest expirableDataRequest = new ExpirableDataRequest();
		expirableDataRequest.setClientBusinessUuid(req.getClientBusinessUuid());
		expirableDataRequest.setProviderUuid(req.getProviderUuid());
		expirableDataRequest.setProviderUuids(providerUuids);
		expirableDataRequest.setType(type);
		expirableDataRequest.setStateNameList(stateNameList);
		expirableDataRequest.setDatabaseHelper(databaseHelper);
		expirableDataRequest.setSpecialty(specialty);
		expirableDataRequest.setIsGroupByProvider(isGroupByProvider);
		expirableDataRequest.setDocType(docType);
		expirableDataRequest.setProviderAddress(providerAddress);
		return clientHandler.getClientExpirable(expirableDataRequest, req);
	
	}
	
}
