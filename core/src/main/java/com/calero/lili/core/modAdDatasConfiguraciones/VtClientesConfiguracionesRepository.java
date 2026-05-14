package com.calero.lili.core.modAdDatasConfiguraciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VtClientesConfiguracionesRepository extends JpaRepository<VtClientesConfiguracionesEntity, UUID> {

    @Query(
            value = "SELECT entity " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) ",
            countQuery = "SELECT COUNT(entity) " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) "
    )
    Page<VtClientesConfiguracionesEntity> findAllPaginate(@Param("filter") String filter,
                                                          @Param("filterContent") String filterContent,
                                                          Pageable pageable
    );

    @Query(
            value = "SELECT entity " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) " +

                    "AND entity.fechaVencimiento >= COALESCE(:fechaVencimientoDesde, entity.fechaVencimiento) " +
                    "AND entity.fechaVencimiento <= COALESCE(:fechaVencimientoHasta, entity.fechaVencimiento) "
            ,
            countQuery = "SELECT COUNT(entity) " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) " +

                    "AND entity.fechaVencimiento >= COALESCE(:fechaVencimientoDesde, entity.fechaVencimiento) " +
                    "AND entity.fechaVencimiento <= COALESCE(:fechaVencimientoHasta, entity.fechaVencimiento) "
    )
    Page<VtClientesConfiguracionesEntity> findAllPaginateVencimiento(@Param("filter") String filter,
                                                                     @Param("filterContent") String filterContent,
                                                                     @Param("fechaVencimientoDesde") LocalDate fechaVencimientoDesde,
                                                                     @Param("fechaVencimientoHasta") LocalDate fechaVencimientoHasta,
                                                                     Pageable pageable);

    @Query(
            value = "SELECT entity " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) " +

                    "AND entity.fechaBlo >= COALESCE(:fechaBloqueoDesde, entity.fechaBlo) " +
                    "AND entity.fechaBlo <= COALESCE(:fechaBloqueoHasta, entity.fechaBlo) "
            ,
            countQuery = "SELECT COUNT(entity) " +
                    "FROM VtClientesConfiguracionesEntity entity " +
                    "WHERE ( :filter IS NULL OR ( " +
                    "       LOWER(entity.clave) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "       LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")) " +

                    "AND entity.fechaBlo >= COALESCE(:fechaBloqueoDesde, entity.fechaBlo) " +
                    "AND entity.fechaBlo <= COALESCE(:fechaBloqueoHasta, entity.fechaBlo) "
    )
    Page<VtClientesConfiguracionesEntity> findAllPaginateBloqueo(@Param("filter") String filter,
                                                                 @Param("filterContent") String filterContent,
                                                                 @Param("fechaBloqueoDesde") LocalDate fechaVencimientoDesde,
                                                                 @Param("fechaBloqueoHasta") LocalDate fechaVencimientoHasta,
                                                                 Pageable pageable);


    @Query(value = "SELECT entity " +
            "FROM VtClientesConfiguracionesEntity entity " +
            "where entity.idConfiguracion = :idConfiguracion ")
    Optional<VtClientesConfiguracionesEntity> findById(@Param("idConfiguracion") UUID idConfiguracion);

    @Query(value = "SELECT entity " +
            "FROM VtClientesConfiguracionesEntity entity " +
            "where entity.clave = :clave ")
    Optional<VtClientesConfiguracionesEntity> findByClave(@Param("clave") String clave);

    @Query(value = "SELECT entity " +
            "FROM VtClientesConfiguracionesEntity entity " +
            "where entity.ruc = :ruc ")
    Optional<VtClientesConfiguracionesEntity> findByRuc(@Param("ruc") String ruc);

}
