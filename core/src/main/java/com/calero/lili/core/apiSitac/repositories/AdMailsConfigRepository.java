package com.calero.lili.core.apiSitac.repositories;

import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdMailsConfigRepository extends JpaRepository<AdMailConfigEntity, Long> {

    AdMailConfigEntity findByIdConfig(Long idConfig);

}
