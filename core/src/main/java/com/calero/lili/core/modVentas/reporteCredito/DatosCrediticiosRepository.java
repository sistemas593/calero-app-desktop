package com.calero.lili.core.modVentas.reporteCredito;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatosCrediticiosRepository extends JpaRepository<DatosCrediticiosEntity, UUID> {

    @Query(value = "SELECT entity " +
            "FROM DatosCrediticiosEntity entity " +
            "where entity.idData = :idData and entity.idEmpresa = :idEmpresa")
    List<DatosCrediticiosEntity> getFindAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa);

}
