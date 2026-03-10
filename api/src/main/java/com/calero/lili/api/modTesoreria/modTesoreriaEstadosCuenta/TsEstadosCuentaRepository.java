package com.calero.lili.api.modTesoreria.modTesoreriaEstadosCuenta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TsEstadosCuentaRepository extends JpaRepository<TsEstadosCuentaEntity, UUID> {

    @Query("""
            SELECT e  FROM TsEstadosCuentaEntity e WHERE e.tsEntidadEntity.idEntidad = :idEntidad 
            AND e.fechaDocumento >= :fechaInicio AND e.fechaDocumento < :fechaFin""")
    List<TsEstadosCuentaEntity> findByPeriodoAndEntidad(@Param("idEntidad") UUID idEntidad,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin
    );

}
