package com.calero.lili.api.modCxP.XpPagos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface XpPagosRepository extends JpaRepository<XpPagosEntity, UUID> {

    @Query("SELECT c FROM XpPagosEntity c WHERE c.idPagoGrupo = :idPagoGrupo")
    List<XpPagosEntity> getAllForFindByIdPagoGrupo(@Param("idPagoGrupo") UUID idPagoGrupo);



}
