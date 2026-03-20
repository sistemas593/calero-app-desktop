package com.calero.lili.core.apiSitac.repositories;

import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdMailsEnviadosRepository extends JpaRepository<AdMailEnviadosEntity, Long> {

}
