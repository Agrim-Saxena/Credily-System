package com.credv3.caqh;

import lombok.Data;

public @Data class QueryNPIResponse {
	private String organization_id;
	private String caqh_provider_id;
	private String roster_status;
	private String authorization_flag;
	private String provider_status;
	private String provider_status_date;
	private String provider_practice_state;
	private String provider_found_flag;
}
