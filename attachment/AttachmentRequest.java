package com.credv3.attachment;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

public @Data class AttachmentRequest {

	private long id;
	private String fileName;
	private String fileType;
	private String fileUrl;
	private Date issueDate;
	private Date expirationDate;
	private String description;
	private UUID providerUUID;
	private int isTrackable;
	private String stateName;
	private String stateCode;
}
