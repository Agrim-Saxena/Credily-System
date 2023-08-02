package com.credv3.auditTrail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.credv3.common.entities.AuditTrail;
import com.credv3.common.entities.ClientContact;
import com.credv3.common.entities.ClientUser;
import com.credv3.common.entities.Enterprise;
import com.credv3.common.entities.EnterpriseLeader;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.UserAccount;
import com.credv3.helper.CentralService;
import com.credv3.helper.DatabaseHelper;

@Component
public class AuditTrailHandler extends CentralService {

	public ResponseEntity<AuditTrail> createAuditTrail(AuditTrailRequest request) {
		try {
			AuditTrailMapper auditTrailMapper = new AuditTrailMapper();

			AuditTrail auditTrail = auditTrailMapper.map(request);
			
			if (request.getUserAccountUuid() != null) {
				UserAccount userAccount=userAccountJpaRepo.findByUuid(request.getUserAccountUuid());
//				userAccount.setUuid(request.getUserAccountUuid());
				auditTrail.setUserAccount(userAccount);
			}
			
			String username = null;
			
			if (auditTrail.getUserAccount().getType().equalsIgnoreCase("ENTERPRISE")) {
				if (auditTrail.getUserAccount().getRole().getName().equalsIgnoreCase("Super Admin")) {
					Enterprise enterprise = enterpriseJpaRepository
							.findByUuid(auditTrail.getUserAccount().getUserUuid());

					username = enterprise.toString();
				} else {
					EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo
							.findByUuid(auditTrail.getUserAccount().getUserUuid());
					username = enterpriseLeader.toString();
				}

			} else if (auditTrail.getUserAccount().getType().equalsIgnoreCase("CLIENT")) {
				if (auditTrail.getUserAccount().getRole().getName().equalsIgnoreCase("Client")) {
					ClientContact clientContact = clientContactJpaRepo
							.findByUuid(auditTrail.getUserAccount().getUserUuid());
					
					username = clientContact.toString();

				} else {
					ClientUser clientUser = clientUserJPARepo.findByUuid(auditTrail.getUserAccount().getUserUuid());
				
					username = clientUser.toString();
				}

			} else if (auditTrail.getUserAccount().getType().equalsIgnoreCase("PROVIDER")) {
				Provider provider = providerJpaRepo.findByUuid(auditTrail.getUserAccount().getUserUuid());
				username = provider.toString();
			}
			String tag = auditTrail.getActionType() + " " + username+" "+ auditTrail.getDescription();
			auditTrail.setSearchTag(tag);
			auditTrail.setUserName(username);
			auditTrailJpaRepo.save(auditTrail);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	public ResponseEntity<AuditTrail> getAuditTrail(UUID providerUUID, DatabaseHelper databaseHelper,String actionFrom) {
		try {
			List<AuditTrail> auditTrails = auditTrailRepository.get(providerUUID, databaseHelper,actionFrom);
			Long totalItemsCount = auditTrailRepository.getCount(providerUUID, databaseHelper,actionFrom);
			AuditTrailMapper auditTrailMapper = new AuditTrailMapper();
			List<AuditTrailResponse> responseList = new ArrayList<>(auditTrails.size());
			for (AuditTrail auditTrail : auditTrails) {
				AuditTrailResponse auditTrailResponse = auditTrailMapper.map(auditTrail);
				if (auditTrail.getUserAccount().getType().equalsIgnoreCase("ENTERPRISE")) {
					if (auditTrail.getUserAccount().getRole().getName().equalsIgnoreCase("Super Admin")) {
						Enterprise enterprise = enterpriseJpaRepository
								.findByUuid(auditTrail.getUserAccount().getUserUuid());
						auditTrailResponse.setUsername(enterprise.toString());
					} else {
						EnterpriseLeader enterpriseLeader = enterpriseLeaderJpaRepo
								.findByUuid(auditTrail.getUserAccount().getUserUuid());
						auditTrailResponse.setUsername(enterpriseLeader.toString());
					}

				} else if (auditTrail.getUserAccount().getType().equalsIgnoreCase("CLIENT")) {
					if (auditTrail.getUserAccount().getRole().getName().equalsIgnoreCase("Client")) {
						ClientContact clientContact = clientContactJpaRepo
								.findByUuid(auditTrail.getUserAccount().getUserUuid());
						auditTrailResponse.setUsername(clientContact.toString());

					} else {
						ClientUser clientUser = clientUserJPARepo.findByUuid(auditTrail.getUserAccount().getUserUuid());
						auditTrailResponse.setUsername(clientUser.toString());
					}

				} else if (auditTrail.getUserAccount().getType().equalsIgnoreCase("PROVIDER")) {
					Provider provider = providerJpaRepo.findByUuid(auditTrail.getUserAccount().getUserUuid());
					auditTrailResponse.setUsername(provider.toString());
				}
				auditTrailResponse.setDescription(auditTrailResponse.getUsername() +" "+ auditTrailResponse.getActionType().toLowerCase()+ " "+auditTrailResponse.getDescription() );
				responseList.add(auditTrailResponse);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("list", responseList);
			map.put("totalItems", totalItemsCount);

			return new ResponseEntity(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
