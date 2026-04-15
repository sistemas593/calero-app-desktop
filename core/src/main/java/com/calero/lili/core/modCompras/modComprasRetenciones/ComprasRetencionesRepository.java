package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.modCompras.impuestosXml.CpRetencionesOneProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.DeEmitidasRetencionesProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface ComprasRetencionesRepository extends JpaRepository<CpRetencionesEntity, UUID>, JpaSpecificationExecutor<CpRetencionesEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CpRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idRetencion = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<CpRetencionesEntity> findByIdEntity(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa,
                                                 @Param("idVenta") UUID idVenta,
                                                 @Param("sucursal") String sucursal,
                                                 @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpRetencionesEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idRetencion = :idVenta")
    void deleteById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idVenta") UUID idVenta);


    @Query(value = "SELECT cp_retenciones.id_Data as id_data " +
            "FROM cp_retenciones cp_retenciones " +
            "WHERE (cp_retenciones.id_Data = :idData)  AND " +
            "(cp_retenciones.id_Empresa = :idEmpresa) AND " +
            "cp_retenciones.clave_acceso = :numeroAutorizacion AND cp_retenciones.deleted = false" +
            "LIMIT 1", nativeQuery = true)
    Optional<DeEmitidasRetencionesProjection> findExistByNumeroAutorizacion(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("numeroAutorizacion") String numeroAutorizacion);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CpRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
            "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacionRetencion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM CpRetencionesEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacionRetencion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )"
    )
    Page<CpRetencionesEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                              @Param("sucursal") String sucursal,
                                              @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                              @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                              @Param("serie") String serie,
                                              @Param("secuencial") String secuencial,
                                              @Param("numeroAutorizacion") String numeroAutorizacion,
                                              @Param("usuario") String usuario,
                                              Pageable pageable);

    @Query(
            value = "SELECT cpiv.codigo as codigo, " +
                    "cpiv.codigo_porcentaje as codigoPorcentaje, " +
                    "SUM(cpiv.base_imponible) as totalBaseImponible, " +
                    "SUM(cpiv.valor) as totalValor " +
                    "FROM cp_retenciones cpr " +
                    "INNER JOIN cp_impuestos cpi " +
                    "ON cpr.id_retencion = cpi.id_retencion " +
                    "INNER JOIN cp_impuestos_valores cpiv " +
                    "ON cpi.id_impuestos = cpiv.id_impuestos " +
                    "WHERE cpr.id_data = :idData " +
                    "AND cpr.anulada = false AND cpr.deleted = false AND " +
                    "AND cpr.id_empresa = :idEmpresa " +
                    "AND (:sucursal IS NULL OR cpr.sucursal = :sucursal) " +
                    "AND (:serie IS NULL OR cpr.serie_retencion = :serie) " +
                    "AND (:secuencial IS NULL OR cpr.secuencial_retencion = :secuencial) " +
                    "AND (:fechaEmisionDesde IS NULL OR cpr.fecha_emision_retencion >= :fechaEmisionDesde) " +
                    "AND (:fechaEmisionHasta IS NULL OR cpr.fecha_emision_retencion <= :fechaEmisionHasta) " +
                    "GROUP BY cpiv.codigo, cpiv.codigo_porcentaje",
            nativeQuery = true
    )
    List<TotalesProjection> totalValores(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                         @Param("sucursal") String sucursal,
                                         @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("serie") String serie,
                                         @Param("secuencial") String secuencial);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM CpRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )  " +
            ")"
    )
    List<CpRetencionesEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                      @Param("sucursal") String sucursal, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                      @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                      @Param("serie") String serie, @Param("secuencial") String secuencial);

    @Query(value = "SELECT " +
            "entity.id_retencion as idRetencion,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.comprobante as comprobante, " +
            "entity.serie as serie, " +
            "entity.secuencial as secuencial, " +
            "entity.estado_documento as estadoDocumento,  " +
            "FROM cp_retenciones  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND entity.deleted = false AND " +
            "entity.id_retencion = :id ", nativeQuery = true)
    Optional<CpRetencionesOneProjection> findXMLById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("id") UUID id);

}
