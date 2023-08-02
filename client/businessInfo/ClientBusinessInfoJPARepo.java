package com.credv3.client.businessInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessInfo;

public interface ClientBusinessInfoJPARepo extends JpaRepository<ClientBusinessInfo, UUID>{

	List<ClientBusinessInfo> findAllByOrderByUuidDesc();

	List<ClientBusinessInfo> findAllByIsFlag(int i);

	ClientBusinessInfo findTopOneByUuid(UUID fromString);
	
	@Query(value="SELECT cbi.legal_business_name  FROM client_business_info cbi LEFT JOIN provider p ON cbi.client_business_info_uuid = p.client_business_info_uuid WHERE p.provider_uuid =?1",nativeQuery=true)
	String getBusinessName(String uuid);
	
	@Query(value = "select uuid from client_business_info WHERE client_business_info_owner_user_uuid = ?1", nativeQuery = true)
	List<UUID> findClientBusinessInfoUuids(String uuid);

	ClientBusinessInfo findTopOneByOwner_Uuid(UUID uid);

	ClientBusinessInfo findByUuid(UUID clientUuid);

	@Modifying
	@Transactional
	void deleteByUuid(UUID clientUuid);

}
