package com.credv3.attachment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.Attachment;
import com.credv3.passport.PassportSearchReflection;
import com.credv3.passport.ProviderPassportAttachmentsReflection;
import com.credv3.passport.ProviderPassportAttachmentsReflection;

public interface AttachmentJpaRepository extends JpaRepository<Attachment, Long>{

	List<Attachment> findTop10ByProvider_Uuid(UUID providerUUID);

	@Modifying
	@Transactional
	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

	List<Attachment> findAllByClientBusinessInfoUuid(UUID clientUuid);

	void deleteByProvider_Uuid(UUID uuid);
	
	@Query(value="SELECT a.attachment_id as id , a.file_type as fileType FROM attachment a where a.provider_uuid = ?1  ORDER BY a.file_type ASC Limit ?2, ?3", nativeQuery=true)
	List<ProviderPassportAttachmentsReflection> getProviderPassportAttachment(String Uuid, int offset, int limit);
	
	@Query(value="SELECT COUNT(*) FROM attachment a where a.provider_uuid = ?1", nativeQuery=true)
	long getProviderPassportAttachmentCount(String uuid);
	
	@Query(value="SELECT a.attachment_id as attachmentId FROM attachment a WHERE a.provider_uuid = ?1", nativeQuery=true)
	List<Long> getProviderPassportAttachmentId(String Uuid);
	
	@Query(value="SELECT a.file_type as fileType FROM attachment a WHERE a.attachment_id = ?1", nativeQuery=true)
	String getProviderPassportAttachment(long id);	
}
