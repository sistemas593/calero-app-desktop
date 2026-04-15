package com.calero.lili.core.adLogs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public interface AdLogsRepository extends JpaRepository<AdLogsEntity, Long> {

    @Query("SELECT a FROM AdLogsEntity a WHERE a.idDocumento = :id")
    List<AdLogsEntity> getFindById(@Param("id") UUID id);


    @Query(value = "SELECT a FROM AdLogsEntity a " +
            "WHERE a.idData = :idData AND " +
            "a.idEmpresa = :idEmpresa AND " +
            "(:tipo IS NULL OR a.tipo = :tipo) AND " +
            "(cast(:fechaDesde as date) is null OR cast(a.fechaHora as date) >= :fechaDesde) AND " +
            "(cast(:fechaHasta as date) is null OR cast(a.fechaHora as date) <= :fechaHasta)",
            countQuery = "SELECT COUNT(1) FROM AdLogsEntity a " +
                    "WHERE a.idData = :idData AND " +
                    "a.idEmpresa = :idEmpresa AND " +
                    "(cast(:fechaDesde as date) is null OR cast(a.fechaHora as date) >= :fechaDesde) AND " +
                    "(cast(:fechaHasta as date) is null OR cast(a.fechaHora as date) <= :fechaHasta)")
    Page<AdLogsEntity> findAllByFecha(@Param("idData") Long idData,
                                      @Param("idEmpresa") Long idEmpresa,
                                      @Param("fechaDesde") LocalDate fechaDesde,
                                      @Param("fechaHasta") LocalDate fechaHasta,
                                      @Param("tipo") String tipo,
                                      Pageable pageable);


}
