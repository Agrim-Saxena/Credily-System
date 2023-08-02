package com.credv3.attachment;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.customAnnotation.RequestInterceptor;
import com.credv3.helper.ConstantKey;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;

@RestController
@RequestMapping(value = "/attachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentController {

	@Autowired
	private AttachmentHandler attachmentHandler;

	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public ResponseEntity<Object> createAttachement(@RequestBody AttachmentRequest request,
			@RequestInterceptor("value") RequestInterceptorResponse req) {
		return attachmentHandler.createAttachment(request, req);
	}

	@RequestMapping(value = "/provider", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getProviderAttachement(@RequestParam(name = "providerUUID") UUID providerUUID) {
		return attachmentHandler.getProviderAttachment(providerUUID);
	}

	@RequestMapping(value = "/by-page", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> getPagination(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(name = "providerUUID", required = false) UUID providerUUID,
			@RequestParam(name = "type", required = false, defaultValue = "") String type,
			@RequestParam(name = "isTrackable", required = false, defaultValue = "0") int isTrackable,
			@RequestParam(value = "search", defaultValue = "", required = false) String search,
			@RequestParam(value = "searchBy", defaultValue = "", required = false) String searchBy,
			@RequestParam(value = "currentPage", required = true) int currentPage,
			@RequestParam(value = "itemsPerPage", required = true) int itemsPerPage,
			@RequestParam(value = "sortBy", defaultValue = "", required = false) String sortBy,
			@RequestParam(value = "sortOrder", defaultValue = "", required = false) String sortOrder) {
		DatabaseHelper databaseHelper = new DatabaseHelper(search, searchBy, currentPage, itemsPerPage, sortBy,
				sortOrder);
		if(req.getRole().equalsIgnoreCase(ConstantKey.PROVIDER)) {
			providerUUID = req.getUserUuid();
		}
		return attachmentHandler.findByUUID(providerUUID, databaseHelper,req, type,isTrackable);
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.OPTIONS, RequestMethod.DELETE })
	public ResponseEntity<Object> deleteByAttachmentId(
			@RequestInterceptor("value") RequestInterceptorResponse req,
			@RequestParam(value = "id", defaultValue = "", required = false) long id) {
		return attachmentHandler.deleteByAttachmentId(id);
	}

	@RequestMapping(value = "/update", method = { RequestMethod.OPTIONS, RequestMethod.POST })
	public ResponseEntity<Object> updateAttachment(@RequestBody AttachmentRequest attachmentrequest) {
		return attachmentHandler.updateAttachment(attachmentrequest);
	}

	@RequestMapping(value = "/id", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> findAttachmentById(
			@RequestParam(value = "id", defaultValue = "", required = false) long id) {
		return attachmentHandler.findAttachmentById(id);
	}

	@RequestMapping(value = "/otp", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> setAttachmentOtp(@RequestParam("id") long id) throws Exception {
		return attachmentHandler.saveOtp(id);
	}

	@RequestMapping(value = "/match-otp", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> matchOtp(@RequestParam("id") long id, @RequestParam("otp") String otp) throws Exception {
		return attachmentHandler.matchOtp(id, otp);
	}
	
	@RequestMapping(value = "/get-otp", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public ResponseEntity<Object> matchOtp(@RequestParam("id") long id) {
		return attachmentHandler.getOtp(id);
	}
	

}
