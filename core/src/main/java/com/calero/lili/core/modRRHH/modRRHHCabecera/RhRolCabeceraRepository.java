package com.calero.lili.core.modRRHH.modRRHHCabecera;

import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.core.modRRHH.modRRHHCabecera.projection.TalonResumenTrabajadoresProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RhRolCabeceraRepository extends JpaRepository<RhRolCabeceraEntity, UUID> {


    @Query(value = "SELECT gt.numero_identificacion, gt.tercero," +
            " gt.tipo_identificacion, rt.codigo_establecimiento," +
            " rt.codigo_residencia, rt.codigo_pais, rt.aplica_convenio," +
            " rt.tipo_discapacidad, rt.porcentaje_discapacidad," +
            " rt.id_discapacidad, rt.codigo_salario, rrc.fecha_generacion," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-001' THEN rd.valor END), 0.00) AS ingresos," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-005' THEN rd.valor END), 0.00) AS comisiones," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-015' THEN rd.valor END), 0.00) AS otros_ingresos_gravados_impuesto_renta," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-009' THEN rd.valor END), 0.00) AS decimo_tercero," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-010' THEN rd.valor END), 0.00) AS decimo_cuarto," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-013' THEN rd.valor END), 0.00) AS fondos_de_reserva," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-016' THEN rd.valor END), 0.00) AS participacion_utilidades," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-001' THEN rd.valor END), 0.00) AS ingresos_gravados_otros_empleadores," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-002' THEN rd.valor END), 0.00) AS iess_otros_empleadores," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-001' THEN rd.valor END), 0.00) AS gastos_vivienda," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-002' THEN rd.valor END), 0.00) AS gastos_salud," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-003' THEN rd.valor END), 0.00) AS gastos_alimentacion," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-004' THEN rd.valor END), 0.00) AS gastos_educacion," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-005' THEN rd.valor END), 0.00) AS gastos_vestimenta," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-006' THEN rd.valor END), 0.00) AS gastos_turismo," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'EXO-001' THEN rd.valor END), 0.00) AS exoneracion_discapacidad," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'EXO-002' THEN rd.valor END), 0.00) AS exoneracion_tercera_edad," +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-003' THEN rd.valor END), 0.00) AS retenido_asumido_otros_empleadores," +
            "   0.00 AS impuesto_causado, 0.00 AS base_imponible  " +
            " FROM rh_rol_cabecera rrc JOIN ge_terceros gt ON gt.id_tercero = rrc.id_tercero" +
            " JOIN rh_trabajadores rt ON rt.id_tercero = gt.id_tercero" +
            " LEFT JOIN rh_rol_detalles rd ON rd.id_rol = rrc.id_rol" +
            " LEFT JOIN rh_rubros r ON r.id_rubro = rd.id_rubro WHERE rrc.id_periodo = :idPeriodo" +
            " AND rrc.id_data =:idData AND rrc.id_empresa =:idEmpresa " +
            "AND (:idTercero is null or rrc.id_tercero = :idTercero)" +
            " GROUP BY gt.numero_identificacion, gt.tercero, gt.tipo_identificacion," +
            " rt.codigo_establecimiento, rt.codigo_residencia, rt.codigo_pais," +
            " rt.aplica_convenio, rt.tipo_discapacidad, rt.porcentaje_discapacidad," +
            " rt.id_discapacidad, rt.codigo_salario, rrc.fecha_generacion;", nativeQuery = true)
    List<ReporteRolCabeceraProjection> getReportRolCabeceraDetalles(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idPeriodo") UUID idPeriodo, @Param("idTercero") UUID idTercero);


    @Query(value = "SELECT gt.numero_identificacion, gt.tercero," +
            " gt.tipo_identificacion, rt.codigo_establecimiento," +
            " rt.codigo_residencia, rt.codigo_pais, rt.aplica_convenio," +
            " rt.tipo_discapacidad, rt.porcentaje_discapacidad, rt.enfermedad_catastrofica, rt.beneficio_prov_galapagos, rrpc.numero_cargas, " +
            " rt.id_discapacidad, rt.codigo_salario, rrc.fecha_generacion, rt.tipo_id_discapacidad, rt.apellidos, rt.nombres, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-001' THEN rd.valor END), 0.00) AS ingresos, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-005' THEN rd.valor END), 0.00) AS comisiones, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-015' THEN rd.valor END), 0.00) AS otros_ingresos_gravados_impuesto_renta, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-016' THEN rd.valor END), 0.00) AS participacion_utilidades, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-001' THEN rd.valor END), 0.00) AS ingresos_gravados_otros_empleadores, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'DES-008' THEN rd.valor END), 0.00) AS impuesto_renta_asumido_empleador, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-009' THEN rd.valor END), 0.00) AS decimo_tercero, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-010' THEN rd.valor END), 0.00) AS decimo_cuarto, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-013' THEN rd.valor END), 0.00) AS fondos_de_reserva, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-018' THEN rd.valor END), 0.00) AS salario_digno, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'ING-017' THEN rd.valor END), 0.00) AS otros_ingresos_relacion_dependencia_no_renta_gravada, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'DES-001' THEN rd.valor END), 0.00) AS aporte_iess_personal, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-002' THEN rd.valor END), 0.00) AS iess_otros_empleadores, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-001' THEN rd.valor END), 0.00) AS gastos_vivienda, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-002' THEN rd.valor END), 0.00) AS gastos_salud, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-003' THEN rd.valor END), 0.00) AS gastos_alimentacion, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-004' THEN rd.valor END), 0.00) AS gastos_educacion, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-005' THEN rd.valor END), 0.00) AS gastos_vestimenta, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'GTP-006' THEN rd.valor END), 0.00) AS gastos_turismo, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'EXO-001' THEN rd.valor END), 0.00) AS exoneracion_discapacidad, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'EXO-002' THEN rd.valor END), 0.00) AS exoneracion_tercera_edad, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'OTE-003' THEN rd.valor END), 0.00) AS retenido_asumido_otros_empleadores, " +
            "  COALESCE(MAX(CASE WHEN r.codigo = 'RET-002' THEN rd.valor END), 0.00) AS impuesto_renta_retenido, " +
            "   0.00 AS impuesto_causado, 0.00 AS base_imponible  " +
            " FROM rh_rol_cabecera rrc JOIN ge_terceros gt ON gt.id_tercero = rrc.id_tercero " +
            " JOIN rh_trabajadores rt ON rt.id_tercero = gt.id_tercero " +
            " JOIN rh_periodos rp ON rp.id_periodo = rrc.id_periodo " +
            " LEFT JOIN rh_rol_parametro_cargas rrpc on rrpc.id_tercero = rrc.id_tercero " +
            " LEFT JOIN rh_rol_detalles rd ON rd.id_rol = rrc.id_rol " +
            " LEFT JOIN rh_rubros r ON r.id_rubro = rd.id_rubro " +
            " WHERE rrc.id_data =:idData AND rrc.id_empresa =:idEmpresa " +
            " AND rp.fecha_inicio >= :fechaInicio AND rp.fecha_fin <= :fechaFin " +
            " GROUP BY gt.numero_identificacion, gt.tercero, gt.tipo_identificacion, " +
            " rt.codigo_establecimiento, rt.codigo_residencia, rt.codigo_pais, " +
            " rt.aplica_convenio, rt.tipo_discapacidad, rt.porcentaje_discapacidad," +
            " rt.id_discapacidad, rt.codigo_salario, rrc.fecha_generacion, rt.enfermedad_catastrofica, " +
            " rt.beneficio_prov_galapagos, rrpc.numero_cargas,rt.tipo_id_discapacidad, rt.apellidos, rt.nombres;", nativeQuery = true)
    List<ReporteRolCabeceraProjection> getRolCabeceraForPeriodo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);


    @Query("SELECT r FROM RhRolCabeceraEntity r WHERE r.periodos.idPeriodo = :idPeriodo AND r.idEmpresa =:idEmpresa AND r.idData =:idData")
    List<RhRolCabeceraEntity> getAllPeriodo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idPeriodo") UUID idPeriodo);


    @Query(
            value = "WITH roles_filtrados AS ( SELECT DISTINCT rrc.id_rol FROM rh_rol_cabecera rrc " +
                    "JOIN rh_periodos rp ON rp.id_periodo = rrc.id_periodo WHERE rp.fecha_fin >= :fechaInicio AND rp.fecha_inicio <= :fechaFin " +
                    "AND rrc.id_data = :idData AND rrc.id_empresa = :idEmpresa ) " +
                    "SELECT rr.codigo, rr.rubro, SUM(rrd.valor) AS total_rubro," +
                    " (SELECT COUNT(*) FROM roles_filtrados) AS total_registros FROM rh_rol_detalles rrd " +
                    "JOIN rh_rol_cabecera rrc ON rrc.id_rol = rrd.id_rol JOIN rh_rubros rr ON rr.id_rubro = rrd.id_rubro " +
                    "JOIN roles_filtrados rf ON rf.id_rol = rrc.id_rol GROUP BY rr.codigo, rr.rubro ORDER BY rr.codigo", nativeQuery = true)
    List<TalonResumenTrabajadoresProjection> getTalResumenRetencionTrabajadores(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaInicio") LocalDate fechaInicio,
                                                                                @Param("fechaFin") LocalDate fechaFin);


    @Query("SELECT r FROM RhRolCabeceraEntity r WHERE r.tercero.idTercero = :idTercero AND r.idEmpresa =:idEmpresa AND r.idData =:idData")
    Optional<RhRolCabeceraEntity> getCabeceraForTercero(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idTercero") UUID idTercero);

}
