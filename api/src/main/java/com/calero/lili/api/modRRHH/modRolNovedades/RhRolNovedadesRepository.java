package com.calero.lili.api.modRRHH.modRolNovedades;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RhRolNovedadesRepository extends JpaRepository<RhRolNovedadesEntity, UUID> {

    @Query(value = "SELECT entity " +
            "FROM RhRolNovedadesEntity entity " +
            "WHERE entity.idData = :idData " +
            "AND entity.idEmpresa = :idEmpresa " +
            "AND entity.periodos.periodo = :periodo ")
    List<RhRolNovedadesEntity> findByAllForPeriodo(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("periodo") String periodo);


    @Query(value = "SELECT entity " +
            "FROM RhRolNovedadesEntity entity " +
            "WHERE entity.idData = :idData " +
            "AND entity.idEmpresa = :idEmpresa " +
            "AND entity.periodos.idPeriodo = :idPeriodo ")
    List<RhRolNovedadesEntity> findByAllPeriodoId(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("idPeriodo") UUID idPeriodo);


}
