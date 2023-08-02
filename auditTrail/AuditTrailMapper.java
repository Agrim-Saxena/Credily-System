package com.credv3.auditTrail;

import com.credv3.common.entities.AuditTrail;
import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.UserAccount;
import com.credv3.helper.CentralService;

public class AuditTrailMapper{

	public AuditTrail map(AuditTrailRequest request) {
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setActionType(request.getActionType());
		auditTrail.setDescription(request.getDescription());
		auditTrail.setActionFrom(request.getActionFrom());
	

		if (request.getProviderUuid() != null) {
			Provider provider = new Provider();
			provider.setUuid(request.getProviderUuid());
			auditTrail.setProvider(provider);
		}

		if (request.getClientBusinessUuid() != null) {
			ClientBusinessInfo clientBusinessInfo = new ClientBusinessInfo();
			clientBusinessInfo.setUuid(request.getClientBusinessUuid());
			auditTrail.setClientBusinessInfo(clientBusinessInfo);
		}
		return auditTrail;

	}

	public AuditTrailResponse map(AuditTrail entity) {
		AuditTrailResponse response=new AuditTrailResponse();
		response.setActionType(entity.getActionType());
		response.setCreatedDate(entity.getCreatedDate());
		response.setDescription(entity.getDescription());
		response.setActionFrom(entity.getActionFrom());
		return response;
	}
}
