package com.calero.lili.core.modRRHH.modRRHHParametros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RhParametrosCargaRepository extends JpaRepository<RhRolParametrosCargasEntity, UUID> {

    @Query(value = "select * from rh_rol_parametro_cargas rrpc where rrpc.id_tercero =:idTercero", nativeQuery = true)
    List<RhRolParametrosCargasEntity> findByAllTercero(@Param("idTercero") UUID idTercero);

}
