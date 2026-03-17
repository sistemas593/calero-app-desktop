package com.calero.lili.api.modCxC.XcPagos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface XcPagosRepository extends JpaRepository<XcPagosEntity, UUID> {

    @Query("SELECT c FROM XcPagosEntity c WHERE c.idPagoGrupo = :idPagoGrupo AND " +
            "c.idData =:idData AND c.idEmpresa  =:idEmpresa" +
            "(:sucursal IS NULL OR c.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR c.createdBy = :usuario)")
    List<XcPagosEntity> getAllForFindByIdPagoGrupo(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("idPagoGrupo") UUID idPagoGrupo,
                                                   @Param("sucursal") String sucursal,
                                                   @Param("usuario") String usuario);


}
