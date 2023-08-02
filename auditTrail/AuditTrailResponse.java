package com.credv3.auditTrail;

import java.util.Date;

import lombok.Data;

public  @Data  class  AuditTrailResponse {
private String username;
private Date createdDate;
private String description;
private String actionType;
private String actionFrom;
}
