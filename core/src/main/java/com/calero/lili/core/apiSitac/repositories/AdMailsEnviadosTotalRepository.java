package com.calero.lili.core.apiSitac.repositories;

import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosTotalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AdMailsEnviadosTotalRepository extends JpaRepository<AdMailEnviadosTotalEntity, Long> {

    @Query(value = "SELECT e " +
            "FROM AdMailEnviadosTotalEntity e " +
            "where e.clave1 = :clave1 and " +
            "e.periodo = :periodo" )
    Optional<AdMailEnviadosTotalEntity> findByClaveAndPeriodo(String clave1, String periodo);

}
