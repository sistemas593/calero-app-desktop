package com.calero.lili.core.apiSitac.repositories;

import com.calero.lili.core.apiSitac.repositories.entities.StRecibidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StRecibidasRepository extends JpaRepository<StRecibidaEntity, Long> {
    @Query(value = "SELECT entity " +
            "FROM StRecibidaEntity entity " +
            "where entity.numeroAutorizacion = :numeroAutorizacion")
    Optional<StRecibidaEntity> findByNumeroAutorizacion(@Param("numeroAutorizacion") String numeroAutorizacion);

}
