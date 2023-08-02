package com.credv3.auditTrail;

import java.util.UUID;

import lombok.Data;

public @Data class AuditTrailRequest {
private UUID userAccountUuid;
private UUID providerUuid;
private UUID clientBusinessUuid;
private long id;
private String description;
private String actionType;
private String actionFrom;;

}
