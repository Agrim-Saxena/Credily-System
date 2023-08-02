package com.credv3.client.contact;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientContact;

public interface ClientContactJPARepo extends JpaRepository<ClientContact, UUID>{

	ClientContact findByUuid(UUID userUUID);

	@Modifying
	@Transactional
	void deleteAllByUuidIn(List<UUID> uuids);

}
