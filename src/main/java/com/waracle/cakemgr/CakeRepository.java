package com.waracle.cakemgr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.waracle.cakemgr.CakeEntity;

@Repository
public interface CakeRepository extends JpaRepository<CakeEntity, Long> {


}
