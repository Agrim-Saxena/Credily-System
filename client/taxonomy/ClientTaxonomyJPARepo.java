package com.credv3.client.taxonomy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.credv3.common.entities.ClientTaxonomy;

public interface ClientTaxonomyJPARepo extends JpaRepository<ClientTaxonomy, Long> {

	List<ClientTaxonomy> findTop10BydisplayNameContaining(String search);

}
