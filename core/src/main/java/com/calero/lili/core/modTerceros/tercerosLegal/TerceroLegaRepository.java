package com.calero.lili.core.modTerceros.tercerosLegal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TerceroLegaRepository extends JpaRepository<TerceroLegalEntity, UUID> {


    @Query(value = "SELECT entity.tercero.idTercero " +
            "FROM TerceroLegalEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa")
    List<UUID> findByAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa);


}
