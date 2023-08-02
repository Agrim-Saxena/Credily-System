package com.credv3.caqh;

import java.util.List;

import lombok.Data;

public @Data class BatchStatusResponse {
	  private String batch_status;
	  private String batch_time;
	  private List<RosterResult> roster_result;
}
