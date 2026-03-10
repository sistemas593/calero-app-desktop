package com.calero.lili.api.modCompras.modComprasRetenciones;

import com.calero.lili.api.modCompras.impuestosXml.CpRetencionesOneProjection;
import com.calero.lili.api.modCompras.modComprasRetenciones.projection.DeEmitidasRetencionesProjection;
import com.calero.lili.api.modCompras.modComprasRetenciones.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
            "vtVentasEntity.idRetencion = :idVenta ")
    Optional<CpRetencionesEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idVenta);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpRetencionesEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idRetencion = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);


    @Query(value = "SELECT cp_retenciones.id_Data as id_data " +
            "FROM cp_retenciones cp_retenciones " +
            "WHERE (cp_retenciones.id_Data = :idData)  AND " +
            "(cp_retenciones.id_Empresa = :idEmpresa) AND " +
            "cp_retenciones.clave_acceso = :numeroAutorizacion " +
            "LIMIT 1", nativeQuery = true)
    Optional<DeEmitidasRetencionesProjection> findExistByNumeroAutorizacion(Long idData, Long idEmpresa, String numeroAutorizacion);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CpRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
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
                    "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacionRetencion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )"
    )
    Page<CpRetencionesEntity> findAllPaginate(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial, String numeroAutorizacion, Pageable pageable);

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
                    "AND cpr.anulada = false " +
                    "AND cpr.id_empresa = :idEmpresa " +
                    "AND (:sucursal IS NULL OR cpr.sucursal = :sucursal) " +
                    "AND (:numeroIdentificacion IS NULL OR cpr.numero_identificacion = :numeroIdentificacion) " +
                    "AND (:serie IS NULL OR cpr.serie_retencion = :serie) " +
                    "AND (:secuencial IS NULL OR cpr.secuencial_retencion = :secuencial) " +
                    "AND (:fechaEmisionDesde IS NULL OR cpr.fecha_emision_retencion >= :fechaEmisionDesde) " +
                    "AND (:fechaEmisionHasta IS NULL OR cpr.fecha_emision_retencion <= :fechaEmisionHasta) " +
                    "GROUP BY cpiv.codigo, cpiv.codigo_porcentaje",
            nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde,
                                         LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie,
                                         String secuencial);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM CpRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )  " +
            ")"
    )
    List<CpRetencionesEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);

    @Query(value = "SELECT " +
            "entity.id_retencion as idRetencion,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.comprobante as comprobante " +
            "FROM cp_retenciones  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_retencion = :id ", nativeQuery = true)
    Optional<CpRetencionesOneProjection> findXMLById(Long idData, Long idEmpresa, UUID id);

}
