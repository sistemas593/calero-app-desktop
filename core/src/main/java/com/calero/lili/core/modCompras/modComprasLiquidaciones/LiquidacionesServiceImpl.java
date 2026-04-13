package com.calero.lili.core.modCompras.modComprasLiquidaciones;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.services.BuscarDatosEmpresa;
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.NotFoundException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresas.projection.MomentoEnvioProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.builder.CpLiquidacionesBuilder;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.FilterListDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetListDtoTotalizado;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.OneProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.TotalesProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.LiquidacionReembolsosRepository;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiquidacionesServiceImpl {

    private final LiquidacionesRepository liquidacionesRepository;

    private final ResponseApiBuilder responseApiBuilder;
    private final CpLiquidacionesBuilder cpLiquidacionesBuilder;
    private final CpImpuestosServiceImpl cpImpuestosService;
    private final ComprobanteServiceImpl comprobanteService;
    private final LiquidacionReembolsosRepository liquidacionReembolsosRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final GeItemsRepository geItemsRepository;
    private final LiquidacionPersistenceService liquidacionPersistenceService;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final AdLogsBuilder adLogsBuilder;
    private final ProcesarDocumentosServiceImpl procesarDocumentosService;
    private final AdEmpresasRepository adEmpresasRepository;


    public RespuestaProcesoGetDto create(Long idData, Long idEmpresa, CreationRequestLiquidacionCompraDto request,
                                         String usuario, String origenCertificado) {

        DateUtils.validarFechaEmision(request.getFechaEmision());
        Optional<OneProjection> existingFactura = liquidacionesRepository
                .findExistBySecuencial(idData, idEmpresa, request.getSerie(), request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La liquidación ya existe  Serie: {0} Secuencia: {1}", request.getSerie(), request.getSecuencial()));
        }

        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("El tercero seleccionado no existe"));

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);


        CpLiquidacionesEntity cpLiquidacionesEntity = cpLiquidacionesBuilder.builderEntity(request, idData, idEmpresa);
        cpLiquidacionesEntity.setProveedor(proveedor);
        cpLiquidacionesEntity.setEmail(proveedor.getEmail());

        cpLiquidacionesEntity.setCreatedBy(usuario);
        cpLiquidacionesEntity.setCreatedDate(LocalDateTime.now());

        cpLiquidacionesEntity.setTipoEmision(getTipoEmision(request));
        List<CpLiquidacionesReembolsosEntity> reembolsos = validarReembolso(request);
        if (Objects.nonNull(reembolsos)) {
            cpLiquidacionesEntity.setReembolsosEntity(reembolsos);
        }
        comprobanteService.getComprobanteXmlLiquidacion(idData, idEmpresa, cpLiquidacionesEntity);
        CpLiquidacionesEntity saved = liquidacionPersistenceService.guardarLiquidacion(cpLiquidacionesEntity, request, idData, idEmpresa);


        MomentoEnvioProjection momentoEnvio = adEmpresasRepository.obtenerMomentosEnvio(idEmpresa)
                .orElseThrow(() -> new GeneralException("No se encontraron los momentos de envío para la empresa con id: " + idEmpresa));

        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (momentoEnvio.getMomentoEnvioLiquidacion() == 2) {

            DatosEmpresaDto datosEmpresaDto = null;

            switch (origenCertificado) {

                case "WEB" ->
                        datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(saved.getIdData(), saved.getIdEmpresa());

                case "LOC" ->
                        datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(saved.getIdData(), saved.getIdEmpresa());
            }

            respuestaProcesoGetDto = procesarDocumentosService.procesarLiquidacion(saved,
                    adLogsBuilder.builderLiquidacion(saved, Boolean.FALSE), datosEmpresaDto);
            respuestaProcesoGetDto.setIdDocumento(saved.getIdLiquidacion());
        }


        if (Objects.isNull(respuestaProcesoGetDto.getNumeroAutorizacion())) {
            respuestaProcesoGetDto.setIdDocumento(saved.getIdLiquidacion());
            respuestaProcesoGetDto.setNumeroAutorizacion("");
            respuestaProcesoGetDto.setEmailEstado(saved.getEmailEstado());
            respuestaProcesoGetDto.setEstadoDocumento(saved.getEstadoDocumento().getEstadoDocumento());
        }

        return respuestaProcesoGetDto;


    }

    private List<CpLiquidacionesReembolsosEntity> validarReembolso(CreationRequestLiquidacionCompraDto request) {

        if (Objects.nonNull(request.getCodDocReembolso())) {
            if (request.getCodDocReembolso().equals("41")) {
                if (Objects.nonNull(request.getListIdLiquidacionesReembolso()) && validacionCampoReembolso(request)) {
                    return validateLiquidacionesReembolso(request.getListIdLiquidacionesReembolso());
                } else {
                    throw new GeneralException("El codigo de reembolso es 41 pero no existe información de reembolso");
                }
            }
        }
        return null;
    }

    private Boolean validacionCampoReembolso(CreationRequestLiquidacionCompraDto request) {
        if (Objects.nonNull(request.getTotalComprobantesReembolso())
                && Objects.nonNull(request.getTotalBaseImponibleReembolso())
                && Objects.nonNull(request.getTotalImpuestoReembolso())) {

            return Boolean.TRUE;
        }
        throw new GeneralException("No existe información de los totales de reembolso.");
    }

    private List<CpLiquidacionesReembolsosEntity> validateLiquidacionesReembolso(List<UUID> listUUIDs) {
        List<CpLiquidacionesReembolsosEntity> list = liquidacionReembolsosRepository.getFindAllByIds(listUUIDs);
        if (list.isEmpty()) throw new GeneralException("Los identificadores de los reembolsos no existen");

        for (CpLiquidacionesReembolsosEntity entidad : list) {
            if (Objects.nonNull(entidad.getIdLiquidacion())) {
                throw new GeneralException("El reembolso con id: " + entidad.getIdLiquidacionReembolsos() +
                        " ya se encuentra registrado en otra liquidación");
            }
        }

        return list;
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationRequestLiquidacionCompraDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        DateUtils.validarFechaEmision(request.getFechaEmision());
        CpLiquidacionesEntity cpLiquidacionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!cpLiquidacionesEntity.getSerie().equals(request.getSerie()) || !cpLiquidacionesEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = liquidacionesRepository.findExistBySecuencial(idData, idEmpresa, request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La liquidación ya existe Serie: {0} Secuencia: {1}", request.getSerie(), request.getSecuencial()));
            }
        }

        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("El tercero seleccionado no existe"));

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);
        validarEstadoDocumento(cpLiquidacionesEntity);

        CpLiquidacionesEntity update = cpLiquidacionesBuilder.builderUpdateEntity(request, cpLiquidacionesEntity);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        update.setProveedor(proveedor);
        update.setEmail(proveedor.getEmail());

        update.setTipoEmision(getTipoEmision(request));
        actualizarReembolsos(update, request);


        comprobanteService.getComprobanteXmlLiquidacion(idData, idEmpresa, update);
        liquidacionesRepository.save(update);
        return responseApiBuilder.builderResponse(cpLiquidacionesEntity.getIdLiquidacion().toString());

    }

    private void validarEstadoDocumento(CpLiquidacionesEntity cpLiquidacionesEntity) {
        if (cpLiquidacionesEntity.getEstadoDocumento().equals(EstadoDocumento.AUT)) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario,
                       FilterListDto filters, TipoPermiso tipoBusqueda) {

        CpLiquidacionesEntity liquidacion = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        liquidacion.setDelete(Boolean.TRUE);
        liquidacion.setDeletedBy(usuario);
        liquidacion.setDeletedDate(LocalDateTime.now());

        liquidacionesRepository.save(liquidacion);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpLiquidacionesEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        GetDto response = cpLiquidacionesBuilder.builderGetDto(vtVentaEntity);
        response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent(idVenta, idEmpresa, idData));
        return response;
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idVenta,
                                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpLiquidacionesEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        return vtVentaEntity.getMensajes();
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<CpLiquidacionesEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        List<GetListDto> dtoList = page.stream().map(entidad -> {

            GetListDto response = null;

            if (entidad.getAnulada()) {
                response = cpLiquidacionesBuilder.builderAnuladaGetListDto(entidad);
            } else {
                response = cpLiquidacionesBuilder.builderGetListDto(entidad);
            }

            response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent
                    (response.getIdLiquidacion(), idEmpresa, idData));
            return response;
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


        Page<CpLiquidacionesEntity> page = liquidacionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);

        List<GetListDto> dtoList = page.stream().map(entidad -> {

            GetListDto response = null;

            if (entidad.getAnulada()) {
                response = cpLiquidacionesBuilder.builderAnuladaGetListDto(entidad);
            } else {
                response = cpLiquidacionesBuilder.builderGetListDto(entidad);
            }
            response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent
                    (response.getIdLiquidacion(), idEmpresa, idData));
            return response;
        }).toList();

        List<TotalesProjection> totalValoresProjection = liquidacionesRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial());

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

        List<CpLiquidacionesEntity> facturas = liquidacionesRepository.findAll(idData, idEmpresa, filter.getSucursal(),
                filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getNumeroIdentificacion(),
                filter.getSerie(), filter.getSecuencial());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "liquidaciones_" + currentDateTime + ".xlsx" + "\"");

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
                    CpLiquidacionesEntity factura = facturas.get(i);
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

                    for (CpLiquidacionesValoresEntity valores : factura.getValoresEntity()) {
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


        List<CpLiquidacionesEntity> facturas = liquidacionesRepository.findAll(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(),
                filters.getSerie(), filters.getSecuencial());

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
            for (CpLiquidacionesEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (CpLiquidacionesValoresEntity valores : factura.getValoresEntity()) {
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


    private void actualizarReembolsos(CpLiquidacionesEntity liquidacion, CreationRequestLiquidacionCompraDto request) {


        if (Objects.nonNull(request.getCodDocReembolso())) {
            if (!request.getCodDocReembolso().equals("41")) {
                throw new GeneralException("No se puede realizar la operación el codigo no corresponde con reembolsos");
            }
        }


        // Si la lista viene nula o vacía -> desligar todos
        if (request.getListIdLiquidacionesReembolso() == null || request.getListIdLiquidacionesReembolso().isEmpty()) {
            liquidacion.getReembolsosEntity().clear();
            return;
        }

        // IDs actuales en la entidad
        List<UUID> idsActuales = liquidacion.getReembolsosEntity()
                .stream()
                .map(CpLiquidacionesReembolsosEntity::getIdLiquidacionReembolsos)
                .toList();

        // Quitar los que ya no están
        liquidacion.getReembolsosEntity().removeIf(
                reembolso -> !request.getListIdLiquidacionesReembolso().contains(reembolso.getIdLiquidacionReembolsos())
        );

        // Agregar nuevos
        for (UUID idNuevo : request.getListIdLiquidacionesReembolso()) {
            if (!idsActuales.contains(idNuevo)) {
                CpLiquidacionesReembolsosEntity nuevo = liquidacionReembolsosRepository.findByIdLiquidacionReembolsos(idNuevo)
                        .orElseThrow(() -> new GeneralException("Reembolso no encontrado: " + idNuevo));

                if (Objects.nonNull(nuevo.getIdLiquidacion())) {
                    throw new GeneralException("El reembolso con id: " + nuevo.getIdLiquidacionReembolsos() +
                            " ya se encuentra registrado en otra liquidación");
                }

                liquidacion.getReembolsosEntity().add(nuevo);
            }
        }
    }

    private Integer getTipoEmision(CreationRequestLiquidacionCompraDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }

    private void validarAmbiente(CreationRequestLiquidacionCompraDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }

    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idLiquidacion,
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpLiquidacionesEntity cpLiquidacionesEntity = validacionTipoBusqueda(idData, idEmpresa, idLiquidacion, filters, tipoBusqueda, usuario);

        if (!cpLiquidacionesEntity.getAnulada()) {
            cpLiquidacionesEntity.setAnulada(Boolean.TRUE);
            cpLiquidacionesEntity.setFechaAnulacion(LocalDate.now());
            liquidacionesRepository.save(cpLiquidacionesEntity);
        }
        return responseApiBuilder.builderResponse(idLiquidacion.toString());
    }

    public void validarInfoAddicional(CreationRequestLiquidacionCompraDto request) {
        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }
        for (CreationRequestLiquidacionCompraDto.DetailDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    throw new GeneralException("En la lista de detalles, se envia detalle adicional pero este esta vacio");
                }
            }
        }

    }


    private void validarItem(CreationRequestLiquidacionCompraDto request, Long idData, Long idEmpresa) {
        for (CreationRequestLiquidacionCompraDto.DetailDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }

    private Page<CpLiquidacionesEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa,
                                                                FilterListDto filters, Pageable pageable,
                                                                TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return liquidacionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return liquidacionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            filters.getNumeroIdentificacion(), filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return liquidacionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private CpLiquidacionesEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                         FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return liquidacionesRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return liquidacionesRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return liquidacionesRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

}
