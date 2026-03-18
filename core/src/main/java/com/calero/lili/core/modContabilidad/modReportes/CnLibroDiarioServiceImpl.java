package com.calero.lili.core.modContabilidad.modReportes;

import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.LibroDiarioDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.FilaReporteMayorContableDto;
import com.calero.lili.core.modContabilidad.modReportes.builder.CnLibroDiarioBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.utils.DateUtils;
import com.lowagie.text.DocumentException;
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
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class CnLibroDiarioServiceImpl {


    private final CnReportesRepository cnReportesRepository;
    private final CnLibroDiarioBuilder cnLibroDiarioBuilder;
    private final AdEmpresasRepository adEmpresasRepository;


    /**
     * Genera y escribe un archivo Excel (XLSX) con el Libro Diario filtrado.
     * <p>
     * Flujo principal:
     * - Consulta los registros mediante `cnReportesRepository` y transforma a `LibroDiarioDto`.
     * - Construye un `XSSFWorkbook` con encabezados y filas por cada detalle de asiento.
     * - Escribe el workbook en el `HttpServletResponse` estableciendo los encabezados para forzar la descarga.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param response  HttpServletResponse donde se escribirá el archivo Excel
     * @param filters   filtros de búsqueda (sucursal, fechaEmisionDesde, fechaEmisionHasta)
     * @throws IOException      cuando ocurre un error al crear o escribir el archivo Excel
     * @throws GeneralException cuando no existen datos a mostrar
     */
    public void reportLibroDiarioExcel(Long idData, Long idEmpresa,
                                       HttpServletResponse response, FilterListDto filters) throws IOException {


        List<LibroDiarioDto> detalles = cnReportesRepository
                .findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta())
                .stream()
                .map(cnLibroDiarioBuilder::builderLibroDiario)
                .toList();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "libro-diario_" + LocalDateTime.now() + ".xlsx" + "\"");


        if (!detalles.isEmpty()) {

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                XSSFSheet sheet = workbook.createSheet("libroDiario");
                XSSFRow headerRow = sheet.createRow(0);

                String[] columnNames = {"Fecha Asiento", "Número Asiento", "Concepto", "Codigo Cuenta",
                        "Cuenta", "Número Documento", "Tipo Documento", "Debe", "Haber"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < detalles.size(); i++) {
                    LibroDiarioDto libroDiario = detalles.get(i);
                    XSSFRow row = sheet.createRow(i + 1);

                    for (int j = 0; j < libroDiario.getDetalleCuentas().size(); j++) {
                        LibroDiarioDto.DetalleCuenta detail = libroDiario.getDetalleCuentas().get(j);
                        XSSFRow detailRow = sheet.createRow(i + 1 + j);

                        detailRow.createCell(0).setCellValue(libroDiario.getFechaAsiento());
                        detailRow.createCell(1).setCellValue(libroDiario.getNumeroAsiento());
                        detailRow.createCell(2).setCellValue(libroDiario.getConcepto());
                        detailRow.createCell(3).setCellValue(detail.getCodigoCuenta());
                        detailRow.createCell(4).setCellValue(detail.getNombreCuenta());
                        detailRow.createCell(5).setCellValue(detail.getNumeroDocumento());
                        detailRow.createCell(6).setCellValue(detail.getTipoDocumento());
                        detailRow.createCell(7).setCellValue(detail.getDebe().toString());
                        detailRow.createCell(8).setCellValue(detail.getHaber().toString());
                    }

                }

                try (OutputStream os = response.getOutputStream()) {
                    workbook.write(os);
                }

            } catch (IOException e) {
                log.error("Error al crear el archivo Excel", e);
                throw e;
            }

        } else {
            throw new GeneralException("No existen datos a mostrar");
        }

    }

    /**
     * Genera y devuelve el PDF del Libro Diario para la empresa y rango de fechas indicados.
     * <p>
     * Pasos:
     * - Construye la lista de filas para el reporte usando `mapToReporte`.
     * - Recupera los datos de la empresa y compila la plantilla JasperReports.
     * - Llena el reporte con parámetros (empresa, sucursal, fechas, fechaActual) y exporta a PDF.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (sucursal, fechaEmisionDesde, fechaEmisionHasta)
     * @return arreglo de bytes con el contenido del PDF generado
     * @throws DocumentException cuando hay un error específico de Jasper/Document al compilar o generar el reporte
     * @throws GeneralException  cuando no existen datos a mostrar o ocurre cualquier otro error durante la generación
     */
    public byte[] generarReporteLibroDiarioPDF(Long idData, Long idEmpresa,
                                               FilterListDto filters) throws DocumentException {

        try {
            List<FilaReporteMayorContableDto> detalles = mapToReporte(idData, idEmpresa, filters);

            if (!detalles.isEmpty()) {
                AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa con id {0} no existe", idEmpresa)));


                InputStream reporteStream = new ClassPathResource("report-libro-diario.jrxml").getInputStream();

                JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalles);

                Map<String, Object> parametros = new HashMap<>();
                parametros.put("empresa", empresa.getRazonSocial());
                parametros.put("sucursal", filters.getSucursal());
                parametros.put("fechaDesde", DateUtils.toString(filters.getFechaEmisionDesde()));
                parametros.put("fechaHasta", DateUtils.toString(filters.getFechaEmisionHasta()));
                parametros.put("fechaActual", DateUtils.toString(LocalDate.now()));

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

                return JasperExportManager.exportReportToPdf(jasperPrint);
            } else {
                throw new GeneralException("No existen datos a mostrar");
            }

        } catch (Exception exception) {
            throw new GeneralException("Error al generar el PDF del libro diario");
        }

    }

    /**
     * Convierte los registros obtenidos a la estructura plana utilizada por el reporte (FilaReporteMayorContableDto).
     * <p>
     * Detalles:
     * - Recupera los `LibroDiarioDto` mediante el repositorio y el builder.
     * - Recorre cada asiento y sus `detalleCuentas`, creando una fila por detalle.
     * - Ignora asientos sin detalle.
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (sucursal, fechaEmisionDesde, fechaEmisionHasta)
     * @return lista de {@link FilaReporteMayorContableDto} lista para consumo por JasperReports o exportaciones
     */
    public List<FilaReporteMayorContableDto> mapToReporte(Long idData, Long idEmpresa, FilterListDto filters) {

        List<LibroDiarioDto> lista = cnReportesRepository
                .findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta())
                .stream()
                .map(cnLibroDiarioBuilder::builderLibroDiario)
                .toList();


        List<FilaReporteMayorContableDto> resultado = new ArrayList<>();

        for (LibroDiarioDto asiento : lista) {
            if (asiento.getDetalleCuentas() == null) continue;

            for (LibroDiarioDto.DetalleCuenta d : asiento.getDetalleCuentas()) {

                resultado.add(
                        FilaReporteMayorContableDto.builder()
                                .fechaAsiento(asiento.getFechaAsiento())
                                .numeroAsiento(asiento.getNumeroAsiento())
                                .concepto(asiento.getConcepto())
                                .codigoCuenta(d.getCodigoCuenta())
                                .nombreCuenta(d.getNombreCuenta())
                                .numeroDocumento(d.getNumeroDocumento())
                                .tipoDocumento(d.getTipoDocumento())
                                .debe(d.getDebe())
                                .haber(d.getHaber())
                                .build()
                );
            }
        }

        return resultado;
    }

    /**
     * Devuelve una respuesta paginada con el Libro Diario de acuerdo a filtros y paginación.
     * <p>
     * Proceso:
     * - Consulta `cnReportesRepository.findAllPaginate` obteniendo una página de `CnAsientosEntity`.
     * - Transforma cada entidad a `LibroDiarioDto` mediante el builder.
     * - Construye y devuelve un `PaginatedDto<LibroDiarioDto>` con contenido y metadatos (`Paginator`).
     *
     * @param idData    identificador lógico del entorno/data
     * @param idEmpresa identificador de la empresa
     * @param filters   filtros de búsqueda (sucursal, fechaEmisionDesde, fechaEmisionHasta)
     * @param pageable  parámetros de paginación y ordenamiento
     * @return {@link PaginatedDto}\<LibroDiarioDto> con la página de resultados y su paginador
     */
    public PaginatedDto<LibroDiarioDto> reportLibroDiario(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<CnAsientosEntity> page = cnReportesRepository
                .findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), pageable);

        List<LibroDiarioDto> dtoList = page.stream().map(cnLibroDiarioBuilder::builderLibroDiario).toList();

        PaginatedDto paginatedDto = new PaginatedDto();
        paginatedDto.setContent(dtoList);

        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginated);

        return paginatedDto;
    }

}
