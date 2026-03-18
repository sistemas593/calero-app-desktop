package com.calero.lili.core.modVentasRetenciones;

import com.calero.lili.core.modCompras.impuestosXml.VtRetencionesOneProjection;
import com.calero.lili.core.modVentasRetenciones.projection.DeRecibidasRetencionesProjection;
import com.calero.lili.core.modVentasRetenciones.projection.TotalesProjection;
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
public interface VentasRetencionesRepository extends JpaRepository<VtRetencionesEntity, UUID>, JpaSpecificationExecutor<VtRetencionesEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idRetencion = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<VtRetencionesEntity> findById(@Param("idData") Long idData,
                                           @Param("idEmpresa") Long idEmpresa,
                                           @Param("idVenta") UUID idVenta,
                                           @Param("sucursal") String sucursal,
                                           @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM VtRetencionesEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idRetencion = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);


    @Query(value = "SELECT vt_retenciones.id_Data as id_data " +
            "FROM vt_retenciones vt_retenciones " +
            "WHERE (vt_retenciones.id_Data = :idData)  AND " +
            "(vt_retenciones.id_Empresa = :idEmpresa) AND " +
            "vt_retenciones.numero_autorizacion = :numeroAutorizacion " +
            "LIMIT 1", nativeQuery = true)
    Optional<DeRecibidasRetencionesProjection> findExistByNumeroAutorizacion(Long idData, Long idEmpresa, String numeroAutorizacion);

    @Query(value = "SELECT " +
            "entity.id_retencion as idRetencion,  " +
            "entity.numero_autorizacion_retencion as numeroAutorizacion, " +
            "entity.comprobante as comprobante " +
            "FROM vt_retenciones  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_retencion = :id ", nativeQuery = true)
    Optional<VtRetencionesOneProjection> findXMLById(Long idData, Long idEmpresa, UUID id);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtRetencionesEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
            "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacionRetencion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM VtRetencionesEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
                    "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serieRetencion = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencialRetencion = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacionRetencion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmisionRetencion >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmisionRetencion <= :fechaEmisionHasta )"
    )
    Page<VtRetencionesEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                              @Param("sucursal") String sucursal,
                                              @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                              @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                              @Param("numeroIdentificacion") String numeroIdentificacion,
                                              @Param("serie") String serie,
                                              @Param("secuencial") String secuencial,
                                              @Param("numeroAutorizacion") String numeroAutorizacion,
                                              @Param("usuario") String usuario,
                                              Pageable pageable);


    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_retencion as codigoRetencion, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor_retenido) as totalValor " +
                    "FROM vt_retenciones vtVentaEntity " +
                    "INNER JOIN vt_retenciones_valores valoresEntity ON vtVentaEntity.id_retencion = valoresEntity.id_retencion " +
                    "WHERE ( vtVentaEntity.id_data = :idData)  AND " +
                    "(vtVentaEntity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentaEntity.sucursal = :sucursal) AND " +
                    "(:numeroIdentificacion IS NULL OR vtVentaEntity.numero_identificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR vtVentaEntity.serie_retencion = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial_retencion = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fecha_emision_retencion >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fecha_emision_retencion <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_retencion", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtRetencionesEntity vtVentasEntity " +
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
    List<VtRetencionesEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);


}
