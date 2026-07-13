package com.calero.lili.core.modImpuestosAnexos.formulario104;

import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.projection.ImpuestosF104Projection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface Formulario104Repository extends JpaRepository<VtVentaEntity, UUID> {


    // BUSQUEDA DE FACTURAS Y NOTAS DE DEBITO POR EL 15%, VENTAS LOCALES
    @Query(value = "SELECT " +
            "    COALESCE(SUM(vvv.valor),0) AS valor, " +
            "    COALESCE(SUM(vvv.base_imponible),0) AS base_imponible, " +
            "    vv.tipo_ingreso AS tipo_ingreso " +
            "FROM vt_ventas vv " +
            "JOIN vt_ventas_valores vvv ON vv.id_venta = vvv.id_venta " +
            "WHERE vv.tipo_ingreso = :tipoIngreso " +
            "  AND vv.fecha_emision BETWEEN :fechaDesde AND :fechaHasta " +
            "  AND vv.anulada = false " +
            "  AND vv.id_empresa = :idEmpresa " +
            "  AND vv.id_data = :idData " +
            " AND vvv.codigo = '2' and vvv.codigo_porcentaje = '4'" +
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorBruto15(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("tipoIngreso") String tipoIngreso,
                                                   @Param("fechaDesde") LocalDate fechaDesde,
                                                   @Param("fechaHasta") LocalDate fechaHasta);


    // BUSQUEDA DE NOTA DE CREDITO POR EL 15%, VENTAS LOCALES
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
            " and (vv.tipo_venta = 'NCR') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> notasCredito15(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorBrutoBaseCero(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'NCR') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> notasCreditoBaseCero(@Param("idData") Long idData,
                                                           @Param("idEmpresa") Long idEmpresa,
                                                           @Param("tipoIngreso") String tipoIngreso,
                                                           @Param("fechaDesde") LocalDate fechaDesde,
                                                           @Param("fechaHasta") LocalDate fechaHasta);


    // BUSQUEDA DE FACTURA Y NOTAS DE DEBITO POR EL BASE NO OBJECTO Y EXCENTA, VENTAS LOCALES
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
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorBrutoVentasLocalesExcentaYNoObjecto(@Param("idData") Long idData,
                                                                               @Param("idEmpresa") Long idEmpresa,
                                                                               @Param("tipoIngreso") String tipoIngreso,
                                                                               @Param("fechaDesde") LocalDate fechaDesde,
                                                                               @Param("fechaHasta") LocalDate fechaHasta);


    // BUSQUEDA DE NOTAS DE CREDITO POR EL BASE NO OBJECTO Y EXCENTA, VENTAS LOCALES
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
            " and (vv.tipo_venta = 'NCR') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> notasCreditoVentasLocalesExentaYNoObjecto(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorBrutoVentasLocales5(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'NCR') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> notasCreditoVentasLocalesTarifa5(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'FAC' or vv.tipo_venta = 'NDB') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorBrutoReembolso(@Param("idData") Long idData,
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
            " and (vv.tipo_venta = 'NCR') and vv.deleted = false " +
            "GROUP BY vv.tipo_ingreso;", nativeQuery = true)
    Optional<ImpuestosF104Projection> notasCreditoReembolso(@Param("idData") Long idData,
                                                            @Param("idEmpresa") Long idEmpresa,
                                                            @Param("tipoIngreso") String tipoIngreso,
                                                            @Param("fechaDesde") LocalDate fechaDesde,
                                                            @Param("fechaHasta") LocalDate fechaHasta);


    // ESTE REQUEST ES PARA COMPRA IMPUESTO BASE CERO
    @Query(value = "SELECT  \n" +
            "    COALESCE(SUM(vvv.valor), 0) AS valor,  \n" +
            "    COALESCE(SUM(vvv.base_imponible), 0) AS base_imponible,  \n" +
            "FROM cp_impuestos vv  \n" +
            "JOIN cp_impuestos_valores vvv \n" +
            "    ON vv.id_impuestos = vvv.id_impuestos  \n" +
            "WHERE vv.fecha_registro BETWEEN :fechaDesde AND :fechaHasta  \n" +
            "  AND vv.id_empresa = :idEmpresa  \n" +
            "  AND vv.id_data = :idData  \n" +
            "  AND vvv.codigo = '2' \n" +
            "  AND vvv.codigo_porcentaje = '0' \n" +
            "  AND vv.deleted = false  \n" +
            " AND  vv.codigo_sustento NOT IN ('S08', 'S09', 'S14') ", nativeQuery = true)
    Optional<ImpuestosF104Projection> valorCompraImpuestoBrutoBaseCero(@Param("idData") Long idData,
                                                                       @Param("idEmpresa") Long idEmpresa,
                                                                       @Param("fechaDesde") LocalDate fechaDesde,
                                                                       @Param("fechaHasta") LocalDate fechaHasta);


}
