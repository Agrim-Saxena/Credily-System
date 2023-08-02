package com.credv3.auditTrail;



import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.credv3.common.entities.AuditTrail;
import com.credv3.helper.DatabaseHelper;
@RestController
@RequestMapping(value = "/audit", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuditTrailController {
	@Autowired
	private AuditTrailHandler auditTrailHandler;
	
	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<AuditTrail> getProviderAttachement(
			@RequestParam(name = "providerUUID",defaultValue = "", required = false) UUID providerUUID,
			@RequestParam(value = "search", defaultValue = "", required = false) String search,
			@RequestParam(value = "searchBy", defaultValue = "searchTag", required = false) String searchBy,
			@RequestParam(value = "currentPage", required = true) int currentPage,
			@RequestParam(value = "itemsPerPage", required = true) int itemsPerPage,
			@RequestParam(value = "actionFrom") String actionFrom	){
		DatabaseHelper databaseHelper = new DatabaseHelper(search, searchBy, currentPage, itemsPerPage, null,null);
		return auditTrailHandler.getAuditTrail(providerUUID,databaseHelper,actionFrom);
	}
}
