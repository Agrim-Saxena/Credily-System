package com.credv3.client.businessInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.HelperExtension;

@Component
public class ClientBusinessInfoRepositoryImpl implements ClientBusinessInfoRepository {

	@Autowired
	private EntityManager entityManager;
	HelperExtension helperExtension = new HelperExtension();

	@Override
	public List<ClientBusinessInfo> get(DatabaseHelper databaseHelper, List<UUID> ownerUuids, UUID clientUuid) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ClientBusinessInfo> criteriaQuery = criteriaBuilder.createQuery(ClientBusinessInfo.class);
		Root<ClientBusinessInfo> root = criteriaQuery.from(ClientBusinessInfo.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		
		if (!helperExtension.isNullOrEmpty(clientUuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("uuid"), clientUuid));
		}
		
		if(ownerUuids!=null && ownerUuids.size()>0)  {
			predicateList.add(root.get("owner").get("userUuid").in(ownerUuids));
		}

		predicateList.add(criteriaBuilder.equal(root.get("isFlag"), 1));

		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {

				predicateList.add(criteriaBuilder.like(root.get("legalBusinessName").as(String.class),
						"%" + databaseHelper.getSearch() + "%"));

			}
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSortOrder())
					&& !helperExtension.isNullOrEmpty(databaseHelper.getSortBy())) {
				// Sorting Starts
				if (databaseHelper.getSortOrder().equalsIgnoreCase("asc")) {
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get(databaseHelper.getSortBy())));
				} else if (databaseHelper.getSortOrder().equalsIgnoreCase("desc")) {
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get(databaseHelper.getSortBy())));
				}
			}
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
			// Pagination Starts
			if (databaseHelper != null && databaseHelper.getCurrentPage() != 0
					&& databaseHelper.getItemsPerPage() != 0) {
				final TypedQuery<ClientBusinessInfo> typedQuery = entityManager.createQuery(criteriaQuery);
				typedQuery.setFirstResult((databaseHelper.getCurrentPage() - 1)
						* databaseHelper.getItemsPerPage());
				typedQuery.setMaxResults(databaseHelper.getItemsPerPage());
				return typedQuery.getResultList();
			}
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	public Long getCount(DatabaseHelper databaseHelper, List<UUID> ownerUuids, UUID clientUuid) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<ClientBusinessInfo> root = criteriaQuery.from(ClientBusinessInfo.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		criteriaQuery.select(criteriaBuilder.count(root));

		if (!helperExtension.isNullOrEmpty(clientUuid)) {
			predicateList.add(criteriaBuilder.equal(root.get("uuid"), clientUuid));
		}
		
		if(ownerUuids!=null && ownerUuids.size()>0)  {
			predicateList.add(root.get("owner").get("userUuid").in(ownerUuids));
		}

		predicateList.add(criteriaBuilder.equal(root.get("isFlag"), 1));
		
		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {

				predicateList.add(criteriaBuilder.like(root.get("legalBusinessName").as(String.class),
						"%" + databaseHelper.getSearch() + "%"));

			}
			
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}
		return entityManager.createQuery(criteriaQuery).getSingleResult();

	}
}
