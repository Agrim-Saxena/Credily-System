package com.credv3.caqh;

import lombok.Data;

public @Data class RosterResult {
	private String po_provider_uuid;
	private String organization_id;
	private String delegation_flag;
	private String affiliation_flag;
	private String exception_description;
}
