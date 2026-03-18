package com.calero.lili.core.modContabilidad.modCentroCostos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    Optional<CnCentroCostosEntity> findByIdCentroCostos(Long idData, Long idEmpresa, UUID idCentroCostos);


    @Query("SELECT e " +
            "FROM CnCentroCostosEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.codigoCentroCostosOriginal = :codigoOriginal")
    Optional<CnCentroCostosEntity> findByCentroCostosCodigoOriginal(Long idData, Long idEmpresa, String codigoOriginal);

    @Query("SELECT e " +
            "FROM CnCentroCostosEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.codigoCentroCostos = :codigoCentroCostos")
    Optional<CnCentroCostosEntity> findByCentroCostosCodigo(Long idData, Long idEmpresa, String codigoCentroCostos);


    @Query(
            value = "SELECT entity " +
                    "FROM CnCentroCostosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) " +
                    "ORDER BY entity.codigoCentroCostosOriginal")
    List<CnCentroCostosEntity> findAll(Long idData, Long idEmpresa);


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
    Page<CnCentroCostosEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent, Pageable pageable);

}
