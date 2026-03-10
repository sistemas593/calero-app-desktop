package com.calero.lili.api.modRRHH.modRRHHParametros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolParametrosRepository extends JpaRepository<RhRolParametrosEntity, UUID> {


    @Query(value = "select * from rh_rol_parametro rrp where rrp.id_tercero =:idTercero", nativeQuery = true)
    List<RhRolParametrosEntity> findByAllTercero(@Param("idTercero") UUID idTercero);


}
