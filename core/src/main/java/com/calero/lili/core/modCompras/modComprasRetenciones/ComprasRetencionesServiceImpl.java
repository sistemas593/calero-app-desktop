package com.calero.lili.core.modCompras.modComprasRetenciones;

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
import com.calero.lili.core.modCompras.modComprasRetenciones.builder.CpRetencionesBuilder;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.FilterListDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.GetListDtoTotalizado;
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


    public RespuestaProcesoGetDto create(Long idData, Long idEmpresa, CreationRetencionRequestDto request,
                                         String usuario, String origenCertificado) {


        DateUtils.validarFechaEmision(request.getFechaEmisionRetencion());
        GeTerceroEntity proveedor = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("El tercero seleccionado no existe"));

        validarInfoAddicional(request);
        validarAmbiente(request);
        CpRetencionesEntity retencionesEntity = cpRetencionesBuilder.builderEntity(request, idData, idEmpresa);

        retencionesEntity.setTipoEmision(getTipoEmision(request));
        retencionesEntity.setProveedor(proveedor);
        retencionesEntity.setEmail(proveedor.getEmail());

        retencionesEntity.setCreatedBy(usuario);
        retencionesEntity.setCreatedDate(LocalDateTime.now());

        comprobanteService.getComprobanteXmlRetencion(idData, idEmpresa, retencionesEntity, request);
        CpRetencionesEntity saved = cpRetencionPersistenceService.guardarRetencion(retencionesEntity, request);

        MomentoEnvioProjection momentoEnvio = adEmpresasRepository.obtenerMomentosEnvio(idEmpresa)
                .orElseThrow(() -> new GeneralException("No se encontraron los momentos de envío para la empresa con id: " + idEmpresa));

        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (momentoEnvio.getMomentoEnvioFactura() == 2) {

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
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {


        DateUtils.validarFechaEmision(request.getFechaEmisionRetencion());
        CpRetencionesEntity retencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

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

        comprobanteService.getComprobanteXmlRetencion(idData, idEmpresa, update, request);
        comprasRetencionesRepository.save(update);
        return responseApiBuilder.builderResponse(retencionesEntity.getIdRetencion().toString());

    }

    private void validarEstadoDocumento(CpRetencionesEntity retencionesEntity) {
        if (retencionesEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListDto filters,
                       TipoPermiso tipoBusqueda) {


        CpRetencionesEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        comprasRetencionesRepository.save(venta);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpRetencionesEntity cpRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa,
                idVenta, filters, tipoBusqueda, usuario);
        GetDto response = cpRetencionesBuilder.builderResponse(cpRetencionesEntity);
        response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent(idVenta, idEmpresa, idData));
        return response;
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idRetencion,
                                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CpRetencionesEntity cpRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idRetencion, filters, tipoBusqueda, usuario);
        return cpRetencionesEntity.getMensajes();
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
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

    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<CpRetencionesEntity> page = comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);

        List<GetListDto> dtoList = page.stream().map(entidad -> {
                    GetListDto response = cpRetencionesBuilder.builderListResponse(entidad);
                    response.setListCompraImpuesto(cpImpuestosService.getListCompraImpuestoForIdParent(
                            response.getIdRetencion(), idEmpresa, idData));
                    return response;
                }
        ).toList();
        List<TotalesProjection> totalValoresProjection = comprasRetencionesRepository.totalValores(idData, idEmpresa, filters.getSucursal(),
                filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial());

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

    public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws
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
                        "NumeroIdentificación",
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

                    row.createCell(1).setCellValue(factura.getSerieRetencion());
                    row.createCell(2).setCellValue(factura.getSecuencialRetencion());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmisionRetencion()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacionRetencion());
                    row.createCell(5).setCellValue(factura.getProveedor().getNumeroIdentificacion());

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


    public void exportarPDF(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filters) throws
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
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

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
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }
    }


    private Page<CpRetencionesEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa,
                                                              FilterListDto filters, Pageable pageable,
                                                              TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return comprasRetencionesRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private CpRetencionesEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                       FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

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

}
