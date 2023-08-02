package com.credv3.attachment;

import java.util.Date;

import lombok.Data;

public @Data class AttachmentDTO {

	private long id;
	private String fileName;
	private String fileUrl;
	private String fileType;
	private Date issueDate;
	private Date expirationDate;
	private String description;
	private int isTrackable;
	private String stateCode;
	private String stateName;
	private String otp;
	private ProviderAttachmentDTO provider;
	
}
