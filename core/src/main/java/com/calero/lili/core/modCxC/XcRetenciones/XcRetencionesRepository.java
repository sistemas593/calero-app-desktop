package com.calero.lili.core.modCxC.XcRetenciones;

import com.calero.lili.core.modCxC.XcPagos.XcPagosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface XcRetencionesRepository extends JpaRepository<XcPagosEntity, UUID> {

    @Query("SELECT c FROM XcPagosEntity c WHERE c.idPagoGrupo = :idPagoGrupo")
    List<XcPagosEntity> getAllForFindByIdPagoGrupo(@Param("idPagoGrupo") UUID idPagoGrupo);



}
