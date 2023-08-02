package com.credv3.auditTrail;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.AuditTrail;

public interface AuditTrailJpaRepo extends JpaRepository<AuditTrail, Long>{

	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

	@Modifying
	@Transactional
	void deleteByProvider_Uuid(UUID uuid);

}
