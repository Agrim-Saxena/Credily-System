package com.credv3.client.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessContact;
import com.credv3.common.entities.ClientTaxonomy;
import com.credv3.common.entities.TaxonomyMap;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;
import com.credv3.provider.texonomy.TaxonomyMapResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientTaxonomyHandler {
	
	@Autowired
	private ClientTaxonomyJPARepo clientTaxonomyJPARepo;
	
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getClientTaxonomy(String search) {
		try {

			List<Object> respList = new ArrayList<>();
			
			List<ClientTaxonomy> taxonomies = clientTaxonomyJPARepo.findTop10BydisplayNameContaining(search);
			
			for(ClientTaxonomy taxonomie : taxonomies) {
				TaxonomyMapResp resp = new TaxonomyMapResp();
				resp.setId(taxonomie.getCode());
				resp.setItemName(taxonomie.getDisplayName());
				
				respList.add(resp);
			}
			Map<String, Object> map = new HashMap<>();
			map.put(ConstantExtension.RETURN_OBJECT_KEY, respList);
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientBusinessInfo' : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
}
