package com.calero.lili.api.modComprasOrden;

import com.calero.lili.api.modAuditoria.TipoPermiso;
import com.calero.lili.api.modComprasOrden.builder.CpOrdenComprasBuilder;
import com.calero.lili.api.modComprasOrden.dto.FilterListDto;
import com.calero.lili.api.modComprasOrden.dto.GetDto;
import com.calero.lili.api.modComprasOrden.dto.GetListDto;
import com.calero.lili.api.modComprasOrden.dto.GetListDtoTotalizado;
import com.calero.lili.api.modComprasOrden.dto.OrdenCompraRequestDto;
import com.calero.lili.api.modComprasOrden.projection.TotalesProjection;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrdenComprasServiceImpl {

    private final OrdenComprasRepository ordenComprasRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final CpOrdenComprasBuilder cpOrdenComprasBuilder;
    private final GeTercerosRepository geTercerosRepository;

    public ResponseDto create(Long idData, Long idEmpresa, OrdenCompraRequestDto request, String usuario) {

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        CpOrdenComprasEntity cpOrdenCompras = cpOrdenComprasBuilder.builderEntity(request, idData, idEmpresa);
        cpOrdenCompras.setTercero(tercero);
        cpOrdenCompras.setCreatedBy(usuario);
        cpOrdenCompras.setCreatedDate(LocalDateTime.now());

        CpOrdenComprasEntity cpOrdenComprasEntity = ordenComprasRepository.save(cpOrdenCompras);
        return responseApiBuilder.builderResponse(cpOrdenComprasEntity.getIdCompra().toString());

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, OrdenCompraRequestDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));


        CpOrdenComprasEntity cpOrdenComprasEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        CpOrdenComprasEntity orden = cpOrdenComprasBuilder.builderUpdateEntity(request, cpOrdenComprasEntity);

        orden.setModifiedBy(usuario);
        orden.setModifiedDate(LocalDateTime.now());
        orden.setTercero(tercero);

        ordenComprasRepository.save(orden);
        return responseApiBuilder.builderResponse(cpOrdenComprasEntity.getIdCompra().toString());

    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario,
                       FilterListDto filters, TipoPermiso tipoBusqueda) {

        CpOrdenComprasEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        ordenComprasRepository.save(venta);


    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpOrdenComprasEntity cpOrdenComprasEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return cpOrdenComprasBuilder.builderGetDto(cpOrdenComprasEntity);
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<CpOrdenComprasEntity> page = ordenComprasRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSecuencial(), pageable);

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return cpOrdenComprasBuilder.builderAnuladoGetListDto(item);
            }
            return cpOrdenComprasBuilder.builderGetListDto(item);
        }).toList();

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

        Page<CpOrdenComprasEntity> page = ordenComprasRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSecuencial(), pageable);

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return cpOrdenComprasBuilder.builderAnuladoGetListDto(item);
            }
            return cpOrdenComprasBuilder.builderGetListDto(item);
        }).toList();

        List<TotalesProjection> totalValoresProjection = ordenComprasRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSecuencial());

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
        List<CpOrdenComprasEntity> facturas = ordenComprasRepository.findAll(idData, idEmpresa, filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getSecuencial(), filter.getSucursal());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Recibidos_" + fechaActual + ".xlsx" + "\"");

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
                    CpOrdenComprasEntity factura = facturas.get(i);
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

                    for (CpOrdenComprasValoresEntity valores : factura.getValoresEntity()) {
                        // IVA
                        // IVA
                        if (valores.getCodigo().equals("2")) {
                            // cero
                            if (valores.getCodigoPorcentaje().equals("0")) {

                                if (factura.getAnulada()) {
                                    baseCero = new BigDecimal("0.00");
                                } else {
                                    baseCero = valores.getBaseImponible();
                                }

                            }
                            // noobjeto
                            if (valores.getCodigoPorcentaje().equals("6")) {

                                if (factura.getAnulada()) {
                                    baseNoObjeto = new BigDecimal("0.00");
                                } else {
                                    baseNoObjeto = valores.getBaseImponible();
                                }
                            }
                            // exenta
                            if (valores.getCodigoPorcentaje().equals("7")) {

                                if (factura.getAnulada()) {
                                    baseExenta = new BigDecimal("0.00");
                                } else {
                                    baseExenta = valores.getBaseImponible();
                                }
                            }
                            //5 %
                            if (valores.getCodigoPorcentaje().equals("5")) {

                                if (factura.getAnulada()) {
                                    baseGrav5 = new BigDecimal("0.00");
                                    valorIva5 = new BigDecimal("0.00");
                                } else {
                                    baseGrav5 = valores.getBaseImponible();
                                    valorIva5 = valores.getValor();
                                }

                            }
                            // 8%
                            if (valores.getCodigoPorcentaje().equals("8")) {

                                if (factura.getAnulada()) {
                                    baseGrav8 = new BigDecimal("0.00");
                                    valorIva8 = new BigDecimal("0.00");
                                } else {
                                    baseGrav8 = valores.getBaseImponible();
                                    valorIva8 = valores.getValor();
                                }


                            }
                            // 15%
                            if (valores.getCodigoPorcentaje().equals("4")) {

                                if (factura.getAnulada()) {
                                    baseGrav15 = new BigDecimal("0.00");
                                    valorIva15 = new BigDecimal("0.00");
                                } else {
                                    baseGrav15 = valores.getBaseImponible();
                                    valorIva15 = valores.getValor();
                                }

                            }
                        }
                    }


                    row.createCell(1).setCellValue(factura.getSecuencial());
                    row.createCell(2).setCellValue(DateUtils.toString(factura.getFechaEmision()));


                    row.createCell(3).setCellValue(baseCero.doubleValue());

                    row.createCell(4).setCellValue(baseNoObjeto.doubleValue());
                    row.createCell(5).setCellValue(baseExenta.doubleValue());

                    row.createCell(6).setCellValue(baseGrav5.doubleValue());
                    row.createCell(7).setCellValue(valorIva5.doubleValue());

                    row.createCell(8).setCellValue(baseGrav8.doubleValue());
                    row.createCell(9).setCellValue(valorIva8.doubleValue());

                    row.createCell(10).setCellValue(baseGrav15.doubleValue());
                    row.createCell(11).setCellValue(valorIva15.doubleValue());

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

        List<CpOrdenComprasEntity> facturas = ordenComprasRepository.findAll(idData, idEmpresa, filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSecuencial(), filters.getSucursal());

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
            for (CpOrdenComprasEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (CpOrdenComprasValoresEntity valores : factura.getValoresEntity()) {
                    // IVA
                    // IVA
                    if (valores.getCodigo().equals("2")) {
                        // cero
                        if (valores.getCodigoPorcentaje().equals("0")) {

                            if (factura.getAnulada()) {
                                baseCero = new BigDecimal("0.00");
                            } else {
                                baseCero = valores.getBaseImponible();
                            }

                        }
                        // noobjeto
                        if (valores.getCodigoPorcentaje().equals("6")) {

                            if (factura.getAnulada()) {
                                baseNoObjeto = new BigDecimal("0.00");
                            } else {
                                baseNoObjeto = valores.getBaseImponible();
                            }
                        }
                        // exenta
                        if (valores.getCodigoPorcentaje().equals("7")) {

                            if (factura.getAnulada()) {
                                baseExenta = new BigDecimal("0.00");
                            } else {
                                baseExenta = valores.getBaseImponible();
                            }
                        }
                        //5 %
                        if (valores.getCodigoPorcentaje().equals("5")) {

                            if (factura.getAnulada()) {
                                baseGrav5 = new BigDecimal("0.00");
                                valorIva5 = new BigDecimal("0.00");
                            } else {
                                baseGrav5 = valores.getBaseImponible();
                                valorIva5 = valores.getValor();
                            }

                        }
                        // 8%
                        if (valores.getCodigoPorcentaje().equals("8")) {

                            if (factura.getAnulada()) {
                                baseGrav8 = new BigDecimal("0.00");
                                valorIva8 = new BigDecimal("0.00");
                            } else {
                                baseGrav8 = valores.getBaseImponible();
                                valorIva8 = valores.getValor();
                            }


                        }
                        // 15%
                        if (valores.getCodigoPorcentaje().equals("4")) {

                            if (factura.getAnulada()) {
                                baseGrav15 = new BigDecimal("0.00");
                                valorIva15 = new BigDecimal("0.00");
                            } else {
                                baseGrav15 = valores.getBaseImponible();
                                valorIva15 = valores.getValor();
                            }

                        }
                    }
                }


                table.addCell(factura.getSecuencial());
                table.addCell(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

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

            document.add(table);
            document.close();
        } else {
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

    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idOrdenCompra,
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpOrdenComprasEntity cpOrdenComprasEntity = validacionTipoBusqueda(idData, idEmpresa, idOrdenCompra, filters, tipoBusqueda, usuario);

        if (!cpOrdenComprasEntity.getAnulada()) {
            cpOrdenComprasEntity.setAnulada(Boolean.TRUE);
            cpOrdenComprasEntity.setFechaAnulacion(LocalDate.now());
            ordenComprasRepository.save(cpOrdenComprasEntity);
        }
        return responseApiBuilder.builderResponse(idOrdenCompra.toString());
    }


    private CpOrdenComprasEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                        FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return ordenComprasRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return ordenComprasRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return ordenComprasRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

}
