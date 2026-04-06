package com.calero.lili.core.modAdminEmpresasSeries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdEmpresasSeriesRepository extends JpaRepository<AdEmpresasSeriesEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSeriesEntity entity " +
            "LEFT JOIN FETCH entity.documentosEntity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idSerie = :idSerie ")
    Optional<AdEmpresasSeriesEntity> findById(@Param("idData") Long idData,
                                              @Param("idEmpresa") Long idEmpresa,
                                              @Param("idSerie") UUID idSerie);

    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSeriesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.serie = :serie ")
    Optional<AdEmpresasSeriesEntity> findBySerie(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa,
                                                 @Param("serie") String serie);

    @Query(
            value = "SELECT entity " +
                    "FROM AdEmpresasSeriesEntity entity " +
                    "LEFT JOIN FETCH entity.documentosEntity " +
                    "WHERE (entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) and " +
                    "(" +
                    "(:filter IS NULL OR LOWER(entity.serie) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.nombreComercial) LIKE LOWER(CONCAT('%', :filterContent, '%')))" +
                    ") ",
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdEmpresasSeriesEntity   entity " +
                    "WHERE (entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) and " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.serie) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.nombreComercial) LIKE LOWER(CONCAT('%', :filterContent, '%'))" +
                    ") ")
    Page<AdEmpresasSeriesEntity> findAllPaginate(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa,
                                                 @Param("filter") String filter,
                                                 @Param("filterContent") String filterContent,
                                                 Pageable pageable);

    @Query("SELECT DISTINCT entity " +
            "FROM AdEmpresasSeriesEntity entity " +
            "LEFT JOIN FETCH entity.documentosEntity " +
            "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa")
    List<AdEmpresasSeriesEntity> findAllWithDocumentos(@Param("idData") Long idData,
                                                       @Param("idEmpresa") Long idEmpresa);
}
