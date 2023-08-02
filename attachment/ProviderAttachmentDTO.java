package com.credv3.attachment;

import com.credv3.common.entities.Status;

import lombok.Data;

public @Data class ProviderAttachmentDTO {

	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String npi;
	private Status status;
	private String specialty;
}
