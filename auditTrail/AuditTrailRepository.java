package com.credv3.auditTrail;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.credv3.common.entities.AuditTrail;
import com.credv3.expirable.ExpirableDataRequest;
import com.credv3.helper.DatabaseHelper;

@Repository
public interface AuditTrailRepository {

	List<AuditTrail> get(UUID providerUUID, DatabaseHelper databaseHelper,String actionFrom);

	Long getCount(UUID providerUUID, DatabaseHelper databaseHelper,String actionFrom);

	
	
}
