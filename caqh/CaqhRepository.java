package com.credv3.caqh;

import org.springframework.data.jpa.repository.JpaRepository;

import com.credv3.common.entities.Caqh;

public interface CaqhRepository extends JpaRepository<Caqh, Long>{

	Caqh findByNpi(String npi);

	Caqh findByCaqhId(String caqhId);

}
