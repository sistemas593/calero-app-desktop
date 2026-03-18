package com.calero.lili.core.modCxP.XpPagos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface XpPagosRepository extends JpaRepository<XpPagosEntity, UUID> {

    @Query("SELECT c FROM XpPagosEntity c " +
            "WHERE c.idPagoGrupo = :idPagoGrupo AND " +
            "c.idData = :idData AND c.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR c.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR c.createdBy = :usuario)")
    List<XpPagosEntity> getAllForFindByIdPagoGrupo(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("idPagoGrupo") UUID idPagoGrupo,
                                                   @Param("sucursal") String sucursal,
                                                   @Param("usuario") String usuario);


}
