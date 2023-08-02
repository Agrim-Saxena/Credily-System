package com.credv3.client.businessContact;

import java.util.UUID;

import lombok.Data;

public @Data class ClientBusinessContactRequest {

	private long id;
	private UUID clientBusinessInfoUuid;
	private UUID clientContactUuid;
}
