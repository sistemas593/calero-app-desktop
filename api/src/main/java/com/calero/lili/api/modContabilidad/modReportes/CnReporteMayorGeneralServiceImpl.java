package com.calero.lili.api.modContabilidad.modReportes;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.api.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.api.modContabilidad.modCentroCostos.CnCentroCostosRepository;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.api.modContabilidad.modReportes.builder.CnReporteMayorGeneralBuilder;
import com.calero.lili.api.modContabilidad.modReportes.dto.DetalleMayorDtoPDF;
import com.calero.lili.api.modContabilidad.modReportes.dto.MayorGeneralDto;
import com.calero.lili.api.modContabilidad.modReportes.projection.CabeceraMayorProjection;
import com.calero.lili.api.modContabilidad.modReportes.projection.MayorGeneralProjection;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class CnReporteMayorGeneralServiceImpl {

    private final CnReportesRepository cnReportesRepository;
    private final CnReporteMayorGeneralBuilder cnReporteMayorGeneralBuilder;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final CnCentroCostosRepository cnCentroCostosRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final FormatoValores formatoValores;

    /**
     * Genera y devuelve el reporte de Mayor General para la cuenta indicada.
     * <p>
     * Flujo:
     * - Valida que la cuenta exista y no sea mayor.
     * - Determina la lógica de consulta según si la fecha inicial es enero o no.
     * - Consulta el repositorio para obtener la cabecera y los detalles paginados.
     * - Convierte las proyecciones a DTOs, construye el objeto de paginación y arma la respuesta {@link MayorGeneralDto}.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (código de cuenta, sucursal, fechaEmisionDesde, fechaEmisionHasta, ...)
     * @param pageable  parámetros de paginación y ordenamiento
     * @return {@link MayorGeneralDto} con la cabecera y detalle paginado (o detalle nulo si no hay registros)
     * @throws GeneralException cuando la cuenta no existe, es cuenta mayor o ocurre un error en la obtención de datos
     */
    public MayorGeneralDto reporteMayorGeneral(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<MayorGeneralProjection> detalles = null;
        CabeceraMayorProjection cabecera = null;

        validarCuenta(idData, idEmpresa, filters.getCodigoCuenta());
        validarCentroCostos(idData, idEmpresa, filters.getCodigoCentroCostos());
        if (DateUtils.validacionFechaEnero(filters.getFechaEmisionDesde())) {

            cabecera = cnReportesRepository
                    .cabeceraMayorGeneralFechaInicialEnero(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getCodigoCentroCostos());

            detalles = cnReportesRepository
                    .reporteDetallesPaginadoMayorGeneralFechaInicialEnero(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getCodigoCentroCostos(), pageable);
        } else {

            cabecera = cnReportesRepository
                    .cabeceraMayorGeneralFechaDiferentePrimeroEnero(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), DateUtils.getFechaInicio(filters.getFechaEmisionDesde()), filters.getCodigoCentroCostos());

            detalles = cnReportesRepository
                    .reportePaginadoMayorGeneralFechaInicialDiferenteEnero(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            DateUtils.getFechaInicio(filters.getFechaEmisionDesde()), filters.getCodigoCentroCostos(), pageable);
        }


        List<MayorGeneralDto.DetalleMayorGeneralDto> dtoList =
                detalles.stream().map(cnReporteMayorGeneralBuilder::builderMayorGeneralDto).toList();

        PaginatedDto paginatedDto = new PaginatedDto();
        paginatedDto.setContent(dtoList);

        Paginator paginated = new Paginator();
        paginated.setTotalElements(detalles.getTotalElements());
        paginated.setTotalPages(detalles.getTotalPages());
        paginated.setNumberOfElements(detalles.getNumberOfElements());
        paginated.setSize(detalles.getSize());
        paginated.setFirst(detalles.isFirst());
        paginated.setLast(detalles.isLast());
        paginated.setPageNumber(detalles.getPageable().getPageNumber());
        paginated.setPageSize(detalles.getPageable().getPageSize());
        paginated.setEmpty(detalles.isEmpty());
        paginated.setNumber(detalles.getNumber());

        paginatedDto.setPaginator(paginated);

        MayorGeneralDto response = new MayorGeneralDto();

        if (Objects.nonNull(cabecera)) {
            response.setCabecera(cnReporteMayorGeneralBuilder.builderMayorCabeceraGeneralDto(cabecera));
        } else {

            CnPlanCuentaEntity cuenta = cnPlanCuentasRepository.findByCodigoCuenta(idData, idEmpresa, filters.getCodigoCuenta())
                    .orElseThrow(() -> new GeneralException("No existe plan de cuenta"));
            response.setCabecera(cnReporteMayorGeneralBuilder.builderDefault(cuenta));

        }

        if (!dtoList.isEmpty()) {
            response.setDetalle(paginatedDto);
        } else {
            response.setDetalle(null);
        }

        return response;
    }

    private void validarCentroCostos(Long idData, Long idEmpresa, String codigoCentroCostos) {
        if (Objects.nonNull(codigoCentroCostos)) {

            CnCentroCostosEntity centroCostos = cnCentroCostosRepository.findByCentroCostosCodigo
                    (idData, idEmpresa, codigoCentroCostos).orElseThrow(() -> new GeneralException("No existe el centro de costos"));

            if (centroCostos.getMayor()) {
                throw new GeneralException("El centro de costos ha buscar no puede ser mayor");
            }

        }
    }

    /**
     * Valida que la cuenta exista y que no sea una cuenta mayor.
     * <p>
     * - Lanza {@link GeneralException} si la cuenta no se encuentra.
     * - Lanza {@link GeneralException} si la cuenta es marcada como mayor (no corresponde para este reporte).
     *
     * @param idData       identificador lógico del entorno/data
     * @param idEmpresa    identificador de la empresa
     * @param codigoCuenta código de la cuenta a validar
     * @throws GeneralException cuando la cuenta no existe o es cuenta mayor
     */
    private void validarCuenta(Long idData, Long idEmpresa, String codigoCuenta) {

        CnPlanCuentaEntity cnPlanCuentaEntity = cnPlanCuentasRepository.findByCodigoCuenta(idData, idEmpresa, codigoCuenta)
                .orElseThrow(() -> new GeneralException("El codigo de cuenta no corresponde a ninguna cuenta"));

        if (cnPlanCuentaEntity.getMayor()) {
            throw new GeneralException("No se puede obtener el reporte Mayor General de una cuenta mayor");
        }
    }

    /**
     * Genera y escribe en el {@link HttpServletResponse} un archivo Excel (XLSX) con el reporte de Mayor General.
     * <p>
     * Flujo:
     * - Valida la cuenta.
     * - Consulta los detalles (lista completa) según la lógica de fechas.
     * - Convierte las proyecciones a DTOs y construye un {@link XSSFWorkbook} con encabezados y filas.
     * - Escribe el workbook en la salida del response para forzar la descarga.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param response  {@link HttpServletResponse} donde se escribirá el archivo Excel
     * @param filters   filtros de búsqueda (código de cuenta, sucursal, fechaEmisionDesde, fechaEmisionHasta, ...)
     * @throws IOException      cuando ocurre un error al crear o escribir el archivo Excel
     * @throws GeneralException cuando la cuenta no es válida
     */
    public void reporteMayorGeneralExcel(Long idData, Long idEmpresa,
                                         HttpServletResponse response, FilterListDto filters) throws IOException {

        validarCentroCostos(idData, idEmpresa, filters.getCodigoCentroCostos());
        validarCuenta(idData, idEmpresa, filters.getCodigoCuenta());
        List<MayorGeneralProjection> detalles = null;

        if (DateUtils.validacionFechaEnero(filters.getFechaEmisionDesde())) {


            detalles = cnReportesRepository
                    .reporteDetallesPaginadoMayorGeneralList(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());
        } else {

            detalles = cnReportesRepository
                    .reportePaginadoMayorGeneralList(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            DateUtils.getFechaInicio(filters.getFechaEmisionDesde()));
        }

        List<MayorGeneralDto.DetalleMayorGeneralDto> dtoList =
                detalles.stream().map(cnReporteMayorGeneralBuilder::builderMayorGeneralDto).toList();


        log.info("Iniciando generación del excel de reporte mayor: {}", filters.getCodigoDocumento());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "reporte-mayor_" + LocalDateTime.now() + ".xlsx" + "\"");
        if (!dtoList.isEmpty()) {

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                XSSFSheet sheet = workbook.createSheet("Facturas");
                XSSFRow headerRow = sheet.createRow(0);

                String[] columnNames = {"fechaAsiento", "tipoAsiento", "numeroAsiento", "tipoDocumento",
                        "numeroDocumento", "concepto", "debe", "haber", "saldo"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < dtoList.size(); i++) {
                    MayorGeneralDto.DetalleMayorGeneralDto mayorDetalle = dtoList.get(i);
                    XSSFRow row = sheet.createRow(i + 1);

                    row.createCell(0).setCellValue(mayorDetalle.getFechaAsiento());
                    row.createCell(1).setCellValue(mayorDetalle.getTipoAsiento());
                    row.createCell(2).setCellValue(mayorDetalle.getNumeroAsiento());
                    row.createCell(3).setCellValue(mayorDetalle.getTipoDocumento());
                    row.createCell(4).setCellValue(mayorDetalle.getNumeroDocumento());
                    row.createCell(5).setCellValue(mayorDetalle.getConcepto());
                    row.createCell(6).setCellValue(mayorDetalle.getDebe().toString());
                    row.createCell(7).setCellValue(mayorDetalle.getHaber().toString());
                    row.createCell(8).setCellValue(mayorDetalle.getSaldo().toString());

                }

                try (OutputStream os = response.getOutputStream()) {
                    workbook.write(os);
                }

            } catch (IOException e) {
                log.error("Error al crear el archivo Excel", e);
                throw e;
            }

        }

    }

    /**
     * Genera y devuelve el PDF del reporte de Mayor General para la cuenta y rango indicados.
     * <p>
     * Flujo:
     * - Valida la cuenta.
     * - Consulta los detalles (lista completa) según la lógica de fechas.
     * - Convierte las proyecciones a DTOs y delega en {@link #getReportPdf(List, FilterListDto, Long, Long)} para generar el PDF.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (código de cuenta, sucursal, fechaEmisionDesde, fechaEmisionHasta, ...)
     * @return arreglo de bytes con el contenido del PDF generado
     * @throws GeneralException cuando la cuenta no es válida o ocurre un error durante la generación del PDF
     */
    public byte[] reporteMayorGeneralPdf(Long idData, Long idEmpresa, FilterListDto filters) {

        validarCentroCostos(idData, idEmpresa, filters.getCodigoCentroCostos());
        validarCuenta(idData, idEmpresa, filters.getCodigoCuenta());
        List<MayorGeneralProjection> detalles = null;

        if (DateUtils.validacionFechaEnero(filters.getFechaEmisionDesde())) {


            detalles = cnReportesRepository
                    .reporteDetallesPaginadoMayorGeneralList(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());
        } else {

            detalles = cnReportesRepository
                    .reportePaginadoMayorGeneralList(idData, idEmpresa, filters.getSucursal(), filters.getCodigoCuenta(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            DateUtils.getFechaInicio(filters.getFechaEmisionDesde()));
        }


        List<MayorGeneralDto.DetalleMayorGeneralDto> dtoList =
                detalles.stream().map(cnReporteMayorGeneralBuilder::builderMayorGeneralDto).toList();

        return getReportPdf(dtoList, filters, idData, idEmpresa);

    }

    /**
     * Compila y rellena la plantilla JasperReports para producir el PDF del Mayor General.
     * <p>
     * Pasos:
     * - Calcula totales (debe/haber) a partir de la lista.
     * - Recupera la información de la empresa para el encabezado.
     * - Compila la plantilla JRXML desde classpath, crea la fuente de datos y parámetros del reporte.
     * - Llena el reporte y exporta el resultado a PDF en bytes.
     *
     * @param list      lista de {@link MayorGeneralDto.DetalleMayorGeneralDto} que alimenta el reporte
     * @param filters   filtros usados en la generación (se muestran en el reporte)
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @return arreglo de bytes con el PDF generado
     * @throws GeneralException cuando ocurre cualquier error durante la compilación, llenado o exportación del PDF
     */
    private byte[] getReportPdf(List<MayorGeneralDto.DetalleMayorGeneralDto> list, FilterListDto filters,
                                Long idData, Long idEmpresa) {
        try {

            BigDecimal debeTotal = list.stream()
                    .map(MayorGeneralDto.DetalleMayorGeneralDto::getDebe)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal haberTotal = list.stream()
                    .map(MayorGeneralDto.DetalleMayorGeneralDto::getHaber)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));

            InputStream reporteStream = new ClassPathResource("report-mayor-general.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRBeanCollectionDataSource dataSource = null;


            dataSource = new JRBeanCollectionDataSource(validarListPdf(list));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", empresa.getRazonSocial());
            parametros.put("sucursal", filters.getSucursal());
            parametros.put("fechaDesde", DateUtils.toString(filters.getFechaEmisionDesde()));
            parametros.put("fechaHasta", DateUtils.toString(filters.getFechaEmisionHasta()));
            parametros.put("fechaActual", DateUtils.toString(LocalDate.now()));
            parametros.put("cuentaDesde", filters.getCodigoCuenta());
            parametros.put("totalDebe", formatoValores.convertirBigDecimalToStringPDF(debeTotal));
            parametros.put("totalHaber", formatoValores.convertirBigDecimalToStringPDF(haberTotal));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);


        } catch (Exception exception) {
            throw new GeneralException("Error al generar el PDF Mayor General");
        }
    }

    /**
     * Valida y transforma la lista de detalles a la estructura usada por el PDF.
     * <p>
     * - Invoca al builder {@code cnReporteMayorGeneralBuilder} para construir los DTOs específicos de PDF.
     *
     * @param list lista de {@link MayorGeneralDto.DetalleMayorGeneralDto} a transformar
     * @return lista de {@link DetalleMayorDtoPDF} lista para usarse como fuente de datos del reporte
     */
    private List<DetalleMayorDtoPDF> validarListPdf(List<MayorGeneralDto.DetalleMayorGeneralDto> list) {
        return cnReporteMayorGeneralBuilder.builderListMayorGeneralPdf(list);
    }


}
