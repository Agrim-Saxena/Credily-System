package com.credv3.client.taxonomy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.helper.RequestInterceptorResponse;

@RestController
@RequestMapping("/client-taxonomy")
public class ClientTaxonomyController {

	@Autowired
	private ClientTaxonomyHandler clientTexonomyHandler;
	
	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getAllClientInfo(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "search", defaultValue = "", required = false) String search) {
		return clientTexonomyHandler.getClientTaxonomy(search);
	}
	
}
