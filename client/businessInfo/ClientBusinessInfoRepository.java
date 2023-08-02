package com.credv3.client.businessInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.helper.DatabaseHelper;

@Repository
public interface ClientBusinessInfoRepository {

	List<ClientBusinessInfo> get(DatabaseHelper databaseHelper, List<UUID> ownerUuid, UUID clientUuid);

	Long getCount(DatabaseHelper databaseHelper, List<UUID> ownerUuid, UUID clientUuid);
}
