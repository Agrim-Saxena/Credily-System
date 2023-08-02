package com.credv3.client.businessAddress;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessAddress;

public interface ClientBusinessAddressJPARepo extends JpaRepository<ClientBusinessAddress, Long>{

	@Modifying
	@Transactional
	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

}
