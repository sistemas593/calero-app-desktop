package com.calero.lili.core.modCompras.modComprasLiquidaciones;

import com.calero.lili.core.modCompras.impuestosXml.CpLiquidacionOneProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.OneDetalleProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.OneProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.TotalCabeceraProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.TotalesProjection;
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
public interface LiquidacionesRepository extends JpaRepository<CpLiquidacionesEntity, UUID>, JpaSpecificationExecutor<CpLiquidacionesEntity> {

    @Query(value = "SELECT entity " +
            "FROM CpLiquidacionesEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idLiquidacion = :idLiquidacion AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR entity.createdBy = :usuario)")
    Optional<CpLiquidacionesEntity> findByIdEntity(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("idLiquidacion") UUID idLiquidacion,
                                                   @Param("sucursal") String sucursal,
                                                   @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpLiquidacionesEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idLiquidacion = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT id_liquidacion as idLiquidacion " +
            "FROM cp_liquidaciones entity " +
            "WHERE (entity.id_Data = :idData)  AND " +
            "(entity.id_Empresa = :idEmpresa) AND " +
            "entity.serie = :serie AND " +
            "entity.secuencial = :secuencial LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(Long idData, Long idEmpresa, String serie, String secuencial);

    @Query(value = "SELECT  " +
            "detalle.id_item as idItem, " +
            "detalle.codigo_principal as codigoPrincipal, " +
            "detalle.codigo_auxiliar as codigoAuxiliar, " +
            "detalle.codigo_barras as codigoBarras, " +
            "detalle.unidad_medida as unidadMedida, " +
            "detalle.descripcion as descripcion, " +
            "detalle.detalles_adicionales as detallesAdicionales, " +
            "detalle.precio_unitario as precioUnitario, " +
            "detalle.cantidad as cantidad, " +
            "detalle.dscto_item as dsctoItem, " +
            "detalle.descuento as descuento, " +
            "detalle.subtotal_item as subtotalItem, " +
            "detalle.impuestos as impuestos " +
            " " +
            "FROM vt_ventas vtVentasEntity INNER JOIN vt_ventas_detalle detalle ON vtVentasEntity.id_liquidacion = detalle.id_liquidacion " +
            "WHERE vtVentasEntity.id_data = :idData  AND " +
            "vtVentasEntity.id_empresa = :idEmpresa AND " +
            "vtVentasEntity.id_liquidacion = :idVenta ", nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT entity " +
            "FROM CpLiquidacionesEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR entity.createdBy = :usuario) AND " +
            "(:numeroIdentificacion IS NULL OR entity.numeroIdentificacion = :numeroIdentificacion ) AND " +
            "(:serie IS NULL OR entity.serie = :serie) AND " +
            "(:secuencial IS NULL OR entity.secuencial = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR entity.numeroAutorizacion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )"
    )
    Page<CpLiquidacionesEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
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
            value = "SELECT " +
                    "sum(cabeceraEntity.totalDescuento) as totalDescuento, " +
                    "sum(cabeceraEntity.total) as total " +
                    "FROM CpLiquidacionesEntity cabeceraEntity " +
                    "INNER JOIN GeTerceroEntity clienteEntity ON cabeceraEntity.proveedor.idTercero = clienteEntity.idTercero " +
                    "WHERE ( cabeceraEntity.idData = :idData)  AND " +
                    "(cabeceraEntity.idEmpresa = :idEmpresa) AND " +
                    "(:serie IS NULL OR cabeceraEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR cabeceraEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR cabeceraEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR cabeceraEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    TotalCabeceraProjection totalCabecera(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String serie, String secuencial);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM cp_liquidaciones entity " +
                    "INNER JOIN cp_liquidaciones_valores valoresEntity ON entity.id_liquidacion = valoresEntity.id_liquidacion " +
                    "WHERE ( entity.id_data = :idData)  AND entity.anulada = false AND " +
                    "(entity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
                    "(:numeroIdentificacion IS NULL OR entity.numero_identificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR entity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fecha_emision <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);

    @Query(value = "SELECT entity " +
            "FROM CpLiquidacionesEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(" +
            "(:numeroIdentificacion IS NULL OR entity.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:serie IS NULL OR entity.serie = :serie) AND " +
            "(:secuencial IS NULL OR entity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
    )
    List<CpLiquidacionesEntity> findAll(Long idData, Long idEmpresa, String sucursal,
                                        LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta,
                                        String numeroIdentificacion, String serie, String secuencial);

    @Query(
            value = "SELECT entity " +
                    "FROM CpLiquidacionesEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "entity.formatoDocumento= 'E' AND " +
                    "entity.estadoDocumento= :estadoDocumento AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<CpLiquidacionesEntity> findAllFacturasGenerar(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde,
                                                       LocalDate fechaEmisionHasta, String estadoDocumento);

    @Query(
            value = "SELECT entity " +
                    "FROM CpLiquidacionesEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "entity.estadoDocumento='AUT' AND " +
                    /* "entity.emailEstado='0' AND " +*/
                    "entity.email<>'' AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<CpLiquidacionesEntity> findAllFacturasGenerarCorreo(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta);

    @Query(value = "SELECT " +
            "entity.id_liquidacion as idLiquidacion,  " +
            "entity.estado_documento as estadoDocumento,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.comprobante as comprobante " +
            "FROM cp_liquidaciones  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_liquidacion = :id ", nativeQuery = true)
    Optional<CpLiquidacionOneProjection> findXMLById(Long idData, Long idEmpresa, UUID id);


    @Query("""
            SELECT entity
            FROM CpLiquidacionesEntity entity
            WHERE entity.idData = :idData
              AND entity.idEmpresa = :idEmpresa
              AND  entity.fechaEmision >= :fechaEmisionDesde
              AND  entity.fechaEmision <= :fechaEmisionHasta""")
    List<CpLiquidacionesEntity> findAllDates(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                             @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                             @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);


}
