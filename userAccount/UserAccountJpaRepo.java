package com.credv3.userAccount;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.UserAccount;

public interface UserAccountJpaRepo extends JpaRepository<UserAccount, Long> {

	UserAccount findTopOneByUserName(String email);

	boolean existsOneByUserName(String email);

	boolean existsOneByUserNameAndIsFlag(String userName, int i);

	UserAccount findTopOneByUserNameAndIsFlag(String userName, int i);

	UserAccount findTopOneByUserNameAndPasswordAndIsFlag(String userName, String password, int i);

	UserAccount findTopOneByUuid(UUID uuid);

	UserAccount findByUuid(UUID userUUID);

	UserAccount UserUuid(UUID leaderUUID);

	@Query(value = "select ua.uuid as UUID,ua.user_uuid as UserUUID,CONCAT(el.first_name,' ',el.last_name) as FullName from user_account ua left join enterprise_leader el on ua.user_uuid = el.enterprise_leader_uuid where ua.role_id =6", nativeQuery = true)
	List<UserAccountReflection> findByEnterpirseUser();
	
	@Query(value = "select ua.uuid as UUID,ua.user_uuid as UserUUID,CONCAT(el.first_name,' ',el.last_name) as FullName from user_account ua left join enterprise_leader el on ua.user_uuid = el.enterprise_leader_uuid where ua.role_id =6 AND ua.user_uuid IN (?1)", nativeQuery = true)
	List<UserAccountReflection> findByEnterpirseUserAndUuids(List<String> userUuids);

	UserAccount findByUserUuid(UUID userUUID);

	boolean existsByUserUuidAndPassword(UUID userUuid, String oldPassword);
	
	@Query(value = "select ua.uuid as UUID,ua.user_uuid as UserUUID,CONCAT(el.first_name,' ',el.last_name) as FullName from user_account ua left join enterprise_leader el on ua.user_uuid = el.enterprise_leader_uuid where ua.role_id =2", nativeQuery = true)
	List<UserAccountReflection> findByEnterpirseLeader();

	@Modifying
	@Transactional
	void deleteByUserUuid(UUID uuid);

	boolean existsOneByRole_Id(long roleId);

	UserAccount findTopOneByUserNameAndPasswordAndTypeAndIsFlag(String userName, String password, String provider, int i);

	@Query(value = "SELECT ua.uuid FROM user_account ua WHERE ua.user_uuid = ?1", nativeQuery = true)
	String getAccountUuid(String useruuid);

}
