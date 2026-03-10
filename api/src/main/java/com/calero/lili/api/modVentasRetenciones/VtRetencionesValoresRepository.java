package com.calero.lili.api.modVentasRetenciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VtRetencionesValoresRepository extends JpaRepository<VtRetencionesValoresEntity, UUID> {

    @Query(value = "SELECT entidad " +
            "FROM VtRetencionesValoresEntity entidad " +
            "WHERE entidad.idRetencionValores in :listIdValores ")
    List<VtRetencionesValoresEntity> findAllIds(@Param("listIdValores") List<UUID> listIdValores);


    @Query(value = "SELECT entidad " +
            "FROM VtRetencionesValoresEntity entidad " +
            "WHERE entidad.factura.idFactura = :idFactura ")
    List<VtRetencionesValoresEntity> findAllForIdFactura(@Param("idFactura") UUID idFactura);


}
