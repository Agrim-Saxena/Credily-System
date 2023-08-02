package com.credv3.client.businessContact;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessContact;

public interface ClientBusinessContactJPARepo extends JpaRepository<ClientBusinessContact, Long> {

	ClientBusinessContact findByClientContact_Email(String from);

	ClientBusinessContact findTopOneByClientContact_Uuid(UUID userUuid);

//	List<ClientBusinessContact> findTop10ByClientBusinessInfo_LegalBusinessNameContaining(String bussinessName);

	List<ClientBusinessContact> findTop10ByClientContact_isPrimaryAndClientBusinessInfo_LegalBusinessNameContainingOrderByClientBusinessInfo_LegalBusinessNameAsc(
			int i, String bussinessName);

	List<ClientBusinessContact> findByClientContact_isPrimaryAndClientBusinessInfo_LegalBusinessNameContaining(int i,
			String bussinessName);

	List<ClientBusinessContact> findTop10ByClientContact_isPrimaryAndClientBusinessInfo_Owner_UserUuidAndClientBusinessInfo_LegalBusinessNameContainingOrderByClientBusinessInfo_LegalBusinessNameAsc(
			int i, UUID accountUuid, String bussinessName);

	ClientBusinessContact findTopOneByClientBusinessInfo_Uuid(UUID clientUuid);

	List<ClientBusinessContact> findTop10ByClientContact_isPrimaryAndClientBusinessInfo_Owner_UserUuidInAndClientBusinessInfo_LegalBusinessNameContaining(
			int i, List<UUID> userUuids, String bussinessName);

	@Modifying
	@Transactional
	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

	List<ClientBusinessContact> findByClientBusinessInfo_Uuid(UUID clientUuid);

	@Query(value = "SELECT cbc.client_contact_uuid FROM client_business_contact cbc WHERE cbc.client_business_info_uuid = ?1", nativeQuery = true)
	String getClientContactUuid(String useruuid);
	
	@Query(value = "SELECT cbc.client_business_info_uuid FROM client_business_contact cbc WHERE cbc.client_contact_uuid = ?1", nativeQuery = true)
	String getClientBusinessInfoUuid(String uuid);
	
}
