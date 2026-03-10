package com.calero.lili.api.modVentasCotizaciones;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modVentasCotizaciones.builder.VtCotizacionBuilder;
import com.calero.lili.api.modVentasCotizaciones.dto.CreationVentasCotizacionesRequestDto;
import com.calero.lili.api.modVentasCotizaciones.dto.FilterListDto;
import com.calero.lili.api.modVentasCotizaciones.dto.GetDto;
import com.calero.lili.api.modVentasCotizaciones.dto.GetListDto;
import com.calero.lili.api.modVentasCotizaciones.dto.GetListDtoTotalizado;
import com.calero.lili.api.modVentasCotizaciones.projection.OneProjection;
import com.calero.lili.api.modVentasCotizaciones.projection.TotalesProjection;
import com.calero.lili.core.utils.DateUtils;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CotizacionesServiceImpl {

    private final CotizacionesRepository cotizacionesRepository;
    private final VtCotizacionBuilder vtCotizacionBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final GeTercerosRepository geTercerosRepository;
    private final AuditorAware<String> auditorAware;


    public ResponseDto create(Long idData, Long idEmpresa, CreationVentasCotizacionesRequestDto request) {

        Optional<OneProjection> existingFactura = cotizacionesRepository.findExistBySecuencial(idData, idEmpresa, request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La factura ya existe Secuencia: {0}", request.getSecuencial()));
        }


        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtCotizacionEntity vtCotizacion = vtCotizacionBuilder
                .builderEntity(request, idData, idEmpresa);

        vtCotizacion.setCliente(tercero);
        vtCotizacion.setEmail(tercero.getEmail());
        vtCotizacion.setTerceroNombre(tercero.getTercero());

        VtCotizacionEntity vtVentaEntity = cotizacionesRepository.save(vtCotizacion);

        return responseApiBuilder.builderResponse(vtVentaEntity.getIdCotizacion().toString());

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationVentasCotizacionesRequestDto request) {

        VtCotizacionEntity vtVentaEntity = cotizacionesRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idCotizaciones {0} no existe", idVenta)));

        if (!vtVentaEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = cotizacionesRepository.findExistBySecuencial(idData, idEmpresa, request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La factura ya existe  Secuencia: {0}", request.getSecuencial()));
            }
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtCotizacionEntity vtCotizacion = vtCotizacionBuilder
                .builderUpdateEntity(request, vtVentaEntity);


        vtCotizacion.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        vtCotizacion.setModifiedDate(LocalDateTime.now());
        vtCotizacion.setCliente(tercero);
        vtCotizacion.setEmail(tercero.getEmail());
        vtCotizacion.setTerceroNombre(tercero.getTercero());


        VtCotizacionEntity vtCotizacionEntity = cotizacionesRepository.save(vtCotizacion);
        return responseApiBuilder.builderResponse(vtCotizacionEntity.getIdCotizacion().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta) {

        VtCotizacionEntity vtVentaEntity = cotizacionesRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idCotizaciones {0} no existe", idVenta)));

        vtVentaEntity.setDelete(Boolean.TRUE);
        vtVentaEntity.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        vtVentaEntity.setDeletedDate(LocalDateTime.now());

        cotizacionesRepository.save(vtVentaEntity);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta) {

        VtCotizacionEntity vtCotizacionEntity = cotizacionesRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La cotización con ID {0} no exixte", idVenta)));

        return vtCotizacionBuilder.builderResponse(vtCotizacionEntity);
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {


        Page<VtCotizacionEntity> page = cotizacionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSecuencial(), pageable);

        List<GetListDto> dtoList = page.stream().map(vtCotizacionBuilder::builderListResponse).toList();

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

    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {


        Page<VtCotizacionEntity> page = cotizacionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSecuencial(), pageable);

        List<GetListDto> dtoList = page.stream().map(vtCotizacionBuilder::builderListResponse).toList();

        List<TotalesProjection> totalValoresProjection = cotizacionesRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSecuencial());

        GetListDtoTotalizado totalesDto = new GetListDtoTotalizado<>();
        totalesDto.setContent(dtoList);

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
        totalesDto.setPaginated(paginated);

        GetListDtoTotalizado.Totales tot = new GetListDtoTotalizado.Totales();
        tot.setValoresTotales(totalValoresProjection);

        totalesDto.setTotales(tot);
        return totalesDto;

    }


    public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws IOException {

        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        List<VtCotizacionEntity> facturas = cotizacionesRepository.findAll(idData, idEmpresa, filter.getSucursal(), filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getNumeroIdentificacion(), filter.getSecuencial());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=facturas_" + currentDateTime + ".xlsx");

        if (!facturas.isEmpty()) {
            log.info("Facturas obtenidas satisfactoriamente.");

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Facturas");
                XSSFRow headerRow = sheet.createRow(0);

                String[] columnNames = {"Documento", "Serie", "Secuencial", "FechaEmisión", "NumeroAutorizacion",
                        "NumeroIdentificación",
                        "BaseCero", "NoObjeto", "Exenta",
                        "Base15%", "Iva15%",
                        "Base5%", "Iva5%",
                        "Base8%", "Iva8%"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < facturas.size(); i++) {
                    VtCotizacionEntity factura = facturas.get(i);
                    XSSFRow row = sheet.createRow(i + 1);

                    BigDecimal baseCero = BigDecimal.valueOf(0);
                    BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                    BigDecimal baseExenta = BigDecimal.valueOf(0);

                    BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                    BigDecimal valorIva15 = BigDecimal.valueOf(0);

                    BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                    BigDecimal valorIva8 = BigDecimal.valueOf(0);

                    BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                    BigDecimal valorIva5 = BigDecimal.valueOf(0);

                    for (VtCotizacionValoresEntity valores : factura.getValoresEntity()) {
                        // IVA
                        if (valores.getCodigo().equals("2")) {
                            // cero
                            if (valores.getCodigoPorcentaje().equals("0")) {
                                baseCero = valores.getBaseImponible();
                            }
                            // noobjeto
                            if (valores.getCodigoPorcentaje().equals("6")) {
                                baseNoObjeto = valores.getBaseImponible();
                            }
                            // exenta
                            if (valores.getCodigoPorcentaje().equals("7")) {
                                baseExenta = valores.getBaseImponible();
                            }
                            //5 %
                            if (valores.getCodigoPorcentaje().equals("5")) {
                                baseGrav5 = valores.getBaseImponible();
                                valorIva5 = valores.getValor();
                            }
                            // 8%
                            if (valores.getCodigoPorcentaje().equals("8")) {
                                baseGrav8 = valores.getBaseImponible();
                                valorIva8 = valores.getValor();
                            }
                            // 15%
                            if (valores.getCodigoPorcentaje().equals("4")) {
                                baseGrav15 = valores.getBaseImponible();
                                valorIva15 = valores.getValor();
                            }
                        }
                    }

                    row.createCell(2).setCellValue(factura.getSecuencial());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmision()));
                    row.createCell(5).setCellValue(factura.getNumeroIdentificacion());

                    row.createCell(6).setCellValue(baseCero.doubleValue());

                    row.createCell(7).setCellValue(baseNoObjeto.doubleValue());
                    row.createCell(8).setCellValue(baseExenta.doubleValue());

                    row.createCell(9).setCellValue(baseGrav5.doubleValue());
                    row.createCell(10).setCellValue(valorIva5.doubleValue());

                    row.createCell(11).setCellValue(baseGrav8.doubleValue());
                    row.createCell(12).setCellValue(valorIva8.doubleValue());

                    row.createCell(13).setCellValue(baseGrav15.doubleValue());
                    row.createCell(14).setCellValue(valorIva15.doubleValue());

                }

                try (OutputStream os = response.getOutputStream()) {
                    workbook.write(os);
                }
            } catch (IOException e) {
                log.error("Error al crear el archivo Excel", e);
                throw e;
            }
        } else {
            log.warn("No se encontraron facturas con los filtros proporcionados.");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            OutputStream os = response.getOutputStream();
            os.write("No se encontraron facturas con los filtros proporcionados".getBytes());
            os.flush();
            os.close();
        }
    }


    public void exportarPDF(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {

        List<VtCotizacionEntity> facturas = cotizacionesRepository.findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSecuencial());

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=facturas_" + LocalDateTime.now() + ".pdf";
        response.setHeader(headerKey, headerValue);

        if (!facturas.isEmpty()) {

            // Iniciar el documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Crear la tabla y los encabezados del PDF
            PdfPTable table = new PdfPTable(4); // Número de columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // definimos los nombres de las columnas, excluyendo el campo XML
            String[] columnNames = {"Documento", "Serie", "Secuencial", "FechaEmisión", "NumeroAutorizacion",
                    "NumeroIdentificación",
                    "BaseCero", "NoObjeto", "Exenta",
                    "Base15%", "Iva15%",
                    "Base5%", "Iva5%",
                    "Base8%", "Iva8%"};

            // Añadir columnas
            for (String columnName : columnNames) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(Color.LIGHT_GRAY);
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(columnName));
                table.addCell(header);
            }

            // Añadir filas con los datos de las facturas
            for (VtCotizacionEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (VtCotizacionValoresEntity valores : factura.getValoresEntity()) {
                    // IVA
                    if (valores.getCodigo().equals("2")) {
                        // cero
                        if (valores.getCodigoPorcentaje().equals("0")) {
                            baseCero = valores.getBaseImponible();
                        }
                        // noobjeto
                        if (valores.getCodigoPorcentaje().equals("6")) {
                            baseNoObjeto = valores.getBaseImponible();
                        }
                        // exenta
                        if (valores.getCodigoPorcentaje().equals("7")) {
                            baseExenta = valores.getBaseImponible();
                        }
                        //5 %
                        if (valores.getCodigoPorcentaje().equals("5")) {
                            baseGrav5 = valores.getBaseImponible();
                            valorIva5 = valores.getValor();
                        }
                        // 8%
                        if (valores.getCodigoPorcentaje().equals("8")) {
                            baseGrav8 = valores.getBaseImponible();
                            valorIva8 = valores.getValor();
                        }
                        // 15%
                        if (valores.getCodigoPorcentaje().equals("4")) {
                            baseGrav15 = valores.getBaseImponible();
                            valorIva15 = valores.getValor();
                        }
                    }
                }

                table.addCell(factura.getSecuencial());
                table.addCell(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                table.addCell(factura.getNumeroIdentificacion());

                table.addCell(String.valueOf(baseCero));

                table.addCell(String.valueOf(baseNoObjeto));
                table.addCell(String.valueOf(baseExenta));

                table.addCell(String.valueOf(baseGrav5));
                table.addCell(String.valueOf(valorIva5));

                table.addCell(String.valueOf(baseGrav8));
                table.addCell(String.valueOf(valorIva8));

                table.addCell(String.valueOf(baseGrav15));
                table.addCell(String.valueOf(valorIva15));

            }

            // Añadir la tabla al documento y cerrar
            document.add(table);
            document.close();
        } else {
            // Manejar el caso en el que no se encuentren facturas
            try {
                OutputStream os = response.getOutputStream();
                os.write("No se encontraron facturas con los filtros proporcionados".getBytes());
                os.flush();
                os.close();
            } catch (IOException e) {
                log.error("Error al escribir el archivo PDF", e);
            }

        }
    }
}
