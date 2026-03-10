package com.calero.lili.api.modRRHH.modRRHHCabecera;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.enums.TipoRubro;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.api.modRRHH.RhPeriodosEntity;
import com.calero.lili.api.modRRHH.RhPeriodosRepository;
import com.calero.lili.api.modRRHH.modRRHHCabecera.builder.RhRolCabeceraBuilder;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.FilterRolCabeceraDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.RolCabeceraDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.dto.RolCabeceraPDFDto;
import com.calero.lili.api.modRRHH.modRRHHCabecera.projection.ReporteRolCabeceraProjection;
import com.calero.lili.api.modRRHH.modRolNovedades.RhRolNovedadesEntity;
import com.calero.lili.api.modRRHH.modRolNovedades.RhRolNovedadesRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class RhRolCabeceraService {

    private final RhRolCabeceraRepository rhRolCabeceraRepository;
    private final RhPeriodosRepository rhPeriodosRepository;
    private final RhRolNovedadesRepository rhRolNovedadesRepository;
    private final RhRolCabeceraBuilder rhRolCabeceraBuilder;
    private final AdEmpresasRepository adEmpresasRepository;
    private final FormatoValores formatoValores;


    public void procesarRolCabecera(Long idData, Long idEmpresa, FilterRolCabeceraDto model) {
        RhPeriodosEntity periodo = validarPeriodo(idData, idEmpresa, model);
        List<RhRolNovedadesEntity> novedades = obtenerNovedades(idData, idEmpresa, periodo);
        setearCabercera(idData, idEmpresa, periodo, obtenerMapTerceroValores(novedades),
                obtenerSueldoBase(novedades), obtenerListadoNovedadesPorTercero(novedades));
    }

    private void setearCabercera(Long idData, Long idEmpresa, RhPeriodosEntity periodo,
                                 Map<GeTerceroEntity, Map<TipoRubro, BigDecimal>> geTerceroEntityMapMap,
                                 Map<GeTerceroEntity, BigDecimal> sueldoBasePorTercero,
                                 Map<GeTerceroEntity, List<RhRolNovedadesEntity>> novedadesPorTercero) {

        List<RhRolCabeceraEntity> rolesCabecera = new ArrayList<>();

        geTerceroEntityMapMap.forEach((tercero, totalesPorTipo) -> {

            BigDecimal sueldoBase = sueldoBasePorTercero.getOrDefault(tercero, BigDecimal.ZERO);

            BigDecimal ingresos = totalesPorTipo
                    .getOrDefault(TipoRubro.I, BigDecimal.ZERO);

            BigDecimal egresos = totalesPorTipo
                    .getOrDefault(TipoRubro.E, BigDecimal.ZERO);

            BigDecimal neto = ingresos.subtract(egresos);

            RhRolCabeceraEntity cabecera = rhRolCabeceraBuilder.builderCabecera(idData, idEmpresa, periodo);
            cabecera.setSueldoBase(sueldoBase);
            cabecera.setTotalIngresos(ingresos);
            cabecera.setTotalDeducciones(egresos);
            cabecera.setNetoPagar(neto);
            cabecera.setTercero(tercero);
            cabecera.setDetalles(rhRolCabeceraBuilder.builderListDetalle(novedadesPorTercero.get(tercero)));
            rolesCabecera.add(cabecera);
        });

        if (!rolesCabecera.isEmpty()) {
            rhRolCabeceraRepository.saveAll(rolesCabecera);
        }

    }

    private Map<GeTerceroEntity, BigDecimal> obtenerSueldoBase(List<RhRolNovedadesEntity> novedades) {
        return novedades.stream()
                .filter(n -> "ING-001".equals(n.getRubros().getCodigo()))
                .collect(Collectors.toMap(
                        RhRolNovedadesEntity::getTercero,
                        RhRolNovedadesEntity::getValor,
                        BigDecimal::add));
    }

    private Map<GeTerceroEntity, List<RhRolNovedadesEntity>> obtenerListadoNovedadesPorTercero(List<RhRolNovedadesEntity> novedades) {
        return novedades.stream()
                .collect(Collectors.groupingBy(RhRolNovedadesEntity::getTercero));
    }

    private Map<GeTerceroEntity, Map<TipoRubro, BigDecimal>> obtenerMapTerceroValores(List<RhRolNovedadesEntity> novedades) {

        Map<GeTerceroEntity, List<RhRolNovedadesEntity>> novedadesPorTercero = obtenerListadoNovedadesPorTercero(novedades);


        return novedadesPorTercero.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.groupingBy(
                                        novedad -> novedad.getRubros().getTipo(),
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                RhRolNovedadesEntity::getValor,
                                                BigDecimal::add
                                        )
                                ))
                ));

    }


    private List<RhRolNovedadesEntity> obtenerNovedades(Long idData, Long idEmpresa, RhPeriodosEntity periodo) {
        List<RhRolNovedadesEntity> novedades = rhRolNovedadesRepository
                .findByAllPeriodoId(idData, idEmpresa, periodo.getIdPeriodo());

        if (novedades.isEmpty()) {
            throw new GeneralException("No se encontraron novedades para el período seleccionado.");
        }

        return novedades;
    }

    private RhPeriodosEntity validarPeriodo(Long idData, Long idEmpresa, FilterRolCabeceraDto model) {
        return rhPeriodosRepository.findByAnioAndMes(idData, idEmpresa, model.getAnio(), model.getMes())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No se encontró el" +
                        " período para el año {0} y mes {1}", model.getAnio(), model.getMes())));
    }


    public List<RolCabeceraDto> getAll(Long idData, Long idEmpresa, FilterRolCabeceraDto model) {
        RhPeriodosEntity periodo = validarPeriodo(idData, idEmpresa, model);
        return rhRolCabeceraRepository.getReportRolCabeceraDetalles(idData, idEmpresa, periodo.getIdPeriodo(), model.getIdTercero())
                .stream()
                .map(rhRolCabeceraBuilder::builderResponse)
                .toList();

    }


    public void generateExcelCabecera(Long idData, Long idEmpresa,
                                      HttpServletResponse response, FilterRolCabeceraDto model) {


        List<ReporteRolCabeceraProjection> reporte = rhRolCabeceraRepository.getRolCabeceraForPeriodo(idData, idEmpresa,
                model.getFechaDesde(), model.getFechaHasta());

        log.info("Iniciando generación del excel de reporte mayor");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "pagos_trabajadores" + LocalDateTime.now() + ".xlsx" + "\"");

        if (!reporte.isEmpty()) {

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                XSSFSheet sheet = workbook.createSheet("Facturas");
                XSSFRow headerRow = sheet.createRow(0);

                String[] columnNames = {"identificación", "apellidos", "nombres", "tipo identificación",
                        "establecimiento", "residencia", "pais residencia", "convenio", "tipo codigo discapacidad",
                        "porcentaje discapacidad", "id discapacidad", "codigo salario",
                        "ingresos", "comisiones", "sobresueldos", "decimo tercero", "decimo cuarto", "fondos", "salario digno",
                        "utilidades", "aporte iess", "gasto vivienda", "gasto salud", "gasto alimentacion", "gasto salud",
                        "gasto educación", "gasto vestimenta", "gasto turismo", "discapacidad", "tercera edad",
                        "asumido empleador", "base imponible", "causado", "retenido", "fecha generación"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < reporte.size(); i++) {
                    ReporteRolCabeceraProjection cabecera = reporte.get(i);
                    XSSFRow row = sheet.createRow(i + 1);

                    row.createCell(0).setCellValue(cabecera.getNumeroIdentificacion());
                    row.createCell(1).setCellValue(cabecera.getApellidos());
                    row.createCell(2).setCellValue(cabecera.getNombres());
                    row.createCell(3).setCellValue(cabecera.getTipoIdentificacion());
                    row.createCell(4).setCellValue(cabecera.getCodigoEstablecimiento());
                    row.createCell(5).setCellValue(cabecera.getCodigoResidencia());
                    row.createCell(6).setCellValue(cabecera.getCodigoPais());
                    row.createCell(7).setCellValue(cabecera.getAplicaConvenio());
                    row.createCell(8).setCellValue(cabecera.getTipoIdDiscapacidad());
                    row.createCell(9).setCellValue(cabecera.getPorcentajeDiscapacidad());
                    row.createCell(10).setCellValue(cabecera.getIdDiscapacidad());
                    row.createCell(11).setCellValue(cabecera.getCodigoSalario());
                    row.createCell(12).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getIngresos()));
                    row.createCell(13).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getComisiones()));
                    row.createCell(14).setCellValue(formatoValores.convertirBigDecimalToString(BigDecimal.ZERO));
                    row.createCell(15).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getDecimoTercero()));
                    row.createCell(16).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getDecimoCuarto()));
                    row.createCell(17).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getFondosDeReserva()));
                    row.createCell(18).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getSalarioDigno()));
                    row.createCell(19).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getParticipacionUtilidades()));
                    //ingngotemp
                    row.createCell(20).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getAporteIessPersonal()));
                    row.createCell(21).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosVivienda()));
                    row.createCell(22).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosSalud()));
                    row.createCell(23).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosAlimentacion()));
                    row.createCell(24).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosEducacion()));
                    row.createCell(25).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosVestimenta()));
                    row.createCell(26).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getGastosTurismo()));
                    //rebajagp
                    row.createCell(27).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getExoneracionDiscapacidad()));
                    row.createCell(28).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getExoneracionTerceraEdad()));
                    row.createCell(29).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getImpuestoRentaAsumidoEmpleador()));

                    // ingotremp	dedotremp	rebotremp
                    row.createCell(30).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getBaseImponible()));
                    row.createCell(31).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getImpuestoCausado()));
                    row.createCell(32).setCellValue(formatoValores.convertirBigDecimalToString(cabecera.getImpuestoRentaRetenido()));
                    // retotremp
                    row.createCell(33).setCellValue(DateUtils.toString(cabecera.getFechaGeneracion()));


                }

                try (OutputStream os = response.getOutputStream()) {
                    workbook.write(os);
                }

            } catch (IOException e) {
                log.error("Error al crear el archivo Excel", e);
                throw new GeneralException("Error al crear el archivo Excel");
            }

        }


    }


    public byte[] generatePdfCabecera(Long idData, Long idEmpresa, FilterRolCabeceraDto model) {

        List<ReporteRolCabeceraProjection> reporte = rhRolCabeceraRepository.getRolCabeceraForPeriodo(idData, idEmpresa,
                model.getFechaDesde(), model.getFechaHasta());

        List<RolCabeceraPDFDto> camposValores = new ArrayList<>();

        if (!reporte.isEmpty()) {

            for (ReporteRolCabeceraProjection item : reporte) {
                camposValores.add(rhRolCabeceraBuilder.builderPdfCabecera(item,
                        getTotalGastosPersonales(item), getTotalTerceraEdadDiscapacidad(item)));
            }
            AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));

            return generatePdf(camposValores, empresa, reporte);
        } else {
            throw new GeneralException("No existen datos para mostrar");
        }
    }

    private byte[] generatePdf(List<RolCabeceraPDFDto> camposValores,
                               AdEmpresaEntity empresa, List<ReporteRolCabeceraProjection> reporte) {

        try {

            InputStream reporteStream = new ClassPathResource("report-pagos-empleados.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = null;


            dataSource = new JRBeanCollectionDataSource(camposValores);
            Map<String, String> mapValores = mapValoresTotales(reporte);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", empresa.getRazonSocial());
            parametros.put("fecha", DateUtils.toString(LocalDate.now()));
            parametros.put("total_general_sueldo", mapValores.get("total_general_sueldo"));
            parametros.put("total_general_sobreSueldos", mapValores.get("total_general_sobreSueldos"));
            parametros.put("total_general_otros", mapValores.get("total_general_otros"));
            parametros.put("total_general_decimoTercero", mapValores.get("total_general_decimoTercero"));
            parametros.put("total_general_decimoCuarto", mapValores.get("total_general_decimoCuarto"));
            parametros.put("total_general_fondos", mapValores.get("total_general_fondos"));
            parametros.put("total_general_utilidades", mapValores.get("total_general_utilidades"));
            parametros.put("total_general_iessPersonal", mapValores.get("total_general_iessPersonal"));
            parametros.put("total_general_gastosPersonales", mapValores.get("total_general_gastosPersonales"));
            parametros.put("total_general_terceraEdadYDiscapacitados", mapValores.get("total_general_terceraEdadYDiscapacitados"));
            parametros.put("total_general_ingresosOtrosEmpleadores", mapValores.get("total_general_ingresosOtrosEmpleadores"));
            parametros.put("total_general_iessOtrosEmpleadores", mapValores.get("total_general_iessOtrosEmpleadores"));
            parametros.put("total_general_baseImponible", mapValores.get("total_general_baseImponible"));
            parametros.put("total_general_retenidoEsteEmpleador", mapValores.get("total_general_retenidoEsteEmpleador"));
            parametros.put("total_general_retenidoOtrosEmpleador", mapValores.get("total_general_retenidoOtrosEmpleador"));
            parametros.put("total_asumido", mapValores.get("total_asumido"));
            parametros.put("total_ingresos_no_gravados", mapValores.get("total_ingresos_no_gravados"));
            parametros.put("total_salario_digno", mapValores.get("total_salario_digno"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);


        } catch (Exception exception) {
            throw new GeneralException("Error al generar el PDF de Pagos de Empleados");
        }

    }

    private Map<String, String> mapValoresTotales(List<ReporteRolCabeceraProjection> reporte) {

        Map<String, String> valoresTotales = new HashMap<>();

        String totalSueldo = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getIngresos)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalDecimoTercero = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getDecimoTercero)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalDecimoCuarto = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getDecimoCuarto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalFondos = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getFondosDeReserva)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalUtilidad = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getFondosDeReserva)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalIessPersonal = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getAporteIessPersonal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalGastosPersonales = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(item ->
                        Stream.of(item.getGastosVivienda(), item.getGastosSalud(), item.getGastosAlimentacion(),
                                        item.getGastosEducacion(), item.getGastosVestimenta(), item.getGastosTurismo())
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalTerceraEdadDiscapacidad = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(item ->
                        Stream.of(item.getExoneracionTerceraEdad(), item.getExoneracionDiscapacidad())
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalIngresoOtrosEmpleadores = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getIngresosGravadosOtrosEmpleadores)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        String totalIeesOtrosEmpleadores = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getIessOtrosEmpleadores)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalBaseImponible = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getBaseImponible)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalRetenido = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getRetenidoAsumidoOtrosEmpleadores)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalAsumido = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getImpuestoRentaAsumidoEmpleador)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalIngresoNoGravados = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getOtrosIngresosRelacionDependenciaNoRentaGravada)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        String totalSalarioDigno = formatoValores.convertirBigDecimalToStringPDF(reporte.stream()
                .map(ReporteRolCabeceraProjection::getSalarioDigno)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        valoresTotales.put("total_general_sueldo", totalSueldo);
        valoresTotales.put("total_general_sobreSueldos", formatoValores.convertirBigDecimalToString(BigDecimal.ZERO));
        valoresTotales.put("total_general_otros", totalIngresoNoGravados);
        valoresTotales.put("total_general_decimoTercero", totalDecimoTercero);
        valoresTotales.put("total_general_decimoCuarto", totalDecimoCuarto);
        valoresTotales.put("total_general_fondos", totalFondos);
        valoresTotales.put("total_general_utilidades", totalUtilidad);
        valoresTotales.put("total_general_iessPersonal", totalIessPersonal);
        valoresTotales.put("total_general_gastosPersonales", totalGastosPersonales);
        valoresTotales.put("total_general_terceraEdadYDiscapacitados", totalTerceraEdadDiscapacidad);
        valoresTotales.put("total_general_ingresosOtrosEmpleadores", totalIngresoOtrosEmpleadores);
        valoresTotales.put("total_general_iessOtrosEmpleadores", totalIeesOtrosEmpleadores);
        valoresTotales.put("total_general_baseImponible", totalBaseImponible);
        valoresTotales.put("total_general_retenidoEsteEmpleador", formatoValores.convertirBigDecimalToString(BigDecimal.ZERO));
        valoresTotales.put("total_general_retenidoOtrosEmpleador", totalRetenido);
        valoresTotales.put("total_asumido", totalAsumido);
        valoresTotales.put("total_ingresos_no_gravados", totalIngresoNoGravados);
        valoresTotales.put("total_salario_digno", totalSalarioDigno);


        return valoresTotales;
    }


    private static BigDecimal getTotalGastosPersonales(ReporteRolCabeceraProjection item) {
        return item.getGastosVivienda()
                .add(item.getGastosSalud())
                .add(item.getGastosAlimentacion())
                .add(item.getGastosEducacion())
                .add(item.getGastosVestimenta())
                .add(item.getGastosTurismo());
    }

    private static BigDecimal getTotalTerceraEdadDiscapacidad(ReporteRolCabeceraProjection item) {
        return item.getExoneracionTerceraEdad().add(item.getExoneracionDiscapacidad());
    }

}
