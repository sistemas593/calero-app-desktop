package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentasGuias.builder.VtGuiaBuilder;
import com.calero.lili.core.modVentasGuias.dto.CreationRequestGuiaRemisionDto;
import com.calero.lili.core.modVentasGuias.dto.FilterListDto;
import com.calero.lili.core.modVentasGuias.dto.GetDto;
import com.calero.lili.core.modVentasGuias.dto.GetListDto;
import com.calero.lili.core.modVentasGuias.projection.OneProjection;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoTercero;
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
public class VtGuiasServiceImpl {

    private final VtGuiasRepository vtVentaRepository;
    private final VtGuiaBuilder vtGuiaBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final ComprobanteServiceImpl comprobanteService;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final GeItemsRepository geItemsRepository;


    public ResponseDto create(Long idData, Long idEmpresa, CreationRequestGuiaRemisionDto request, String usuario) {

        DateUtils.validarFechaEmision(request.getFechaEmision());
        Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, request.getSerie(), request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La Guia de remisión " +
                    "ya existe: Serie: {0} Secuencia: {1}", request.getSerie(), request.getSecuencial()));
        }

        GeTerceroEntity transportista = geTercerosRepository.findByIdCliente(idData, request.getIdTransportista())
                .orElseThrow(() -> new GeneralException("No existe transportista"));

        validarTransportista(transportista);
        GeTerceroEntity destinatario = geTercerosRepository.findByIdCliente(idData, request.getIdDestinatario())
                .orElseThrow(() -> new GeneralException("No existe destinatario"));

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), "GRM")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe", request.getSerie(), request.getSecuencial())));

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validateCodSustento(request);
        validarAmbiente(request);
        VtGuiaEntity vtGuiaEntity = vtGuiaBuilder.builderEntity(request, idData, idEmpresa);
        vtGuiaEntity.setTransportista(transportista);
        vtGuiaEntity.setDestinatario(destinatario);
        vtGuiaEntity.setTipoEmision(getTipoEmision(request));
        vtGuiaEntity.setEmail(destinatario.getEmail());
        vtGuiaEntity.setCreatedBy(usuario);
        vtGuiaEntity.setCreatedDate(LocalDateTime.now());

        comprobanteService.getComprobanteXmlGuiaRemision(idData, idEmpresa, vtGuiaEntity);
        VtGuiaEntity saved = vtVentaRepository.save(vtGuiaEntity);

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return responseApiBuilder.builderResponse(saved.getIdGuia().toString());

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationRequestGuiaRemisionDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        DateUtils.validarFechaEmision(request.getFechaEmision());
        VtGuiaEntity vtGuiaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtGuiaEntity.getSerie().equals(request.getSerie()) || !vtGuiaEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La factura ya existe Serie: {0} Secuencia: {1}", request.getSerie(), request.getSecuencial()));
            }
        }

        GeTerceroEntity transportista = geTercerosRepository.findByIdCliente(idData, request.getIdTransportista())
                .orElseThrow(() -> new GeneralException("No existe transportista"));

        validarTransportista(transportista);

        GeTerceroEntity destinatario = geTercerosRepository.findByIdCliente(idData, request.getIdDestinatario())
                .orElseThrow(() -> new GeneralException("No existe destinatario"));

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validateCodSustento(request);
        validarAmbiente(request);
        validarEstadoDocumento(vtGuiaEntity);
        VtGuiaEntity update = vtGuiaBuilder.builderUpdateEntity(request, vtGuiaEntity);
        update.setTipoEmision(getTipoEmision(request));

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        update.setTransportista(transportista);
        update.setDestinatario(destinatario);
        update.setEmail(destinatario.getEmail());
        comprobanteService.getComprobanteXmlGuiaRemision(idData, idEmpresa, update);
        VtGuiaEntity actualizado = vtVentaRepository.save(update);
        return responseApiBuilder.builderResponse((actualizado.getIdGuia().toString()));

    }

    private void validarEstadoDocumento(VtGuiaEntity vtGuiaEntity) {
        if (vtGuiaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        VtGuiaEntity vtGuiaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        vtGuiaEntity.setDelete(Boolean.TRUE);
        vtGuiaEntity.setDeletedBy(usuario);
        vtGuiaEntity.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(vtGuiaEntity);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtGuiaEntity vtGuiaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return vtGuiaBuilder.builderResponse(vtGuiaEntity);
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idVenta,
                                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtGuiaEntity vtGuiaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        return vtGuiaEntity.getMensajes();
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<VtGuiaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        if (page.isEmpty()) {
            throw new GeneralException("No existen datos a mostrar");
        }

        List<GetListDto> dtoList = page.stream().map(vtGuiaBuilder::builderListResponse).toList();

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


    public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws IOException {

        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        List<VtGuiaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filter.getSucursal(), filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getSerie(), filter.getSecuencial());

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
                    VtGuiaEntity factura = facturas.get(i);
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


                    row.createCell(1).setCellValue(factura.getSerie());
                    row.createCell(2).setCellValue(factura.getSecuencial());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaIniTransporte()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacion());

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

        List<VtGuiaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial());

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
            for (VtGuiaEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);


                table.addCell(factura.getSerie());
                table.addCell(factura.getSecuencial());
                table.addCell(factura.getFechaIniTransporte().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                table.addCell(factura.getNumeroAutorizacion());

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

    private Integer getTipoEmision(CreationRequestGuiaRemisionDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }

    private void validarAmbiente(CreationRequestGuiaRemisionDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }

    private void validateCodSustento(CreationRequestGuiaRemisionDto request) {

        if (Objects.nonNull(request.getCodDocSustento())) {
            if (request.getCodDocSustento().isBlank()) {
                throw new GeneralException("El código de documento sustento no puede estar vacío");
            }
            if (Objects.isNull(request.getDocumentoSustento())) {
                throw new GeneralException("Es requerida la información del documento sustento");
            }
        }

        if (Objects.isNull(request.getCodDocSustento())) {

            if (Objects.nonNull(request.getDocumentoSustento())) {
                throw new GeneralException("No es requerida la información del documento sustento");
            }
        }

    }

    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idVenta,
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtGuiaEntity vtGuiaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtGuiaEntity.getAnulada()) {
            vtGuiaEntity.setAnulada(Boolean.TRUE);
            vtGuiaEntity.setFechaAnulacion(LocalDate.now());
            vtVentaRepository.save(vtGuiaEntity);
        }
        return responseApiBuilder.builderResponse(idVenta.toString());
    }


    private void validarTransportista(GeTerceroEntity transportista) {
        boolean existeTipoTransportista = transportista.getGeTercerosTipoEntities()
                .stream()
                .anyMatch(item -> item.getTipo().equals(TipoTercero.TRANSPORTISTA.getTipo()));

        if (!existeTipoTransportista) {
            throw new GeneralException("El transportista seleccionado no corresponde a un transportista");
        }
    }


    public void validarInfoAddicional(CreationRequestGuiaRemisionDto request) {
        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }

        for (CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    throw new GeneralException("En la lista de detalles, se envia detalle adicional pero este esta vacio");
                }
            }
        }
    }

    private void validarItem(CreationRequestGuiaRemisionDto request, Long idData, Long idEmpresa) {
        for (CreationRequestGuiaRemisionDto.DetailGuiaRemisionDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }

    private Page<VtGuiaEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa, FilterListDto filters,
                                                      Pageable pageable, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial(),
                        filters.getNumeroAutorizacion(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                            filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial(),
                            filters.getNumeroAutorizacion(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getSerie(), filters.getSecuencial(),
                        filters.getNumeroAutorizacion(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private VtGuiaEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return vtVentaRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return vtVentaRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return vtVentaRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

}
