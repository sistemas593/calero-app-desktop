package com.calero.lili.core.modVentas.reporteCredito;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatosCrediticiosDetalleRepository extends JpaRepository<DatosCrediticiosDetalleEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM DatosCrediticiosDetalleEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.numeroOperacion in :numerosOperacion")
    List<DatosCrediticiosDetalleEntity> findAllNumeroOperacion(@Param("idData") Long idData,
                                                               @Param("idEmpresa") Long idEmpresa,
                                                               @Param("numerosOperacion") List<String> numerosOperacion);


}
