package com.calero.lili.core.modContabilidad.modCentroCostos;

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
public interface CnCentroCostosRepository extends JpaRepository<CnCentroCostosEntity, UUID> {


    @Query("SELECT e " +
            "FROM CnCentroCostosEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idCentroCostos = :idCentroCostos")
    Optional<CnCentroCostosEntity> findByIdCentroCostos(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idCentroCostos") UUID idCentroCostos);


    @Query("SELECT e " +
            "FROM CnCentroCostosEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.codigoCentroCostosOriginal = :codigoOriginal")
    Optional<CnCentroCostosEntity> findByCentroCostosCodigoOriginal(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("codigoOriginal") String codigoOriginal);

    @Query("SELECT e " +
            "FROM CnCentroCostosEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.codigoCentroCostos = :codigoCentroCostos")
    Optional<CnCentroCostosEntity> findByCentroCostosCodigo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("codigoCentroCostos") String codigoCentroCostos);


    @Query(
            value = "SELECT entity " +
                    "FROM CnCentroCostosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) " +
                    "ORDER BY entity.codigoCentroCostosOriginal")
    List<CnCentroCostosEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa);


    @Query(
            value = "SELECT entity " +
                    "FROM CnCentroCostosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "( " +
                    ":filter IS NULL OR LOWER(entity.codigoCentroCostos) LIKE LOWER(CONCAT('%', :filterContent, '%')) AND " +
                    ":filter IS NULL OR LOWER(entity.centroCostos) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")",
            countQuery = "SELECT COUNT(1) " +
                    "FROM CnCentroCostosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.codigoCentroCostos) LIKE LOWER(CONCAT('%', :filterContent, '%')) AND " +
                    ":filter IS NULL OR LOWER(entity.centroCostos) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")")
    Page<CnCentroCostosEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);

}
