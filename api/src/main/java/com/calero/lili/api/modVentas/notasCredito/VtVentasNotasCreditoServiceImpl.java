package com.calero.lili.api.modVentas.notasCredito;

import com.calero.lili.api.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.api.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.api.modComprasItems.GeItemsRepository;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modVentas.GenerarAsientoServiceImpl;
import com.calero.lili.api.modVentas.VtVentaEntity;
import com.calero.lili.api.modVentas.VtVentaValoresEntity;
import com.calero.lili.api.modVentas.VtVentasRepository;
import com.calero.lili.api.modVentas.builder.GetListResponseBuilder;
import com.calero.lili.api.modVentas.dto.GetListDto;
import com.calero.lili.api.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.api.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.api.modVentas.notasCredito.builder.VtNotasCreditoBuilder;
import com.calero.lili.api.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.api.modVentas.notasCredito.dto.GetNotaCreditoDto;
import com.calero.lili.api.modVentas.projection.OneProjection;
import com.calero.lili.api.modVentas.projection.TotalesProjection;
import com.calero.lili.api.utils.validaciones.ValidarCampoAscii;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.NotFoundException;
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
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VtVentasNotasCreditoServiceImpl {

    private final VtVentasRepository vtVentaRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final VtNotasCreditoBuilder vtNotasCreditoBuilder;
    private final GetListResponseBuilder getListResponseBuilder;
    private final ComprobanteServiceImpl vtComprobanteService;
    private final GeItemsRepository geItemsRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final CnAsientosRepository cnAsientosRepository;
    private final GenerarAsientoServiceImpl generarAsientoService;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;


    public ResponseDto create(Long idData, Long idEmpresa, CreationNotaCreditoRequestDto request, String usuario) {

        ValidarCampoAscii.validarStrings(request);
        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getModFechaEmision()));

        Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe Tipo: {0} Serie: {1} Secuencia: {2}", TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial()));
        }

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtVentaEntity vtVentaEntity = vtNotasCreditoBuilder.builderEntity(request, idData, idEmpresa);
        vtVentaEntity.setTercero(tercero);
        vtVentaEntity.setEmail(tercero.getTercero());
        vtVentaEntity.setTerceroNombre(tercero.getTercero());
        vtVentaEntity.setCreatedBy(usuario);
        vtVentaEntity.setCreatedDate(LocalDateTime.now());

        vtVentaEntity.setTipoEmision(getTipoEmision(request));
        vtComprobanteService.getComprobanteXmlNotaCredito(idData, idEmpresa, vtVentaEntity);
        VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.NCR.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe", request.getSerie(), TipoVenta.NCR.name())));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        Boolean generarAsiento = Boolean.TRUE;
        if (generarAsiento) {
            CnAsientosEntity asiento = cnAsientosRepository.save(generarAsientoService.generarAsiento(idData, idEmpresa, saved));
            saved.setIdAsiento(asiento.getIdAsiento());
            vtVentaRepository.save(saved);
        }

        return responseApiBuilder.builderResponse(saved.getIdVenta().toString());

    }

    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationNotaCreditoRequestDto request, String usuario) {

        ValidarCampoAscii.validarStrings(request);
        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getModFechaEmision()));

        VtVentaEntity vtVentaEntity = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idVenta {0} no existe", idVenta)));

        if (!vtVentaEntity.getSerie().equals(request.getSerie()) || !vtVentaEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La nota de credito ya existe TipoIngreso: {0} Serie: {1} Secuencia: {2}", TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial()));
            }
        }

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);
        validarEstadoDocumento(vtVentaEntity);


        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe destinatario"));

        VtVentaEntity update = vtNotasCreditoBuilder.builderUpdateEntity(request, vtVentaEntity);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        update.setTercero(tercero);
        update.setEmail(tercero.getEmail());
        update.setTerceroNombre(tercero.getTercero());

        update.setTipoEmision(getTipoEmision(request));
        vtComprobanteService.getComprobanteXmlNotaCredito(idData, idEmpresa, update);
        VtVentaEntity response = vtVentaRepository.save(update);

        return responseApiBuilder.builderResponse(response.getIdVenta().toString());

    }

    private void validarEstadoDocumento(VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario) {

        VtVentaEntity venta = vtVentaRepository.findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La nota de credito con ID {0} no existe", idVenta)));

        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(venta);
    }

    public GetNotaCreditoDto findById(Long idData, Long idEmpresa, UUID idVenta) {

        VtVentaEntity vtVentaEntity = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La nota de credito con ID {0} no existe", idVenta)));

        return vtNotasCreditoBuilder.builderResponse(vtVentaEntity);
    }


    public List<Mensajes> findByMensajes(Long idData, Long idEmpresa, UUID idVenta) {

        VtVentaEntity vtVentaEntity = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La nota de credito con ID {0} no existe", idVenta)));

        return vtVentaEntity.getMensajes();
    }


    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {
        Page<VtVentaEntity> page = vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), pageable);

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoListResponse(item);
            }
            return getListResponseBuilder.builderListResponse(item);
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

        Page<VtVentaEntity> page = vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), pageable);

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoListResponse(item);
            }
            return getListResponseBuilder.builderListResponse(item);
        }).toList();

        List<TotalesProjection> totalValoresProjection = vtVentaRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial());

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
        List<VtVentaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filter.getSucursal(), filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getNumeroIdentificacion(), filter.getTipoVenta(), filter.getSerie(), filter.getSecuencial());

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
                    VtVentaEntity factura = facturas.get(i);
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

                    for (VtVentaValoresEntity valores : factura.getValoresEntity()) {
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

                    row.createCell(0).setCellValue(factura.getTipoVenta().toString());
                    row.createCell(1).setCellValue(factura.getSerie());
                    row.createCell(2).setCellValue(factura.getSecuencial());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmision()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacion());
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

        List<VtVentaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial());

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
            for (VtVentaEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (VtVentaValoresEntity valores : factura.getValoresEntity()) {
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

                table.addCell(factura.getTipoVenta().toString());
                table.addCell(factura.getSerie());
                table.addCell(factura.getSecuencial());
                table.addCell(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                table.addCell(factura.getNumeroAutorizacion());
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

    private void validarAmbiente(CreationNotaCreditoRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }

    private Integer getTipoEmision(CreationNotaCreditoRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }


    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idVenta) {

        VtVentaEntity vtVentaEntity = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("idVenta {0} no existe", idVenta)));

        if (!vtVentaEntity.getAnulada()) {
            vtVentaEntity.setAnulada(Boolean.TRUE);
            vtVentaEntity.setFechaAnulacion(LocalDate.now());
            vtVentaRepository.save(vtVentaEntity);
        }
        return responseApiBuilder.builderResponse(idVenta.toString());
    }


    public void validarInfoAddicional(CreationNotaCreditoRequestDto request) {
        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }

        for (CreationNotaCreditoRequestDto.DetailDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    throw new GeneralException("En la lista de detalles, se envia detalle adicional pero este esta vacio");
                }
            }
        }
    }


    private void validarItem(CreationNotaCreditoRequestDto request, Long idData, Long idEmpresa) {
        for (CreationNotaCreditoRequestDto.DetailDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }


    public ResponseDto createAsientoNotaCredito(Long idData, Long idEmpresa, UUID idVenta) {

        VtVentaEntity notaCredito = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La factura con ID {0} no exixte", idVenta)));

        Boolean generarAsiento = Boolean.TRUE;

        if (generarAsiento) {
            CnAsientosEntity asiento = cnAsientosRepository.save(generarAsientoService.generarAsiento(idData, idEmpresa, notaCredito));
            notaCredito.setIdAsiento(asiento.getIdAsiento());
            vtVentaRepository.save(notaCredito);
        }

        return responseApiBuilder.builderResponse(idVenta.toString());
    }

    private List<Integer> getIntegerTarifaIva(List<CreationNotaCreditoRequestDto.ValoresDto> valores) {
        return valores.stream()
                .map(CreationNotaCreditoRequestDto.ValoresDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }

}

