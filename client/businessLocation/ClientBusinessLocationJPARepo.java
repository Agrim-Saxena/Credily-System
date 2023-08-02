package com.credv3.client.businessLocation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessLocation;

public interface ClientBusinessLocationJPARepo extends JpaRepository<ClientBusinessLocation, Long>{

	ClientBusinessLocation findByClientBusinessInfo_Uuid(UUID uuid);

	ClientBusinessLocation findByClientBusinessInfo_UuidAndIsFlag(UUID uuid, int i);

	List<ClientBusinessLocation> findAllByClientBusinessInfo_Uuid(UUID clientUuid);

	@Modifying
	@Transactional
	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

}
