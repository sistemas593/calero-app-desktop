package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modReportes.projection.BalanceValoresProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.CabeceraMayorProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.MayorGeneralProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.ResultadosProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.SaldoInicialBCProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CnReportesRepository extends JpaRepository<CnAsientosEntity, UUID>, JpaSpecificationExecutor<CnAsientosEntity> {


    @Query(
            value = "SELECT cpc.id_cuenta, cpc.codigo_cuenta, cpc.codigo_cuenta_original, cpc.cuenta, " +
                    "SUM(cad.debe) AS debe, SUM(cad.haber) AS haber, cpc.mayor " +
                    "FROM cn_asientos ca " +
                    "JOIN cn_asientos_detalle cad ON cad.id_asiento = ca.id_asiento " +
                    "JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta " +
                    "WHERE cad.id_empresa = :idEmpresa " +
                    "AND cad.id_data = :idData " +
                    "AND (:cuentaInicial IS NULL OR cpc.codigo_cuenta >= :cuentaInicial) " +
                    "AND (:cuentaFinal IS NULL OR cpc.codigo_cuenta <= :cuentaFinal) " +
                    "AND ca.fecha_asiento BETWEEN :fechaAsientoInicio AND :fechaAsientoHasta " +
                    "AND ca.sucursal = :sucursal " +
                    "AND ca.tipo_asiento <> 'SI' " +
                    "GROUP BY cpc.codigo_cuenta, cpc.cuenta, cpc.id_cuenta, cpc.mayor " +
                    "ORDER BY cpc.codigo_cuenta",
            nativeQuery = true
    )
    List<BalanceValoresProjection> getValoresBalanceComprobacion(@Param("idData") Long idData,
                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                 @Param("sucursal") String sucursal,
                                                                 @Param("cuentaInicial") String cuentaInicial,
                                                                 @Param("cuentaFinal") String cuentaFinal,
                                                                 @Param("fechaAsientoInicio") LocalDate fechaAsientoInicio,
                                                                 @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta);


    @Query(
            value = "SELECT cad2.id_cuenta, cpc2.codigo_cuenta, cpc2.codigo_cuenta_original, cpc2.cuenta, " +
                    "COALESCE(SUM(cad2.debe) - SUM(cad2.haber), 0) AS saldo_inicial,  cpc2.mayor " +
                    "FROM cn_asientos ca2 " +
                    "JOIN cn_asientos_detalle cad2 ON cad2.id_asiento = ca2.id_asiento " +
                    "JOIN cn_plan_cuentas cpc2 ON cpc2.id_cuenta = cad2.id_cuenta " +
                    "WHERE cad2.id_empresa = :idEmpresa " +
                    "AND cad2.id_data = :idData " +
                    "AND (:cuentaInicial IS NULL OR cpc2.codigo_cuenta >= :cuentaInicial) " +
                    "AND (:cuentaFinal IS NULL OR cpc2.codigo_cuenta <= :cuentaFinal) " +
                    "AND cad2.fecha_documento BETWEEN :fechaInicioSaldo AND :fechaFinalSaldo " +
                    "AND ca2.sucursal = :sucursal " +
                    " AND ca2.tipo_asiento = 'SI' " +
                    "GROUP BY cad2.id_cuenta, cpc2.codigo_cuenta,  cpc2.codigo_cuenta_original, cpc2.cuenta, cpc2.mayor " +
                    "ORDER BY cpc2.codigo_cuenta",
            nativeQuery = true)
    List<SaldoInicialBCProjection> getSaldosInicialesBalanceConTipoAsiento(@Param("idData") Long idData,
                                                                           @Param("idEmpresa") Long idEmpresa,
                                                                           @Param("sucursal") String sucursal,
                                                                           @Param("cuentaInicial") String cuentaInicial,
                                                                           @Param("cuentaFinal") String cuentaFinal,
                                                                           @Param("fechaInicioSaldo") LocalDate fechaInicioSaldo,
                                                                           @Param("fechaFinalSaldo") LocalDate fechaFinalSaldo);


    @Query(
            value = "SELECT cad2.id_cuenta, cpc2.codigo_cuenta, cpc2.codigo_cuenta_original, cpc2.cuenta, cpc2.mayor, " +
                    "COALESCE(SUM(cad2.debe) - SUM(cad2.haber), 0) AS saldo_inicial, cpc2.mayor " +
                    "FROM cn_asientos ca2 " +
                    "JOIN cn_asientos_detalle cad2 ON cad2.id_asiento = ca2.id_asiento " +
                    "JOIN cn_plan_cuentas cpc2 ON cpc2.id_cuenta = cad2.id_cuenta " +
                    "WHERE cad2.id_empresa = :idEmpresa " +
                    "AND cad2.id_data = :idData " +
                    "AND (:cuentaInicial IS NULL OR cpc2.codigo_cuenta >= :cuentaInicial) " +
                    "AND (:cuentaFinal IS NULL OR cpc2.codigo_cuenta <= :cuentaFinal) " +
                    "AND cad2.fecha_documento BETWEEN :fechaInicioSaldo AND :fechaFinalSaldo " +
                    "AND ca2.sucursal = :sucursal " +
                    "GROUP BY cad2.id_cuenta, cpc2.codigo_cuenta,  cpc2.codigo_cuenta_original, cpc2.cuenta, cpc2.mayor " +
                    "ORDER BY cpc2.codigo_cuenta",
            nativeQuery = true)
    List<SaldoInicialBCProjection> getSaldosInicialesBalanceComprobacionSinTipoAsiento(@Param("idData") Long idData,
                                                                                       @Param("idEmpresa") Long idEmpresa,
                                                                                       @Param("sucursal") String sucursal,
                                                                                       @Param("cuentaInicial") String cuentaInicial,
                                                                                       @Param("cuentaFinal") String cuentaFinal,
                                                                                       @Param("fechaInicioSaldo") LocalDate fechaInicioSaldo,
                                                                                       @Param("fechaFinalSaldo") LocalDate fechaFinalSaldo);


    @Query(value = "select cpc2.codigo_cuenta," +
            " cpc2.cuenta," +
            " coalesce(sum(cad2.debe) - sum(cad2.haber), 0) as saldoInicial " +
            " from cn_asientos ca2 " +
            "join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
            "join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta " +
            "left join cn_centro_costos ccc on cad2.id_centro_costos = ccc.id_centro_costos " +
            "where ca2.id_data = :idData and ca2.id_empresa = :idEmpresa and ca2.sucursal = :sucursal " +
            "and ca2.tipo_asiento = 'SI' and cpc2.codigo_cuenta = :codigoCuenta " +
            "and (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos) " +
            "and ca2.fecha_asiento between ( cast(:fechaAsientoDesde as date)) and ( cast(:fechaAsientoDesde as date)) " +
            "group by cpc2.codigo_cuenta, cpc2.cuenta ", nativeQuery = true)
    CabeceraMayorProjection cabeceraMayorGeneralFechaInicialEnero(@Param("idData") Long idData,
                                                                  @Param("idEmpresa") Long idEmpresa,
                                                                  @Param("sucursal") String sucursal,
                                                                  @Param("codigoCuenta") String codigoCuenta,
                                                                  @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                  @Param("codigoCentroCostos") String codigoCentroCostos);


    @Query(
            value = "select ca.id_asiento, ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento," +
                    " cad.tipo_documento, cad.numero_documento, ca.concepto," +
                    " cad.debe, cad.haber, ccc.codigo_centro_costos, ccc.centro_costos," +
                    " (select coalesce(sum(cad2.debe) - sum(cad2.haber), 0) " +
                    "from cn_asientos ca2 join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento" +
                    " join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta" +
                    " left join cn_centro_costos ccc2 on cad2.id_centro_costos = ccc2.id_centro_costos" +
                    " where ca2.id_data = :idData and ca2.id_empresa = :idEmpresa" +
                    " and ca2.sucursal = :sucursal and ca2.tipo_asiento = 'SI' and" +
                    " cpc2.codigo_cuenta = :codigoCuenta and (:codigoCentroCostos is null or ccc2.codigo_centro_costos = :codigoCentroCostos)" +
                    " and ca2.fecha_asiento between cast(:fechaAsientoDesde as date)" +
                    " and cast(:fechaAsientoDesde as date)) + sum(case when cad.debe > 0 then cad.debe when cad.haber > 0 then -cad.haber else 0 end) over (order by ca.fecha_asiento, ca.numero_asiento rows between unbounded preceding and current row) as saldo_acumulado" +
                    " from cn_asientos ca " +
                    "join cn_asientos_detalle cad on ca.id_asiento = cad.id_asiento " +
                    "join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta " +
                    "left join cn_centro_costos ccc on cad.id_centro_costos = ccc.id_centro_costos " +
                    "where ca.id_data = :idData and ca.id_empresa = :idEmpresa and ca.sucursal = :sucursal " +
                    "and ca.tipo_asiento <> 'SI' " +
                    "and (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos) " +
                    "and ca.fecha_asiento between :fechaAsientoDesde and :fechaAsientoHasta " +
                    "and cpc.codigo_cuenta = :codigoCuenta order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento",

            countQuery = "SELECT COUNT(ca.id_asiento) " +
                    "FROM cn_asientos ca " +
                    "JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento " +
                    "JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta " +
                    "LEFT JOIN cn_centro_costos ccc ON cad.id_centro_costos = ccc.id_centro_costos " +
                    "WHERE ca.id_data = :idData " +
                    "AND ca.id_empresa = :idEmpresa " +
                    "AND ca.sucursal = :sucursal " +
                    "AND ca.tipo_asiento <> 'SI' " +
                    "AND ca.fecha_asiento BETWEEN :fechaAsientoDesde " +
                    "AND :fechaAsientoHasta " +
                    "AND cpc.codigo_cuenta = :codigoCuenta " +
                    "AND (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos)",
            nativeQuery = true)
    Page<MayorGeneralProjection> reporteDetallesPaginadoMayorGeneralFechaInicialEnero(@Param("idData") Long idData,
                                                                                      @Param("idEmpresa") Long idEmpresa,
                                                                                      @Param("sucursal") String sucursal,
                                                                                      @Param("codigoCuenta") String codigoCuenta,
                                                                                      @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                                      @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta,
                                                                                      @Param("codigoCentroCostos") String codigoCentroCostos,
                                                                                      Pageable pageable);


    @Query(value = "select cpc2.codigo_cuenta," +
            " cpc2.cuenta," +
            " coalesce(sum(cad2.debe) - sum(cad2.haber), 0) as saldoInicial " +
            " from cn_asientos ca2 " +
            " join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
            " join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta " +
            " left join cn_centro_costos ccc on cad2.id_centro_costos = ccc.id_centro_costos " +
            " where ca2.id_data = :idData and ca2.id_empresa = :idEmpresa and ca2.sucursal = :sucursal " +
            " and cpc2.codigo_cuenta = :codigoCuenta " +
            " and (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos) " +
            " and ca2.fecha_asiento between " +
            "       (cast(:fechaInicio as date)) " +
            "       and ( cast(:fechaAsientoDesde as date) - 1 )" +
            " group by cpc2.codigo_cuenta, cpc2.cuenta",
            nativeQuery = true)
    CabeceraMayorProjection cabeceraMayorGeneralFechaDiferentePrimeroEnero(@Param("idData") Long idData,
                                                                           @Param("idEmpresa") Long idEmpresa,
                                                                           @Param("sucursal") String sucursal,
                                                                           @Param("codigoCuenta") String codigoCuenta,
                                                                           @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                           @Param("fechaInicio") LocalDate fechaInicio,
                                                                           @Param("codigoCentroCostos") String codigoCentroCostos);


    @Query(
            value = "select " +
                    " ca.id_asiento,  " +
                    " ca.fecha_asiento, " +
                    " ca.tipo_asiento, " +
                    " ca.numero_asiento, " +
                    " cad.tipo_documento, " +
                    " cad.numero_documento, " +
                    " ca.concepto, " +
                    " cad.debe, " +
                    " cad.haber, " +
                    " ccc.codigo_centro_costos, " +
                    " ccc.centro_costos, " +
                    " ( " +
                    "   (select coalesce(sum(cad2.debe) - sum(cad2.haber), 0) " +
                    "    from cn_asientos ca2 " +
                    "    join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
                    "    join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta " +
                    "    left join cn_centro_costos ccc on cad2.id_centro_costos = ccc.id_centro_costos " +
                    "    where ca2.id_data = :idData " +
                    "      and ca2.id_empresa = :idEmpresa " +
                    "      and ca2.sucursal = :sucursal " +
                    "      and cpc2.codigo_cuenta = :codigoCuenta " +
                    "      and (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos) " +
                    "      and ca2.fecha_asiento between " +
                    "            cast(:fechaInicio as date) " +
                    "            and ( cast(:fechaAsientoDesde as date) - 1 ) " +
                    "   ) " +
                    "   + " +
                    "   sum( " +
                    "       case " +
                    "           when cad.debe > 0 then cad.debe " +
                    "           when cad.haber > 0 then -cad.haber " +
                    "           else 0 " +
                    "       end " +
                    "   ) over ( " +
                    "       order by ca.fecha_asiento, ca.numero_asiento " +
                    "       rows between unbounded preceding and current row " +
                    "   ) " +
                    " ) as saldo_acumulado, " +
                    " ccc.codigo_centro_costos, ccc.centro_costos " +
                    " from cn_asientos ca " +
                    " join cn_asientos_detalle cad on ca.id_asiento = cad.id_asiento " +
                    " join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta " +
                    " left join cn_centro_costos ccc on cad.id_centro_costos = ccc.id_centro_costos " +
                    " where ca.id_data = :idData " +
                    "   and ca.id_empresa = :idEmpresa " +
                    "   and ca.sucursal = :sucursal " +
                    "   and ca.tipo_asiento <> 'SI' " +
                    "   and ca.fecha_asiento between :fechaAsientoDesde and :fechaAsientoHasta " +
                    "   and cpc.codigo_cuenta = :codigoCuenta " +
                    "   and (:codigoCentroCostos is null or ccc.codigo_centro_costos = :codigoCentroCostos) " +
                    " order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento",

            countQuery =
                    "SELECT COUNT(ca.id_asiento) " +
                            "FROM cn_asientos ca " +
                            "JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento " +
                            "JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta " +
                            "LEFT JOIN cn_centro_costos ccc ON cad.id_centro_costos = ccc.id_centro_costos " +
                            "WHERE ca.id_data = :idData " +
                            "AND ca.id_empresa = :idEmpresa " +
                            "AND ca.sucursal = :sucursal " +
                            "AND ca.tipo_asiento <> 'SI' " +
                            "AND ca.fecha_asiento BETWEEN :fechaAsientoDesde AND :fechaAsientoHasta " +
                            "AND (:codigoCentroCostos IS NULL OR ccc.codigo_centro_costos = :codigoCentroCostos) " +
                            "AND cpc.codigo_cuenta = :codigoCuenta",

            nativeQuery = true
    )
    Page<MayorGeneralProjection> reportePaginadoMayorGeneralFechaInicialDiferenteEnero(@Param("idData") Long idData,
                                                                                       @Param("idEmpresa") Long idEmpresa,
                                                                                       @Param("sucursal") String sucursal,
                                                                                       @Param("codigoCuenta") String codigoCuenta,
                                                                                       @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                                       @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta,
                                                                                       @Param("fechaInicio") LocalDate fechaInicio,
                                                                                       @Param("codigoCentroCostos") String codigoCentroCostos,
                                                                                       Pageable pageable);


    @Query(
            value = "select ca.id_asiento, ca.fecha_asiento, cpc.cuenta, ca.tipo_asiento, ca.numero_asiento," +
                    " cad.tipo_documento, cad.numero_documento, ca.concepto, cad.debe, cad.haber," +
                    " (select coalesce(sum(cad2.debe) - sum(cad2.haber), 0) " +
                    " from cn_asientos ca2 join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
                    " join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta where ca2.id_data = :idData " +
                    " and ca2.id_empresa = :idEmpresa and ca2.sucursal = :sucursal " +
                    " and ca2.tipo_asiento = 'SI' and cpc2.codigo_cuenta = :codigoCuenta " +
                    " and ca2.fecha_asiento between cast(:fechaAsientoDesde as date)" +
                    " and cast(:fechaAsientoDesde as date))" +
                    " + sum(case when cad.debe > 0 then cad.debe when cad.haber > 0 then -cad.haber else 0 end) " +
                    " over (order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento rows between unbounded preceding and current row)" + " as saldo_acumulado " +
                    " from cn_asientos ca join cn_asientos_detalle cad on ca.id_asiento = cad.id_asiento " +
                    " join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta where ca.id_data = :idData " +
                    " and ca.id_empresa = :idEmpresa and ca.sucursal = :sucursal and ca.tipo_asiento <> 'SI' " +
                    " and ca.fecha_asiento BETWEEN :fechaAsientoDesde " +
                    " AND :fechaAsientoHasta and cpc.codigo_cuenta = :codigoCuenta order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento",
            nativeQuery = true)
    List<MayorGeneralProjection> reporteDetallesPaginadoMayorGeneralList(@Param("idData") Long idData,
                                                                         @Param("idEmpresa") Long idEmpresa,
                                                                         @Param("sucursal") String sucursal,
                                                                         @Param("codigoCuenta") String codigoCuenta,
                                                                         @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                         @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta);


    @Query(
            value = "select " +
                    " ca.fecha_asiento, " +
                    " cpc.cuenta ," +
                    " ca.tipo_asiento, " +
                    " ca.numero_asiento, " +
                    " cad.tipo_documento, " +
                    " cad.numero_documento, " +
                    " ca.concepto, " +
                    " cad.debe, " +
                    " cad.haber, " +
                    " ( " +
                    "   (select coalesce(sum(cad2.debe) - sum(cad2.haber), 0) " +
                    "    from cn_asientos ca2 " +
                    "    join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
                    "    join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta " +
                    "    where ca2.id_data = :idData " +
                    "      and ca2.id_empresa = :idEmpresa " +
                    "      and ca2.sucursal = :sucursal " +
                    "      and cpc2.codigo_cuenta = :codigoCuenta " +
                    "      and ca2.fecha_asiento between " +
                    "            cast(:fechaInicio as date) " +
                    "            and ( cast(:fechaAsientoDesde as date) - 1 ) " +
                    "   ) " +
                    "   + " +
                    "   sum( " +
                    "       case " +
                    "           when cad.debe > 0 then cad.debe " +
                    "           when cad.haber > 0 then -cad.haber " +
                    "           else 0 " +
                    "       end " +
                    "   ) over ( " +
                    "       order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento " +
                    "       rows between unbounded preceding and current row " +
                    "   ) " +
                    " ) as saldo_acumulado " +
                    " from cn_asientos ca " +
                    " join cn_asientos_detalle cad on ca.id_asiento = cad.id_asiento " +
                    " join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta " +
                    " where ca.id_data = :idData " +
                    "   and ca.id_empresa = :idEmpresa " +
                    "   and ca.sucursal = :sucursal " +
                    "   and ca.tipo_asiento <> 'SI' " +
                    "   and ca.fecha_asiento between :fechaAsientoDesde and :fechaAsientoHasta " +
                    "   and cpc.codigo_cuenta = :codigoCuenta " +
                    " order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento",
            nativeQuery = true
    )
    List<MayorGeneralProjection> reportePaginadoMayorGeneralList(@Param("idData") Long idData,
                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                 @Param("sucursal") String sucursal,
                                                                 @Param("codigoCuenta") String codigoCuenta,
                                                                 @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                 @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta,
                                                                 @Param("fechaInicio") LocalDate fechaInicio);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM CnAsientosEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
            "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )  " +
            ")"
    )
    List<CnAsientosEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM CnAsientosEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
            "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM CnAsientosEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
                    "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )")
    Page<CnAsientosEntity> findAllPaginate(Long idData, Long idEmpresa, String sucursal, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta, Pageable pageable);


    @Query(value = """
                           select
                           	cpc.id_cuenta,
                               cpc.codigo_cuenta AS codigoCuenta,
                               cpc.cuenta AS cuenta,
                               cpc.codigo_cuenta_original as codigoCuentaOriginal,
                               COALESCE(SUM(cad.debe) - SUM(cad.haber), 0) AS saldoFinal,
                               cpc.mayor,
                               cpc.grupo
                           FROM cn_asientos ca
                           JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento
                           JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta
                           WHERE ca.fecha_asiento BETWEEN :fechaAsientoDesde AND :fechaAsientoHasta
                             AND ca.sucursal = :sucursal
                             AND ca.id_data = :idData
                             AND ca.id_empresa = :idEmpresa
                             AND cpc.grupo in (1,2,3)
                           GROUP by cpc.id_cuenta, cpc.codigo_cuenta, cpc.cuenta,cpc.codigo_cuenta_original, cpc.mayor, cpc.grupo
                           ORDER BY cpc.codigo_cuenta
            """, nativeQuery = true)
    List<BalanceValoresProjection> reporteEstadoFinancieroPageable(@Param("idData") Long idData,
                                                                   @Param("idEmpresa") Long idEmpresa,
                                                                   @Param("fechaAsientoDesde") LocalDate fechaDesde,
                                                                   @Param("fechaAsientoHasta") LocalDate fechaHasta,
                                                                   @Param("sucursal") String sucursal
    );


    @Query(value = """
                           select
                           	cpc.id_cuenta,
                               cpc.codigo_cuenta AS codigoCuenta,
                               cpc.codigo_cuenta_original as codigoCuentaOriginal,
                               cpc.cuenta AS cuenta,
                               COALESCE(SUM(cad.debe) - SUM(cad.haber), 0) AS saldoFinal,
                               cpc.mayor,
                               cpc.grupo,
                               ccc.centro_costos,
                               ccc.codigo_centro_costos
                           FROM cn_asientos ca
                           JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento
                           JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta
                           LEFT JOIN cn_centro_costos ccc ON cad.id_centro_costos = ccc.id_centro_costos
                           WHERE ca.fecha_asiento BETWEEN :fechaAsientoDesde AND :fechaAsientoHasta
                             AND ca.sucursal = :sucursal
                             AND ca.id_data = :idData
                             AND ca.id_empresa = :idEmpresa
                             AND cpc.grupo in (4,5,6)
                             AND (:codigoCentroCostos is null or ccc.codigo_centro_costos LIKE CONCAT(:codigoCentroCostos, '%'))
                           GROUP by cpc.id_cuenta, cpc.codigo_cuenta, cpc.codigo_cuenta_original, cpc.cuenta, cpc.mayor, cpc.grupo,
                             ccc.centro_costos, ccc.codigo_centro_costos
                           ORDER BY cpc.codigo_cuenta
            """, nativeQuery = true)
    List<BalanceValoresProjection> reporteEstadoPerdidasGanancias(@Param("idData") Long idData,
                                                                  @Param("idEmpresa") Long idEmpresa,
                                                                  @Param("fechaAsientoDesde") LocalDate fechaDesde,
                                                                  @Param("fechaAsientoHasta") LocalDate fechaHasta,
                                                                  @Param("sucursal") String sucursal,
                                                                  @Param("codigoCentroCostos") String codigoCentroCostos);


    @Query(
            value = "select " +
                    " ca.fecha_asiento, " +
                    " ca.tipo_asiento, " +
                    " ca.numero_asiento, " +
                    " cad.tipo_documento, " +
                    " cad.numero_documento, " +
                    " ca.concepto, " +
                    " cad.debe, " +
                    " cad.haber, " +
                    " ( " +
                    "   (select coalesce(sum(cad2.debe) - sum(cad2.haber), 0) " +
                    "    from cn_asientos ca2 " +
                    "    join cn_asientos_detalle cad2 on ca2.id_asiento = cad2.id_asiento " +
                    "    join cn_plan_cuentas cpc2 on cpc2.id_cuenta = cad2.id_cuenta " +
                    "    where ca2.id_data = :idData " +
                    "      and ca2.id_empresa = :idEmpresa " +
                    "      and ca2.sucursal = :sucursal " +
                    "      and cpc2.codigo_cuenta = :codigoCuenta " +
                    "      and ca2.fecha_asiento between " +
                    "            cast(:fechaInicio as date) " +
                    "            and ( cast(:fechaAsientoDesde as date) - 1 ) " +
                    "   ) " +
                    "   + " +
                    "   sum( " +
                    "       case " +
                    "           when cad.debe > 0 then cad.debe " +
                    "           when cad.haber > 0 then -cad.haber " +
                    "           else 0 " +
                    "       end " +
                    "   ) over ( " +
                    "       order by ca.fecha_asiento, ca.numero_asiento " +
                    "       rows between unbounded preceding and current row " +
                    "   ) " +
                    " ) as saldo_acumulado " +
                    " from cn_asientos ca " +
                    " join cn_asientos_detalle cad on ca.id_asiento = cad.id_asiento " +
                    " join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta " +
                    " where ca.id_data = :idData " +
                    "   and ca.id_empresa = :idEmpresa " +
                    "   and ca.sucursal = :sucursal " +
                    "   and ca.tipo_asiento <> 'SI' " +
                    "   and ca.fecha_asiento between :fechaAsientoDesde and :fechaAsientoHasta " +
                    "   and cpc.codigo_cuenta = :codigoCuenta " +
                    " order by ca.fecha_asiento, ca.tipo_asiento, ca.numero_asiento",
            nativeQuery = true)
    List<MayorGeneralProjection> reportePaginadoMayorGeneralPdf(@Param("idData") Long idData,
                                                                @Param("idEmpresa") Long idEmpresa,
                                                                @Param("sucursal") String sucursal,
                                                                @Param("codigoCuenta") String codigoCuenta,
                                                                @Param("fechaAsientoDesde") LocalDate fechaAsientoDesde,
                                                                @Param("fechaAsientoHasta") LocalDate fechaAsientoHasta,
                                                                @Param("fechaInicio") LocalDate fechaInicio);


    @Query(value = """
            select coalesce(sum(cad.debe),0) - coalesce(sum(cad.haber),0) as utilidad
            from cn_asientos ca
            join cn_asientos_detalle cad on cad.id_asiento = ca.id_asiento
            join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta
            where cpc.grupo in (4,5,6)
              and ca.fecha_asiento between :fechaDesde and :fechaHasta
              and ca.sucursal = :sucursal
              and ca.id_data = :idData
              and ca.id_empresa = :idEmpresa
            """, nativeQuery = true)
    BigDecimal obtenerResultadoUtilidad(@Param("idData") Long idData,
                                        @Param("idEmpresa") Long idEmpresa,
                                        @Param("fechaDesde") LocalDate fechaDesde,
                                        @Param("fechaHasta") LocalDate fechaHasta,
                                        @Param("sucursal") String sucursal
    );


    @Query(value = """
             SELECT
                 date_trunc('month', ca.fecha_asiento)::date AS mes_fecha,
                 cpc.id_cuenta,
                 cpc.codigo_cuenta AS codigoCuenta,
                 cpc.codigo_cuenta_original as codigoCuentaOriginal,
                 cpc.cuenta,
                 COALESCE(SUM(cad.debe) - SUM(cad.haber), 0) AS saldoFinal,
                 cpc.mayor,
                 cpc.grupo
             FROM cn_asientos ca
             JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento
             JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta
             WHERE ca.fecha_asiento BETWEEN :fechaAsientoDesde AND :fechaAsientoHasta
               AND ca.sucursal = :sucursal
               AND ca.id_data = :idData
               AND ca.id_empresa = :idEmpresa
               AND cpc.grupo IN (1,2,3)
               AND ca.tipo_asiento <> 'SI'
             GROUP BY
                 date_trunc('month', ca.fecha_asiento),
                 cpc.id_cuenta, cpc.codigo_cuenta, cpc.codigo_cuenta_original ,cpc.cuenta, cpc.mayor, cpc.grupo
             ORDER BY
                 mes_fecha, cpc.codigo_cuenta
            """, nativeQuery = true)
    List<BalanceValoresProjection> reporteEstadoFinancieroPageableComparativo(@Param("idData") Long idData,
                                                                              @Param("idEmpresa") Long idEmpresa,
                                                                              @Param("fechaAsientoDesde") LocalDate fechaDesde,
                                                                              @Param("fechaAsientoHasta") LocalDate fechaHasta,
                                                                              @Param("sucursal") String sucursal
    );


    @Query(
            value = "SELECT cad2.id_cuenta, cpc2.codigo_cuenta, cpc2.codigo_cuenta_original, cpc2.cuenta, " +
                    "COALESCE(SUM(cad2.debe) - SUM(cad2.haber), 0) AS saldo_inicial " +
                    "FROM cn_asientos ca2 " +
                    "JOIN cn_asientos_detalle cad2 ON cad2.id_asiento = ca2.id_asiento " +
                    "JOIN cn_plan_cuentas cpc2 ON cpc2.id_cuenta = cad2.id_cuenta " +
                    "WHERE cad2.id_empresa = :idEmpresa " +
                    "AND cad2.id_data = :idData " +
                    "AND cad2.fecha_documento BETWEEN :fechaInicioSaldo AND :fechaFinalSaldo " +
                    "AND ca2.sucursal = :sucursal " +
                    " AND ca2.tipo_asiento = 'SI' " +
                    "GROUP BY cad2.id_cuenta, cpc2.codigo_cuenta, cpc2.codigo_cuenta_original ,cpc2.cuenta",
            nativeQuery = true)
    List<SaldoInicialBCProjection> getSaldosInicialesEstadoFinancieroComparativo(@Param("idData") Long idData,
                                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                                 @Param("sucursal") String sucursal,
                                                                                 @Param("fechaInicioSaldo") LocalDate fechaInicioSaldo,
                                                                                 @Param("fechaFinalSaldo") LocalDate fechaFinalSaldo);


    @Query(value = """
            select
                      date_trunc('month', ca.fecha_asiento)::date AS mes_fecha,
                    coalesce(sum(cad.debe),0) - coalesce(sum(cad.haber),0) as resultado
                        from cn_asientos ca
                        join cn_asientos_detalle cad on cad.id_asiento = ca.id_asiento
                        join cn_plan_cuentas cpc on cpc.id_cuenta = cad.id_cuenta
                        where cpc.grupo in (4,5,6)
                          and ca.fecha_asiento between :fechaDesde and :fechaHasta
                          and ca.sucursal = :sucursal
                          and ca.id_data = :idData
                          and ca.id_empresa = :idEmpresa
                          group by date_trunc('month', ca.fecha_asiento)
                          order by mes_fecha
            """, nativeQuery = true)
    List<ResultadosProjection> obtenerResultadoUtilidadComparativo(@Param("idData") Long idData,
                                                                   @Param("idEmpresa") Long idEmpresa,
                                                                   @Param("fechaDesde") LocalDate fechaDesde,
                                                                   @Param("fechaHasta") LocalDate fechaHasta,
                                                                   @Param("sucursal") String sucursal
    );


    @Query(value = """
                    select
                        date_trunc('month', ca.fecha_asiento)::date AS mes_fecha,
                        cpc.id_cuenta,
                        cpc.codigo_cuenta AS codigoCuenta,
                        cpc.codigo_cuenta_original as codigoCuentaOriginal,
                        cpc.cuenta AS cuenta,
                        COALESCE(SUM(cad.debe) - SUM(cad.haber), 0) AS saldoFinal,
                        cpc.mayor,
                        cpc.grupo,
                        ccc.centro_costos,
                        ccc.codigo_centro_costos
                    FROM cn_asientos ca
                    JOIN cn_asientos_detalle cad ON ca.id_asiento = cad.id_asiento
                    JOIN cn_plan_cuentas cpc ON cpc.id_cuenta = cad.id_cuenta
                    LEFT JOIN cn_centro_costos ccc ON cad.id_centro_costos = ccc.id_centro_costos
                    WHERE ca.fecha_asiento BETWEEN :fechaAsientoDesde AND :fechaAsientoHasta
                      AND ca.sucursal = :sucursal
                      AND ca.id_data = :idData
                      AND ca.id_empresa = :idEmpresa
                      AND cpc.grupo in (4,5,6)
                      AND (:codigoCentroCostos is null or ccc.codigo_centro_costos LIKE CONCAT(:codigoCentroCostos, '%'))
                      GROUP by date_trunc('month', ca.fecha_asiento),
                      cpc.id_cuenta, cpc.codigo_cuenta, cpc.codigo_cuenta_original, cpc.cuenta, cpc.mayor, cpc.grupo,
                      ccc.centro_costos, ccc.codigo_centro_costos
                      ORDER BY  mes_fecha,cpc.codigo_cuenta
            
            """, nativeQuery = true)
    List<BalanceValoresProjection> reporteEstadoPerdidasGananciasComparativo(@Param("idData") Long idData,
                                                                             @Param("idEmpresa") Long idEmpresa,
                                                                             @Param("fechaAsientoDesde") LocalDate fechaDesde,
                                                                             @Param("fechaAsientoHasta") LocalDate fechaHasta,
                                                                             @Param("sucursal") String sucursal,
                                                                             @Param("codigoCentroCostos") String codigoCentroCostos);


}
