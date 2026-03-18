package com.calero.lili.core.modRRHH;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RhPeriodosRepository extends JpaRepository<RhPeriodosEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM RhPeriodosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.periodo = :periodo ")
    Optional<RhPeriodosEntity> findByPeriodo(Long idData, Long idEmpresa, String periodo);


    @Query(value = "SELECT entity " +
            "FROM RhPeriodosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.anio = :anio AND " +
            "entity.mes = :mes ")
    Optional<RhPeriodosEntity> findByAnioAndMes(Long idData, Long idEmpresa, String anio, String mes);

    @Query(value = "SELECT entity " +
            "FROM RhPeriodosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.fechaFin = :fechaFin ")
        Optional<RhPeriodosEntity> findByFechaFin(Long idData, Long idEmpresa, LocalDate fechaFin);


}
