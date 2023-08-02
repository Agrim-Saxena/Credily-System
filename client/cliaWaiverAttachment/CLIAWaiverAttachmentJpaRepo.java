package com.credv3.client.cliaWaiverAttachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.CLIAWaiverAttachment;

public interface CLIAWaiverAttachmentJpaRepo extends JpaRepository<CLIAWaiverAttachment, Long> {

	@Modifying
	@Transactional
	void deleteByClientBusinessLocation_Id(long id);

}
