package com.calero.lili.api.modVentas.reembolsos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.api.modVentas.reembolsos.builder.VtVentaReembolsosBuilder;
import com.calero.lili.api.modVentas.reembolsos.dto.CreationRequestReembolsoDto;
import com.calero.lili.api.modVentas.reembolsos.dto.GetListDtoTotalizado;
import com.calero.lili.api.modVentas.reembolsos.dto.ResponseReembolsoDto;
import com.calero.lili.api.modVentas.reembolsos.projection.TotalesProjection;
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

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VtVentasReembolsoServiceImpl {

    private final VtVentasReembolsoRepository vtVentasReembolsoRepository;
    private final VtVentaReembolsosBuilder vtVentaReembolsosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final AuditorAware<String> auditorAware;


    public ResponseDto create(CreationRequestReembolsoDto request) {

        Optional<VtVentaReembolsosEntity> optional = vtVentasReembolsoRepository
                .findByDatosReembolso(request.getNumeroIdentificacionReemb(), request.getSerieReemb(), request.getSecuencialReemb());

        if (optional.isPresent()) {
            throw new GeneralException(MessageFormat.format("El reembolso ya existe" +
                    "Serie: {0} Secuencia: {1}", request.getSerieReemb(), request.getSecuencialReemb()));
        }

        VtVentaReembolsosEntity reembolso = vtVentasReembolsoRepository.save(vtVentaReembolsosBuilder
                .builderEntity(request));
        return responseApiBuilder.builderResponse(reembolso.getIdVentaReembolsos().toString());
    }


    public ResponseDto update(UUID id, CreationRequestReembolsoDto request) {

        Optional<VtVentaReembolsosEntity> optional = vtVentasReembolsoRepository
                .findByIdEntity(id);

        if (optional.isEmpty()) {
            throw new GeneralException(MessageFormat.format("El reembolso con el id:  {0},  no existe", id));
        }


        VtVentaReembolsosEntity update = vtVentaReembolsosBuilder.builderUpdateEntity(request, optional.get());

        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());
        vtVentasReembolsoRepository.save(update);

        return responseApiBuilder.builderResponse(update.getIdVentaReembolsos().toString());
    }

    public void delete(UUID id) {

        VtVentaReembolsosEntity reembolso = vtVentasReembolsoRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El reembolso con el id:  {0},  no existe", id)));


        reembolso.setDelete(Boolean.TRUE);
        reembolso.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        reembolso.setDeletedDate(LocalDateTime.now());

        vtVentasReembolsoRepository.save(reembolso);

    }

    public ResponseReembolsoDto findById(UUID id) {
        return vtVentaReembolsosBuilder.builderReembolso(vtVentasReembolsoRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El reembolso con el id:  {0},  no existe", id))));
    }


    public PaginatedDto<ResponseReembolsoDto> findAllPaginate(FilterListDto filters, Pageable pageable) {

        Page<VtVentaReembolsosEntity> page = vtVentasReembolsoRepository.findAllPageable(filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSecuencial(),
                filters.getNumeroIdentificacion(), filters.getSerie(), filters.getUtilizado(), pageable);

        List<ResponseReembolsoDto> dtoList = page.stream().map(vtVentaReembolsosBuilder::builderReembolso).toList();

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

    public GetListDtoTotalizado<ResponseReembolsoDto> findAllPaginateTotalizado(FilterListDto filters, Pageable pageable) {


        Page<VtVentaReembolsosEntity> page = vtVentasReembolsoRepository.findAllPageable(filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getSecuencial(),
                filters.getNumeroIdentificacion(), filters.getSerie(), filters.getUtilizado(), pageable);

        List<ResponseReembolsoDto> dtoList = page.stream().map(vtVentaReembolsosBuilder::builderReembolso).toList();

        List<TotalesProjection> totalValoresProjection = vtVentasReembolsoRepository
                .totalValores(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getSecuencial(), filters.getNumeroIdentificacion(), filters.getSerie());

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


    public void exportarExcel(HttpServletResponse response, FilterListDto filter) throws IOException {

        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        List<VtVentaReembolsosEntity> reembolsos = vtVentasReembolsoRepository.getFindAll(filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        LocalDate fechaActual = LocalDate.now();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Rembolsos_" + fechaActual + ".xlsx" + "\"");

        if (!reembolsos.isEmpty()) {
            log.info("Reembolsos obtenidas satisfactoriamente.");

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Reembolsos");
                XSSFRow headerRow = sheet.createRow(0);

                String[] columnNames = {"Documento", "Serie", "Secuencial", "FechaEmisión", "NumeroAutorizacion",
                        "NumeroIdentificación",
                        "BaseCero", "NoObjeto", "Exenta",
                        "Base15%", "Iva15%",
                        "Base5%", "Iva5%",
                        "Base8%", "Iva8%"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < reembolsos.size(); i++) {
                    VtVentaReembolsosEntity reembolso = reembolsos.get(i);
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

                    for (VtVentaReembolsosValoresEntity valores : reembolso.getReembolsosValores()) {
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

                    row.createCell(1).setCellValue(reembolso.getSerieReemb());
                    row.createCell(2).setCellValue(reembolso.getSecuencialReemb());
                    row.createCell(3).setCellValue(reembolso.getFechaEmisionReemb());
                    row.createCell(4).setCellValue(reembolso.getNumeroAutorizacionReemb());
                    row.createCell(5).setCellValue(reembolso.getNumeroIdentificacionReemb());

                    row.createCell(6).setCellValue(baseCero.doubleValue());

                    row.createCell(7).setCellValue(baseNoObjeto.doubleValue());
                    row.createCell(8).setCellValue(baseExenta.doubleValue());

                    row.createCell(9).setCellValue(baseGrav5.doubleValue());
                    row.createCell(10).setCellValue(valorIva5.doubleValue());

                    row.createCell(11).setCellValue(baseGrav8.doubleValue());
                    row.createCell(12).setCellValue(valorIva8.doubleValue());

                    row.createCell(13).setCellValue(baseGrav15.doubleValue());
                    row.createCell(14).setCellValue(valorIva15.doubleValue());
                    row.createCell(15).setCellValue(valorIva15.doubleValue());


                }

                try (OutputStream os = response.getOutputStream()) {
                    workbook.write(os);
                }
            } catch (IOException e) {
                log.error("Error al crear el archivo Excel", e);
                throw e;
            }
        } else {
            log.warn("No se encontraron reembolsos con los filtros proporcionados.");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            OutputStream os = response.getOutputStream();
            os.write("No se encontraron reembolsos con los filtros proporcionados".getBytes());
            os.flush();
            os.close();
        }
    }


    public void exportarPDF(HttpServletResponse response, FilterListDto filter) throws DocumentException, IOException {

        List<VtVentaReembolsosEntity> reembolsos = vtVentasReembolsoRepository
                .getFindAll(filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta());


        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=facturas_" + LocalDateTime.now() + ".pdf";
        response.setHeader(headerKey, headerValue);

        if (!reembolsos.isEmpty()) {

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
            for (VtVentaReembolsosEntity reembolso : reembolsos) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (VtVentaReembolsosValoresEntity valores : reembolso.getReembolsosValores()) {
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

                table.addCell(reembolso.getSerieReemb());
                table.addCell(reembolso.getSecuencialReemb());
                table.addCell(DateUtils.toString(reembolso.getFechaEmisionReemb()));
                table.addCell(reembolso.getNumeroAutorizacionReemb());
                table.addCell(reembolso.getNumeroIdentificacionReemb());

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
