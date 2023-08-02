package com.credv3.caqh;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.helper.RequestInterceptorResponse;

@RestController
@RequestMapping("/caqh")
public class CAQHController {
	
	@Autowired
	private CAQHHandler cAQHHandler;
	
	
	@GetMapping("/status")
	public ResponseEntity<Object> getCAQHStatus(@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(name = "providerUUID", required = false) UUID providerUUID) {
		if(req.getRole().equalsIgnoreCase("Provider")) {
			providerUUID = req.getUserUuid();
		}
		return cAQHHandler.getCAQHStatus(providerUUID);
	}

	
	
	/************************************* Testing Method Section Start ***************************************/
	
	
	@GetMapping("")
	public void getProfile(
			@RequestParam(name = "npi", required = false) String npi,
			@RequestParam(name = "providerUUID", required = false) UUID providerUUID) {
		cAQHHandler.queryProviderWithNPI(npi, providerUUID);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<Object> fetchProfile(
			@RequestParam(name = "providerUUID", required = false) UUID providerUUID) {
		return cAQHHandler.fetchProfile(providerUUID);
	}

	@GetMapping("/retry-caqh")
	public void fetchAllProviderCaqhData() {
		cAQHHandler.reUploadCaqhData();
	}
	
	
	
}
