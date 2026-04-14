package com.calero.lili.core.modVentas;

import com.calero.lili.core.modCompras.impuestosXml.VtVentasFacturaOneProjection;
import com.calero.lili.core.modVentas.projection.ImpuestosF104Projection;
import com.calero.lili.core.modVentas.projection.OneDetalleProjection;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.modVentas.projection.TotalCabeceraProjection;
import com.calero.lili.core.modVentas.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface VtVentasRepository extends JpaRepository<VtVentaEntity, UUID>, JpaSpecificationExecutor<VtVentaEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtVentaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idVenta = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<VtVentaEntity> findByIdEntity(@Param("idData") Long idData,
                                           @Param("idEmpresa") Long idEmpresa,
                                           @Param("idVenta") UUID idVenta,
                                           @Param("sucursal") String sucursal,
                                           @Param("usuario") String usuario);


    @Query(value = "SELECT id_venta as idVenta " +
            "FROM vt_ventas vtVentasEntity " +
            "WHERE (vtVentasEntity.id_Data = :idData)  AND " +
            "(vtVentasEntity.id_Empresa = :idEmpresa) AND " +
            "vtVentasEntity.tipo_Venta = :tipoVenta AND " +
            "vtVentasEntity.serie = :serie AND " +
            "vtVentasEntity.secuencial = :secuencial AND " +
            "vtVentasEntity.deleted = false LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("tipoVenta") String tipoVenta,
                                                  @Param("serie") String serie,
                                                  @Param("secuencial") String secuencial);

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
            "FROM vt_ventas vtVentasEntity INNER JOIN vt_ventas_detalle detalle ON vtVentasEntity.id_venta = detalle.id_venta " +
            "WHERE vtVentasEntity.id_data = :idData  AND " +
            "vtVentasEntity.id_empresa = :idEmpresa AND " +
            "vtVentasEntity.id_venta = :idVenta ", nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(@Param("idData") Long idData,
                                                    @Param("idEmpresa") Long idEmpresa,
                                                    @Param("idVenta") UUID idVenta);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtVentaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.sucursal = :usuario) AND " +
            "(:tipoVenta IS NULL OR vtVentasEntity.tipoVenta = :tipoVenta) AND " +
            "(:idTercero IS NULL OR vtVentasEntity.tercero.idTercero = :idTercero) AND " +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM VtVentaEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "(:usuario IS NULL OR vtVentasEntity.sucursal = :usuario) AND " +
                    "(:tipoVenta IS NULL OR vtVentasEntity.tipoVenta = :tipoVenta) AND " +
                    "(:idTercero IS NULL OR vtVentasEntity.tercero.idTercero = :idTercero) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta ) ")
    Page<VtVentaEntity> findAllPaginate(@Param("idData") Long idData,
                                        @Param("idEmpresa") Long idEmpresa,
                                        @Param("sucursal") String sucursal,
                                        @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                        @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                        @Param("idTercero") UUID idTercero,
                                        @Param("tipoVenta") String tipoVenta,
                                        @Param("serie") String serie,
                                        @Param("secuencial") String secuencial,
                                        @Param("numeroAutorizacion") String numeroAutorizacion,
                                        @Param("usuario") String usuario,
                                        Pageable pageable);


    @Query(
            value = "SELECT " +
                    "sum(vtVentaEntity.totalDescuento) as totalDescuento, " +
                    "sum(vtVentaEntity.total) as total " +
                    "FROM VtVentaEntity vtVentaEntity " +
                    "INNER JOIN GeTerceroEntity clienteEntity ON vtVentaEntity.tercero.idTercero = clienteEntity.idTercero " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "(:tipoVenta IS NULL OR vtVentaEntity.tipoVenta = :tipoVenta) AND " +
                    "(:serie IS NULL OR vtVentaEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    TotalCabeceraProjection totalCabecera(@Param("idData") Long idData,
                                          @Param("idEmpresa") Long idEmpresa,
                                          @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                          @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                          @Param("tipoVenta") String tipoVenta,
                                          @Param("serie") String serie,
                                          @Param("secuencial") String secuencial);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM vt_ventas vtVentaEntity " +
                    "INNER JOIN vt_ventas_valores valoresEntity ON vtVentaEntity.id_venta = valoresEntity.id_venta " +
                    "WHERE ( vtVentaEntity.id_data = :idData)  AND  vtVentaEntity.anulada = false AND" +
                    "(vtVentaEntity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentaEntity.sucursal = :sucursal) AND " +
                    "(:tipoVenta IS NULL OR vtVentaEntity.tipo_venta = :tipoVenta) AND " +
                    "(:serie IS NULL OR vtVentaEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fecha_emision <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(@Param("idData") Long idData,
                                         @Param("idEmpresa") Long idEmpresa,
                                         @Param("sucursal") String sucursal,
                                         @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("tipoVenta") String tipoVenta,
                                         @Param("serie") String serie,
                                         @Param("secuencial") String secuencial);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtVentaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "(:tipoVenta IS NULL OR vtVentasEntity.tipoVenta = :tipoVenta) AND " +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
    )
    List<VtVentaEntity> findAll(@Param("idData") Long idData,
                                @Param("idEmpresa") Long idEmpresa,
                                @Param("sucursal") String sucursal,
                                @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                @Param("tipoVenta") String tipoVenta,
                                @Param("serie") String serie,
                                @Param("secuencial") String secuencial);

    @Query(value = "SELECT " +
            "entity.id_venta as idVenta,  " +
            "entity.estado_documento as estadoDocumento,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.comprobante as comprobante, " +
            "entity.tipo_venta as tipoVenta, " +
            "entity.serie as serie, " +
            "entity.secuencial as secuencial " +
            "FROM vt_ventas  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_venta = :id ", nativeQuery = true)
    Optional<VtVentasFacturaOneProjection> findXMLById(@Param("idData") Long idData,
                                                       @Param("idEmpresa") Long idEmpresa,
                                                       @Param("id") UUID id);


    @Query(
            value = "SELECT vtVentaEntity " +
                    "FROM CpLiquidacionesEntity vtVentaEntity " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "vtVentaEntity.formatoDocumento= 'E' AND " +
                    "vtVentaEntity.estadoDocumento= :estadoDocumento AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<VtVentaEntity> findAllFacturasGenerar(@Param("idData") Long idData,
                                               @Param("idEmpresa") Long idEmpresa,
                                               @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                               @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                               @Param("estadoDocumento") String estadoDocumento);

    @Query(
            value = "SELECT vtVentaEntity " +
                    "FROM CpLiquidacionesEntity vtVentaEntity " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "vtVentaEntity.estadoDocumento='AUT' AND " +
                    /*"vtVentaEntity.emailEstado='0' AND " +*/
                    "vtVentaEntity.email<>'' AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<VtVentaEntity> findAllFacturasGenerarCorreo(@Param("idData") Long idData,
                                                     @Param("idEmpresa") Long idEmpresa,
                                                     @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                                     @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '4'" +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoFacturasNotasDebito(@Param("idData") Long idData,
                                                                        @Param("idEmpresa") Long idEmpresa,
                                                                        @Param("tipoIngreso") String tipoIngreso,
                                                                        @Param("fechaDesde") LocalDate fechaDesde,
                                                                        @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '4'" +
            " and (vv.tipo_venta = 'NCR') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoNotasCredito(@Param("idData") Long idData,
                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                 @Param("tipoIngreso") String tipoIngreso,
                                                                 @Param("fechaDesde") LocalDate fechaDesde,
                                                                 @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '0'" +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoFacturasNotasDebitoBaseCero(@Param("idData") Long idData,
                                                                                @Param("idEmpresa") Long idEmpresa,
                                                                                @Param("tipoIngreso") String tipoIngreso,
                                                                                @Param("fechaDesde") LocalDate fechaDesde,
                                                                                @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '0'" +
            " and (vv.tipo_venta = 'NCR') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoNotasCreditoBaseCero(@Param("idData") Long idData,
                                                                         @Param("idEmpresa") Long idEmpresa,
                                                                         @Param("tipoIngreso") String tipoIngreso,
                                                                         @Param("fechaDesde") LocalDate fechaDesde,
                                                                         @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and (vvv.codigo_porcentaje = '7' or vvv.codigo_porcentaje = '6')" +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoFacturasNotasDebitoExentoIva(@Param("idData") Long idData,
                                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                                 @Param("tipoIngreso") String tipoIngreso,
                                                                                 @Param("fechaDesde") LocalDate fechaDesde,
                                                                                 @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and (vvv.codigo_porcentaje = '7' or vvv.codigo_porcentaje = '6')" +
            " and (vv.tipo_venta = 'NCR') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoNotasCreditoExentoIva(@Param("idData") Long idData,
                                                                          @Param("idEmpresa") Long idEmpresa,
                                                                          @Param("tipoIngreso") String tipoIngreso,
                                                                          @Param("fechaDesde") LocalDate fechaDesde,
                                                                          @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '5'" +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoFacturasNotasDebitoTarifaCinco(@Param("idData") Long idData,
                                                                                   @Param("idEmpresa") Long idEmpresa,
                                                                                   @Param("tipoIngreso") String tipoIngreso,
                                                                                   @Param("fechaDesde") LocalDate fechaDesde,
                                                                                   @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '5'" +
            " and (vv.tipo_venta = 'NCR') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoNotasCreditoTarifaCinco(@Param("idData") Long idData,
                                                                            @Param("idEmpresa") Long idEmpresa,
                                                                            @Param("tipoIngreso") String tipoIngreso,
                                                                            @Param("fechaDesde") LocalDate fechaDesde,
                                                                            @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' " +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoFacturasNotasDebitoReembolso(@Param("idData") Long idData,
                                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                                 @Param("tipoIngreso") String tipoIngreso,
                                                                                 @Param("fechaDesde") LocalDate fechaDesde,
                                                                                 @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT\n" +
            "    COALESCE(SUM(vvv.valor),0) AS valor,\n" +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible,\n" +
            "    vv.tipo_ingreso AS tipo_ingreso\n" +
            "FROM vt_ventas vv\n" +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta\n" +
            "WHERE vv.tipo_ingreso = :tipoIngreso\n" +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta\n" +
            "  AND vv.anulada = false\n" +
            "  AND vv.id_empresa = :idEmpresa\n" +
            "  AND vv.id_data = :idData\n" +
            " AND vvv.codigo = '2' " +
            " and (vv.tipo_venta = 'NCR') " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> findByImpuestoNotasCreditoReembolso(@Param("idData") Long idData,
                                                                          @Param("idEmpresa") Long idEmpresa,
                                                                          @Param("tipoIngreso") String tipoIngreso,
                                                                          @Param("fechaDesde") LocalDate fechaDesde,
                                                                          @Param("fechaHasta") LocalDate fechaHasta);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtVentaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.tipoVenta = 'FAC' AND " +
            "vtVentasEntity.estadoDocumento = 'ENV' ")
    List<VtVentaEntity> obtenerTodosParaAutorizar(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa);

}
