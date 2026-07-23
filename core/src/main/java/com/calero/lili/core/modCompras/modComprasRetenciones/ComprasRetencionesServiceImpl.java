package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.services.BuscarDatosEmpresa;
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl;
import com.calero.lili.core.dtos.CompraImpuestosDto;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.OrigenImpuestos;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesRepository;
import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.ImpuestoCodigoBuilder;
import com.calero.lili.core.modCompras.modComprasRetenciones.builder.CpRetencionesBuilder;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.FilterListCompraRetencionesDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetListDtoTotalizado;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.DeEmitidasRetencionesProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.projection.TotalesProjection;
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
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprasRetencionesServiceImpl {

    private final ComprasRetencionesRepository comprasRetencionesRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final CpRetencionesBuilder cpRetencionesBuilder;
    private final CpImpuestosServiceImpl cpImpuestosService;
    private final ComprobanteServiceImpl comprobanteService;
    private final GeTercerosRepository geTercerosRepository;
    private final CpRetencionPersistenceService cpRetencionPersistenceService;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final AdLogsBuilder adLogsBuilder;
    private final ProcesarDocumentosServiceImpl procesarDocumentosService;
    private final AdEmpresasRepository adEmpresasRepository;
    private final AdEmpresasSeriesRepository adEmpresasSeriesRepository;
    private final CpImpuestosRepository cpImpuestosRepository;
    private final ImpuestoCodigoBuilder impuestoCodigoBuilder;


    public RespuestaProcesoGetDto create(Long idData, Long idEmpresa, CreationRetencionRequestDto request,
                                         String usuario, String origenCertificado) {


        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));
        AdEmpresasSeriesEntity serie = adEmpresasSeriesRepository
                .findBySerie(idData, idEmpresa, request.getSerieRetencion())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0}, serie {1} no existe", idEmpresa, request.getSerieRetencion())));

        Optional<DeEmitidasRetencionesProjection> existingRetencion = comprasRetencionesRepository
                .findExistBySecuencial(idData, idEmpresa, request.getSerieRetencion(), request.getSecuencialRetencion());


        if (existingRetencion.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe : " +
                    "Serie: {0} Secuencia: {1}", request.getSerieRetencion(), request.getSecuencialRetencion()));
        }

        DateUtils.validarFechaEmision(request.getFechaEmisionRetencion());
        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("El tercero seleccionado no existe"));

        validarInfoAddicional(request);
        validarAmbiente(request);
        CpRetencionesEntity retencionesEntity = cpRetencionesBuilder.builderEntity(request, idData, idEmpresa);

        setearCabeceraTotal(retencionesEntity, request);
        retencionesEntity.setTipoEmision(getTipoEmision(request));
        retencionesEntity.setProveedor(proveedor);
        retencionesEntity.setEmail(proveedor.getEmail());

        retencionesEntity.setCreatedBy(usuario);
        retencionesEntity.setCreatedDate(LocalDateTime.now());


        Map<UUID, CpImpuestosEntity> mapImpuestos = new HashMap<>();
        if (Objects.nonNull(request.getCompraImpuestos())) {

            List<UUID> impuestoIds = request.getCompraImpuestos().stream()
                    .map(com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto::getCompraImpuestoId)
                    .toList();

            List<CpImpuestosEntity> lista = cpImpuestosRepository.findByInId(idData, idEmpresa, impuestoIds);

            Set<UUID> idsEncontrados = lista.stream()
                    .map(CpImpuestosEntity::getIdImpuestos)
                    .collect(Collectors.toSet());

            List<UUID> idsFaltantes = impuestoIds.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();

            if (!idsFaltantes.isEmpty()) {
                throw new GeneralException("Los siguientes id de impuestos no existen o ya tienen una retención asociada: " + idsFaltantes);
            }

            lista.forEach(impuesto ->
                    mapImpuestos.put(impuesto.getIdImpuestos(), impuesto)
            );
        }

        CpRetencionesEntity saved = cpRetencionPersistenceService.guardarRetencion(retencionesEntity, empresa, serie,
                idData, idEmpresa, request, mapImpuestos);


        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (empresa.getMomentoEnvioFactura() == 2) {

            DatosEmpresaDto datosEmpresaDto = null;

            switch (origenCertificado) {

                case "WEB" -> {

                    datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(saved.getIdData(), saved.getIdEmpresa());
                    datosEmpresaDto.setOrigenDatos(origenCertificado);
                }

                case "LOC" -> {
                    datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(saved.getIdData(), saved.getIdEmpresa());
                    datosEmpresaDto.setOrigenDatos(origenCertificado);
                }
            }

            respuestaProcesoGetDto = procesarDocumentosService.procesarComprobanteRetencion(saved,
                    adLogsBuilder.builderComprobanteRetencion(saved, Boolean.FALSE), datosEmpresaDto);
            respuestaProcesoGetDto.setIdDocumento(saved.getIdRetencion());
        }

        if (Objects.isNull(respuestaProcesoGetDto.getNumeroAutorizacion())) {
            respuestaProcesoGetDto.setIdDocumento(saved.getIdRetencion());
            respuestaProcesoGetDto.setNumeroAutorizacion("");
            respuestaProcesoGetDto.setEmailEstado(saved.getEmailEstado());
            respuestaProcesoGetDto.setEstadoDocumento(saved.getEstadoDocumento().getEstadoDocumento());
        }

        return respuestaProcesoGetDto;


    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationRetencionRequestDto request,
                              String usuario, FilterListCompraRetencionesDto filters, TipoPermiso tipoBusqueda) {

        validarNumeroAutorizacion(request);

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));
        AdEmpresasSeriesEntity serie = adEmpresasSeriesRepository
                .findBySerie(idData, idEmpresa, request.getSerieRetencion())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0}, serie {1} no existe", idEmpresa, request.getSerieRetencion())));


        CpRetencionesEntity retencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        List<CpImpuestosEntity> listaImpuestos = cpImpuestosService.getListCompraImpuestoForIdRetencion(idVenta, idEmpresa, idData);

        validarAutorizacion(retencionesEntity);

        DateUtils.validarFechaEmision(request.getFechaEmisionRetencion());

        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("El tercero seleccionado no existe"));


        validarInfoAddicional(request);
        validarAmbiente(request);
        validarEstadoDocumento(retencionesEntity);

        CpRetencionesEntity update = cpRetencionesBuilder.builderUpdateEntity(request, retencionesEntity);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        update.setTipoEmision(getTipoEmision(request));
        update.setProveedor(proveedor);
        update.setEmail(proveedor.getEmail());


        validarCompraImpuestos(request);

        Map<UUID, com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto> mapRequestImpuestos = request.getCompraImpuestos().stream()
                .collect(Collectors.toMap(com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto::getCompraImpuestoId, dto -> dto));

        Set<UUID> idsExistentes = listaImpuestos.stream()
                .map(CpImpuestosEntity::getIdImpuestos)
                .collect(Collectors.toSet());

        // Impuestos que estaban ligados a la retencion pero ya no vienen referenciados en el request: se desligan
        listaImpuestos.stream()
                .filter(impuesto -> !mapRequestImpuestos.containsKey(impuesto.getIdImpuestos()))
                .forEach(impuesto -> {
                    impuesto.setRetencion(null);
                    impuesto.setOrigen(OrigenImpuestos.ISC.name());
                    impuesto.getCodigosEntity().clear();
                    cpImpuestosRepository.save(impuesto);
                });

        // Ids que vienen en el request y no estaban ligados previamente: se validan y se ligaran como nuevos
        List<UUID> idsNuevos = mapRequestImpuestos.keySet().stream()
                .filter(id -> !idsExistentes.contains(id))
                .toList();

        Map<UUID, CpImpuestosEntity> mapImpuestos = new HashMap<>();

        if (!idsNuevos.isEmpty()) {
            List<CpImpuestosEntity> lista = cpImpuestosRepository.findByInId(idData, idEmpresa, idsNuevos);

            Set<UUID> idsEncontrados = lista.stream()
                    .map(CpImpuestosEntity::getIdImpuestos)
                    .collect(Collectors.toSet());

            List<UUID> idsFaltantes = idsNuevos.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();

            if (!idsFaltantes.isEmpty()) {
                throw new GeneralException("Los siguientes impuestos no existen: " + idsFaltantes);
            }

            lista.forEach(impuesto ->
                    mapImpuestos.put(impuesto.getIdImpuestos(), impuesto)
            );
        }

        // Ids que ya estaban ligados y se mantienen en el request: se incluyen para que se les refresquen sus codigos
        listaImpuestos.stream()
                .filter(impuesto -> mapRequestImpuestos.containsKey(impuesto.getIdImpuestos()))
                .forEach(impuesto -> mapImpuestos.put(impuesto.getIdImpuestos(), impuesto));

        CpRetencionesEntity saved = cpRetencionPersistenceService.actualizarRetencion(update, empresa, serie, idData, idEmpresa, request, mapImpuestos);
        return responseApiBuilder.builderResponse(saved.getIdRetencion().toString());

    }

    private void validarCompraImpuestos(CreationRetencionRequestDto request) {
        if (Objects.isNull(request.getCompraImpuestos()) || request.getCompraImpuestos().isEmpty()) {
            throw new GeneralException("Es requerido enviar al menos un impuesto con sus códigos de retención");
        }

        request.getCompraImpuestos().forEach(item -> {
            if (Objects.isNull(item.getImpuestoCodigos()) || item.getImpuestoCodigos().isEmpty()) {
                throw new GeneralException(MessageFormat.format(
                        "Es requerido enviar los códigos de retención para el impuesto {0}", item.getCompraImpuestoId()));
            }
        });
    }

    private void validarEstadoDocumento(CpRetencionesEntity retencionesEntity) {
        if (retencionesEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListCompraRetencionesDto
                               filters,
                       TipoPermiso tipoBusqueda) {


        CpRetencionesEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        comprasRetencionesRepository.save(venta);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListCompraRetencionesDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpRetencionesEntity cpRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa,
                idVenta, filters, tipoBusqueda, usuario);

        GetDto response = cpRetencionesBuilder.builderResponse(cpRetencionesEntity);
        List<CpImpuestosEntity> listaImpuestos = cpImpuestosService.getListCompraImpuestoForIdRetencion(idVenta, idEmpresa, idData);
        response.setCompraImpuestos(cpImpuestosService.builderResponseListCompraImpuesto(listaImpuestos));
        return response;
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idRetencion,
                                           FilterListCompraRetencionesDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpRetencionesEntity cpRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idRetencion, filters, tipoBusqueda, usuario);
        return cpRetencionesEntity.getMensajes();
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListCompraRetencionesDto
                                                            filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<CpRetencionesEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);
        List<GetListDto> dtoList = page.stream().map(entidad -> {
                    GetListDto response = cpRetencionesBuilder.builderListResponse(entidad);
                    response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent(
                            response.getIdRetencion(), idEmpresa, idData));
                    return response;
                }
        ).toList();

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

    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long
            idEmpresa, FilterListCompraRetencionesDto filters, Pageable pageable) {

        Page<CpRetencionesEntity> page = comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), null,
                filters.getFechaPeriodoFiscalDesde(), filters.getFechaPeriodoFiscalHasta(), pageable);

        List<GetListDto> dtoList = page.stream().map(entidad -> {
                    GetListDto response = cpRetencionesBuilder.builderListResponse(entidad);
                    response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent(
                            response.getIdRetencion(), idEmpresa, idData));
                    return response;
                }
        ).toList();
        List<TotalesProjection> totalValoresProjection = comprasRetencionesRepository.totalValores(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial(),
                filters.getFechaPeriodoFiscalDesde(), filters.getFechaPeriodoFiscalHasta());

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
        totalesDto.setPaginator(paginated);

        GetListDtoTotalizado.Totales tot = new GetListDtoTotalizado.Totales();
        tot.setValoresTotales(totalValoresProjection);

        totalesDto.setTotales(tot);
        return totalesDto;

    }

    public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse
            response, FilterListCompraRetencionesDto filter) throws
            IOException {


        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        List<CpRetencionesEntity> facturas = comprasRetencionesRepository.findAll(idData, idEmpresa, filter.getSucursal(),
                filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getSerie(), filter.getSecuencial());

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
                        "Tercero", "NumeroIdentificación",
                        "BaseCero", "NoObjeto", "Exenta",
                        "Base15%", "Iva15%",
                        "Base5%", "Iva5%",
                        "Base8%", "Iva8%"};

                IntStream.range(0, columnNames.length)
                        .forEach(i -> headerRow.createCell(i).setCellValue(columnNames[i]));

                for (int i = 0; i < facturas.size(); i++) {
                    CpRetencionesEntity factura = facturas.get(i);
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

                    row.createCell(0).setCellValue("RET");
                    row.createCell(1).setCellValue(factura.getSerieRetencion());
                    row.createCell(2).setCellValue(factura.getSecuencialRetencion());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmisionRetencion()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacionRetencion());
                    row.createCell(5).setCellValue(factura.getProveedor().getTercero());
                    row.createCell(6).setCellValue(factura.getProveedor().getNumeroIdentificacion());

                    row.createCell(7).setCellValue(baseCero.doubleValue());
                    row.createCell(8).setCellValue(baseNoObjeto.doubleValue());
                    row.createCell(9).setCellValue(baseExenta.doubleValue());

                    row.createCell(10).setCellValue(baseGrav15.doubleValue());
                    row.createCell(11).setCellValue(valorIva15.doubleValue());

                    row.createCell(12).setCellValue(baseGrav5.doubleValue());
                    row.createCell(13).setCellValue(valorIva5.doubleValue());

                    row.createCell(14).setCellValue(baseGrav8.doubleValue());
                    row.createCell(15).setCellValue(valorIva8.doubleValue());

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


    public void exportarPDF(Long idData, Long idEmpresa, HttpServletResponse
            response, FilterListCompraRetencionesDto filters) throws
            DocumentException, IOException {

        List<CpRetencionesEntity> facturas = comprasRetencionesRepository.findAll(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial());

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
            for (CpRetencionesEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                table.addCell(factura.getSerieRetencion());
                table.addCell(factura.getSecuencialRetencion());
                table.addCell(factura.getFechaEmisionRetencion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                table.addCell(factura.getNumeroAutorizacionRetencion());
                table.addCell(factura.getProveedor().getNumeroIdentificacion());

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


    private Integer getTipoEmision(CreationRetencionRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }

    private void validarAmbiente(CreationRetencionRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }


    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idRetencion,
                                     FilterListCompraRetencionesDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpRetencionesEntity cpRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idRetencion, filters, tipoBusqueda, usuario);
        if (!cpRetencionesEntity.getAnulada()) {
            cpRetencionesEntity.setAnulada(Boolean.TRUE);
            cpRetencionesEntity.setFechaAnulacion(LocalDate.now());
            comprasRetencionesRepository.save(cpRetencionesEntity);
        }
        return responseApiBuilder.builderResponse(idRetencion.toString());
    }


    public void validarInfoAddicional(CreationRetencionRequestDto request) {
        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                request.setInformacionAdicional(null);
            }
        }
    }


    private Page<CpRetencionesEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa,
                                                              FilterListCompraRetencionesDto filters, Pageable pageable,
                                                              TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null,
                        filters.getFechaPeriodoFiscalDesde(), filters.getFechaPeriodoFiscalHasta(), pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null,
                            filters.getFechaPeriodoFiscalDesde(), filters.getFechaPeriodoFiscalHasta(), pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario,
                        filters.getFechaPeriodoFiscalDesde(), filters.getFechaPeriodoFiscalHasta(), pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private CpRetencionesEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                       FilterListCompraRetencionesDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return comprasRetencionesRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return comprasRetencionesRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return comprasRetencionesRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    public void validarAutorizacion(CpRetencionesEntity retencion) {


        if (retencion.getEstadoDocumento().equals(EstadoDocumento.AUT)) {
            throw new GeneralException("El documento no puede modificarse por que ya esta autorizado");
        }

    }

    private void validarNumeroAutorizacion(CreationRetencionRequestDto request) {


        if (request.getFormatoDocumento().equals(FormatoDocumento.F)) {
            if (Objects.isNull(request.getNumeroAutorizacionRetencion()) || request.getNumeroAutorizacionRetencion().isEmpty()) {
                throw new GeneralException("Un documento físico debe tener número de autorización");
            }

            if (request.getNumeroAutorizacionRetencion().length() == 49 || request.getNumeroAutorizacionRetencion().length() == 10) {

                if (!request.getNumeroAutorizacionRetencion().matches("\\d+")) {
                    throw new GeneralException("El número de autorización no puede contener caracteres que no sean númericos");
                }

            } else {
                throw new GeneralException("El número de autorización no cumple con la cantidad de dígitos");
            }
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            if (Objects.nonNull(request.getNumeroAutorizacionRetencion())) {
                if (request.getNumeroAutorizacionRetencion().isEmpty()) {
                    request.setNumeroAutorizacionRetencion(null);
                }
            }
        }

    }

    private List<CompraImpuestosDto> builderListSave(CreationRetencionRequestDto request) {
        List<com.calero.lili.core.dtos.CompraImpuestosDto> listImpuesto = new ArrayList<>();

        request.getCompraImpuestos().forEach(item -> {
            listImpuesto.add(com.calero.lili.core.dtos.CompraImpuestosDto.builder()
                    .idCompraImpuesto(item.getCompraImpuestoId())
                    .listCodigosImpuesto(Objects.nonNull(item.getImpuestoCodigos())
                            ? item.getImpuestoCodigos()
                            : null)
                    .origen(OrigenImpuestos.RCC.name())
                    .build());
        });
        return listImpuesto;
    }


    private void validateCodigosEntity(CpImpuestosEntity impuesto, List<CpImpuestosCodigosEntity> listCodigos) {
        if (Objects.isNull(impuesto.getCodigosEntity())) {
            impuesto.setCodigosEntity(new ArrayList<>());
        }

        if (Objects.nonNull(listCodigos)) {
            impuesto.getCodigosEntity().clear();
            impuesto.getCodigosEntity().addAll(listCodigos);
        } else {
            impuesto.getCodigosEntity().clear();
        }


    }

    private Boolean permiteRetencion(String origen) {
        if (OrigenImpuestos.XDF.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.ISC.name().equals(origen)) return Boolean.TRUE;
        if (OrigenImpuestos.DSC.name().equals(origen)) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void setearCabeceraTotal(CpRetencionesEntity retencionesEntity, CreationRetencionRequestDto request) {

        BigDecimal totalValor = request.getCompraImpuestos().stream()
                .flatMap(compraImpuesto -> compraImpuesto.getImpuestoCodigos().stream())
                .map(ImpuestoCodigoDto::getValorRetenido)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        retencionesEntity.setTotal(totalValor);
    }

}
