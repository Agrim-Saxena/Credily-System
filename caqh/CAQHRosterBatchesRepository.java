package com.credv3.caqh;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.credv3.common.entities.CAQHRosterBatch;

public interface CAQHRosterBatchesRepository extends JpaRepository<CAQHRosterBatch, Long> {

//	CAQHRosterBatch findByProvider_CaqhId(String caqhId);

	CAQHRosterBatch findTop1ByProvider_Uuid(UUID providerUUID);

	List<CAQHRosterBatch> findByStatusNotIn(List<String> statusList);

	List<CAQHRosterBatch> findByStatusIn(List<String> statusList);

	@Modifying
	@Transactional
	void deleteByProvider_Uuid(UUID providerUUID);

}
