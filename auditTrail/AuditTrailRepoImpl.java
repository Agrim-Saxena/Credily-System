package com.credv3.auditTrail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.credv3.common.entities.Attachment;
import com.credv3.common.entities.AuditTrail;
import com.credv3.common.entities.Expirable;
import com.credv3.expirable.ExpirableDataRequest;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.HelperExtension;

@Component
public class AuditTrailRepoImpl implements AuditTrailRepository {

	@Autowired
	private EntityManager entityManager;
	HelperExtension helperExtension = new HelperExtension();

	@Override
	public List<AuditTrail> get(UUID providerUUID, DatabaseHelper databaseHelper,String actionFrom) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AuditTrail> criteriaQuery = criteriaBuilder.createQuery(AuditTrail.class);
		Root<AuditTrail> root = criteriaQuery.from(AuditTrail.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		if (!helperExtension.isNullOrEmpty(providerUUID)) {
			predicateList.add(criteriaBuilder.equal(root.get("provider").get("uuid"), providerUUID));
		}
		if (!helperExtension.isNullOrEmpty(actionFrom)) {
			predicateList.add(criteriaBuilder.equal(root.get("actionFrom"), actionFrom));
		}
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {
				
				if(!helperExtension.isNullOrEmpty(databaseHelper.getSearchBy())) {
					predicateList.add(criteriaBuilder.like(root.get(databaseHelper.getSearchBy()),
							"%" + databaseHelper.getSearch() + "%"));
				} else {
					predicateList.add(criteriaBuilder.like(root.get("searchTag"),
							"%" + databaseHelper.getSearch().trim() + "%"));
				}
				
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
			if (databaseHelper!=null && databaseHelper.getCurrentPage() != 0 && databaseHelper.getItemsPerPage() != 0) {
				final TypedQuery<AuditTrail> typedQuery = entityManager.createQuery(criteriaQuery);
				typedQuery.setFirstResult((databaseHelper.getCurrentPage() - 1) * databaseHelper.getItemsPerPage());
				typedQuery.setMaxResults(databaseHelper.getItemsPerPage());
				return typedQuery.getResultList();
			}
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}

		return entityManager.createQuery(criteriaQuery).getResultList();

	}

	@Override
	public Long getCount(UUID providerUUID, DatabaseHelper databaseHelper,String actionFrom) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<AuditTrail> root = criteriaQuery.from(AuditTrail.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();
		criteriaQuery.select(criteriaBuilder.count(root));

		if (!helperExtension.isNullOrEmpty(providerUUID)) {
			predicateList.add(criteriaBuilder.equal(root.get("provider").get("uuid"), providerUUID));
		}
		if (!helperExtension.isNullOrEmpty(actionFrom)) {
			predicateList.add(criteriaBuilder.equal(root.get("actionFrom"), actionFrom));
		}

		if (!helperExtension.isNullOrEmpty(databaseHelper)) {
			// Search Starts
			if (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {
				
				if(!helperExtension.isNullOrEmpty(databaseHelper.getSearchBy())) {
					predicateList.add(criteriaBuilder.like(root.get(databaseHelper.getSearchBy()),
							"%" + databaseHelper.getSearch() + "%"));
				} else {
					predicateList.add(criteriaBuilder.like(root.get("searchTag"),
							"%" + databaseHelper.getSearch().trim() + "%"));
				}
			}
			
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
		} else {
			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		}
		return entityManager.createQuery(criteriaQuery).getSingleResult();
	}
}
