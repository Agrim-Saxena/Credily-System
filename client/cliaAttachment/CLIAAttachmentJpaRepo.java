package com.credv3.client.cliaAttachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.CLIAAttachment;

public interface CLIAAttachmentJpaRepo extends JpaRepository<CLIAAttachment,Long> {

	@Modifying
	@Transactional
	void deleteByClientBusinessLocation_Id(long id);

}
