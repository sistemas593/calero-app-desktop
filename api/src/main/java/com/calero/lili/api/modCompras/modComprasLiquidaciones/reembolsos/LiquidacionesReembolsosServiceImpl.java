package com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos;

import com.calero.lili.api.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.FilterListDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.GetListDtoTotalizado;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.projection.TotalesProjection;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.builder.CpLiquidacionesReembolsosBuilder;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiquidacionesReembolsosServiceImpl {


    private final LiquidacionReembolsosRepository reembolsosRepository;
    private final CpLiquidacionesReembolsosBuilder cpLiquidacionesReembolsosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final AuditorAware<String> auditorAware;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;

    public ResponseDto create(ReembolsoRequestDto request) {

        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getReembolsosValores()),
                DateUtils.toLocalDate(request.getFechaEmisionReemb()));

        Optional<CpLiquidacionesReembolsosEntity> existReembolso = reembolsosRepository
                .findByIdentificacionAndDocumentoAndSecuencialAndAutorizacion(request.getNumeroIdentificacionReemb(),
                        request.getSerieReemb(), request.getSecuencialReemb(), request.getNumeroAutorizacionReemb());

        if (existReembolso.isPresent()) {
            throw new GeneralException(MessageFormat.format("El registro ya existe - numeroIdentificacion{0} Serie: {1} Secuencia: {2} numeroAutorizacion: {3}", request.getNumeroIdentificacionReemb(), request.getSerieReemb(), request.getSecuencialReemb(), request.getNumeroAutorizacionReemb()));
        }

        CpLiquidacionesReembolsosEntity entity = cpLiquidacionesReembolsosBuilder.builderReembolso(request);
        entity.setCreatedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entity.setCreatedDate(LocalDateTime.now());
        return responseApiBuilder.builderResponse(reembolsosRepository
                .save(entity)
                .getIdLiquidacionReembolsos().toString());

    }

    @Transactional
    public ResponseDto update(UUID idReembolso, ReembolsoRequestDto request) {

        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getReembolsosValores()),
                DateUtils.toLocalDate(request.getFechaEmisionReemb()));

        CpLiquidacionesReembolsosEntity reembolso = reembolsosRepository
                .findByIdLiquidacionReembolsos(idReembolso).orElseThrow(() -> new GeneralException(MessageFormat.format("idReembolso {0} no existe", idReembolso)));
        if (!reembolso.getSerieReemb().equals(request.getSerieReemb()) || !reembolso.getSecuencialReemb().equals(request.getSecuencialReemb())) {

            if (reembolsosRepository.findBySecuencialReembAndSerieReemb(request.getSecuencialReemb(), request.getSerieReemb()).isPresent()) {

                throw new GeneralException(MessageFormat.format("El reembolso ya existe Serie: {1} Secuencia: {2}",
                        request.getSerieReemb(), request.getSecuencialReemb()));
            }
        }

        CpLiquidacionesReembolsosEntity update = cpLiquidacionesReembolsosBuilder.builderUpdateReembolso(request, reembolso);
        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());
        reembolsosRepository.save(update);
        return responseApiBuilder.builderResponse(reembolso.getIdLiquidacionReembolsos().toString());

    }

    public void delete(UUID idReembolso) {

        CpLiquidacionesReembolsosEntity entidad = reembolsosRepository.findByIdLiquidacionReembolsos(idReembolso)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idReembolso {0} no existe", idReembolso)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        reembolsosRepository.save(entidad);

    }

    public GetReembolsoDto findById(UUID idReembolso) {
        CpLiquidacionesReembolsosEntity reembolso = reembolsosRepository
                .findByIdLiquidacionReembolsos(idReembolso).orElseThrow(() -> new GeneralException(MessageFormat.format("idReembolso {0} no existe", idReembolso)));
        return cpLiquidacionesReembolsosBuilder.builderResponse(reembolso);
    }


    public PaginatedDto<GetReembolsoDto> findAllPaginate(FilterListDto filtro, Pageable pageable) {

        Page<CpLiquidacionesReembolsosEntity> page = reembolsosRepository.findAllPageable(filtro.getFechaEmisionDesde(),
                filtro.getFechaEmisionHasta(), filtro.getSecuencial(), filtro.getNumeroIdentificacion(), filtro.getSerie(),
                filtro.getUtilizado(), pageable);

        List<GetReembolsoDto> dtoList = page.stream()
                .map(cpLiquidacionesReembolsosBuilder::builderResponse)
                .toList();

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


    public GetListDtoTotalizado<GetReembolsoDto> findAllPaginateTotalizado(FilterListDto filtro, Pageable pageable) {


        Page<CpLiquidacionesReembolsosEntity> page = reembolsosRepository
                .findAllPageable(filtro.getFechaEmisionDesde(), filtro.getFechaEmisionHasta(),
                        filtro.getSecuencial(), filtro.getNumeroIdentificacion(), filtro.getSerie(),
                        filtro.getUtilizado(), pageable);

        List<GetReembolsoDto> dtoList = page.stream().map(cpLiquidacionesReembolsosBuilder::builderResponse).toList();

        List<TotalesProjection> totalValoresProjection = reembolsosRepository
                .totalValores(filtro.getFechaEmisionDesde(), filtro.getFechaEmisionHasta(),
                        filtro.getSecuencial(), filtro.getNumeroIdentificacion(), filtro.getSerie());

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
        List<CpLiquidacionesReembolsosEntity> facturas = reembolsosRepository.getFindAll(filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        LocalDate fechaActual = LocalDate.now();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Rembolsos_" + fechaActual + ".xlsx" + "\"");

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
                    CpLiquidacionesReembolsosEntity factura = facturas.get(i);
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

                    for (CpLiquidacionesReembolsosValoresEntity valores : factura.getReembolsosValores()) {
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

                    row.createCell(1).setCellValue(factura.getSerieReemb());
                    row.createCell(2).setCellValue(factura.getSecuencialReemb());
                    row.createCell(3).setCellValue(factura.getFechaEmisionReemb());
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacionReemb());
                    row.createCell(5).setCellValue(factura.getNumeroIdentificacionReemb());

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
                    log.info("Hola");
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

    public void exportarPDF(HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {

        List<CpLiquidacionesReembolsosEntity> facturas = reembolsosRepository.getFindAll(filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta());

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
            for (CpLiquidacionesReembolsosEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (CpLiquidacionesReembolsosValoresEntity valores : factura.getReembolsosValores()) {
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

                table.addCell(factura.getSerieReemb());
                table.addCell(factura.getSecuencialReemb());
                table.addCell(DateUtils.toString(factura.getFechaEmisionReemb()));
                table.addCell(factura.getNumeroAutorizacionReemb());
                table.addCell(factura.getNumeroIdentificacionReemb());

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

    private List<Integer> getIntegerTarifaIva(List<ValoresDto> valores) {
        return valores.stream()
                .map(ValoresDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }


}
