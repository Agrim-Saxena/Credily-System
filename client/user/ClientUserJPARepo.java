package com.credv3.client.user;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientUser;
import com.credv3.userAccount.UserAccountReflection;

public interface ClientUserJPARepo extends JpaRepository<ClientUser, UUID>{

	ClientUser findByUuid(UUID userUUID);
	
	@Query(value = "SELECT ua.uuid as UUID,ua.user_uuid as UserUUID,CONCAT(cu.first_name,' ',cu.last_name) as FullName from user_account ua left join client_user cu on ua.user_uuid = cu.client_user_uuid  where ua.role_id =4 AND cu.client_contact_uuid = ?1 ", nativeQuery = true)
	List<UserAccountReflection> findClientUserByClient(String clientUuid);

	ClientUser findTop1ByUuid(UUID userUuid);

	@Modifying
	@Transactional
	void deleteByClientBusinessInfo_Uuid(UUID clientUuid);

	@Query(value = "SELECT cu.client_business_info_uuid FROM client_user cu WHERE cu.client_contact_uuid = ?1", nativeQuery = true)
	String getClientBusinessInfoUuid(String uuid);
}
