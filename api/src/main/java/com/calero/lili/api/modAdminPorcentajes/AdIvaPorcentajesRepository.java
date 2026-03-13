package com.calero.lili.api.modAdminPorcentajes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdIvaPorcentajesRepository extends JpaRepository<AdIvaPorcentajesEntity, Long> {


    @Query(value = "SELECT entity FROM AdIvaPorcentajesEntity entity WHERE entity.idIvaPorcentaje = :idPorcentaje")
    Optional<AdIvaPorcentajesEntity> findByIdPorcentaje(@Param("idPorcentaje") Long idPorcentaje);


    @Query(value = "SELECT entity FROM AdIvaPorcentajesEntity entity " +
            "WHERE (:fechaInicio IS NULL OR entity.fechaDesde >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR entity.fechaDesde <= :fechaFin)",
            countQuery = "SELECT COUNT(entity) FROM AdIvaPorcentajesEntity entity " +
                    "WHERE (:fechaInicio IS NULL OR entity.fechaDesde >= :fechaInicio) AND " +
                    "(:fechaFin IS NULL OR entity.fechaDesde <= :fechaFin)")
    Page<AdIvaPorcentajesEntity> findAllPaginate(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin,
                                                 Pageable pageable);


    @Query("""
            SELECT entity 
            FROM AdIvaPorcentajesEntity entity
            WHERE entity.fechaDesde <= :fechaFactura
            ORDER BY entity.fechaDesde DESC LIMIT 1
            """)
    Optional<AdIvaPorcentajesEntity> findVigente(@Param("fechaFactura") LocalDate fechaFactura);


}
