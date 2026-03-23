package com.calero.lili.core.modContabilidad.modPlanCuentas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CnPlanCuentasRepository extends JpaRepository<CnPlanCuentaEntity, UUID> {


    @Transactional
    @Modifying
    @Query("DELETE FROM CnPlanCuentaEntity e WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idCuenta = :idCuenta")
    void deleteById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idCuenta") UUID idCuenta);

    @Query("SELECT e " +
            "FROM CnPlanCuentaEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idCuenta = :idCuenta")
    Optional<CnPlanCuentaEntity> findByIdCuenta(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idCuenta") UUID idCuenta);

    @Query("SELECT e " +
            "FROM CnPlanCuentaEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.codigoCuenta = :codigoCuenta")
    Optional<CnPlanCuentaEntity> findByCodigoCuenta(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("codigoCuenta") String codigoCuenta);


    @Query(
            value = "SELECT entity " +
                    "FROM CnPlanCuentaEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) " +
                    "ORDER BY entity.codigoCuentaOriginal"
    )
    List<CnPlanCuentaEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa);

    @Query(
            value = "SELECT entity " +
                    "FROM CnPlanCuentaEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "( " +
                    ":filter IS NULL OR LOWER(entity.codigoCuenta) LIKE LOWER(CONCAT('%', :filterContent, '%')) AND " +
                    ":filter IS NULL OR LOWER(entity.cuenta) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")",
            countQuery = "SELECT COUNT(1) " +
                    "FROM CnPlanCuentaEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.codigoCuenta) LIKE LOWER(CONCAT('%', :filterContent, '%')) AND " +
                    ":filter IS NULL OR LOWER(entity.cuenta) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")")
    Page<CnPlanCuentaEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);


    @Query(
            "SELECT COUNT(e) " +
                    "FROM CnPlanCuentaEntity e " +
                    "WHERE e.idData = :idData " +
                    "AND e.idEmpresa = :idEmpresa")
    Long countByIdDataAndIdEmpresa(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa);


    @Query(
            value = "SELECT entity " +
                    "FROM CnPlanCuentaEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) " +
                    "AND (entity.mayor = :mayor) " +
                    "ORDER BY entity.codigoCuentaOriginal")
    List<CnPlanCuentaEntity> findAllMayores(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("mayor") Boolean mayor);


    @Query("""
                SELECT e
                FROM CnPlanCuentaEntity e
                WHERE e.idData = :idData
                  AND e.idEmpresa = :idEmpresa
                  AND e.codigoCuenta IN :codigosCuentas
            """)
    List<CnPlanCuentaEntity> findByListCuentas(@Param("idData") Long idData,
                                               @Param("idEmpresa") Long idEmpresa,
                                               @Param("codigosCuentas") List<String> codigosCuentas);


}
