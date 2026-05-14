package com.calero.lili.core.modVentasRetenciones;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentasRetenciones.builder.VtRetencionesBuilder;
import com.calero.lili.core.modVentasRetenciones.dto.CreationVentasRetencionesRequestDto;
import com.calero.lili.core.modVentasRetenciones.dto.FilterListDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetListDto;
import com.calero.lili.core.modVentasRetenciones.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentasRetenciones.projection.TotalesProjection;
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
public class VentasRetencionesServiceImpl {

    private final VentasRetencionesRepository vtVentaRepository;
    private final VtRetencionesBuilder vtRetencionesBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final GeTercerosRepository geTercerosRepository;

    public ResponseDto create(Long idData, Long idEmpresa, CreationVentasRetencionesRequestDto request, String usuario) {


        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtRetencionesEntity retenciones = vtRetencionesBuilder
                .builderEntity(request, idData, idEmpresa);
        retenciones.setCliente(tercero);
        retenciones.setCreatedBy(usuario);
        retenciones.setCreatedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(vtVentaRepository.save(retenciones).getIdRetencion().toString());

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationVentasRetencionesRequestDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        VtRetencionesEntity vtRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtRetencionesEntity retenciones = vtRetencionesBuilder
                .builderUpdateEntity(request, vtRetencionesEntity);

        retenciones.setCliente(tercero);
        retenciones.setModifiedBy(usuario);
        retenciones.setModifiedDate(LocalDateTime.now());

        VtRetencionesEntity actualizado = vtVentaRepository.save(retenciones);
        return responseApiBuilder.builderResponse(actualizado.getIdRetencion().toString());

    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListDto filters,
                       TipoPermiso tipoBusqueda) {


        VtRetencionesEntity vtRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        vtRetencionesEntity.setDelete(Boolean.TRUE);
        vtRetencionesEntity.setDeletedBy(usuario);
        vtRetencionesEntity.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(vtRetencionesEntity);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtRetencionesEntity vtRetencionesEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        return vtRetencionesBuilder.builderResponse(vtRetencionesEntity);
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<VtRetencionesEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        List<GetListDto> dtoList = page.stream().map(vtRetencionesBuilder::builderListResponse).toList();

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

    private Page<VtRetencionesEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa, FilterListDto filters,
                                                              Pageable pageable, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                            filters.getNumeroIdentificacion(), filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(),
                        filters.getNumeroIdentificacion(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable) {

        Page<VtRetencionesEntity> page = vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);

        List<GetListDto> dtoList = page.stream().map(vtRetencionesBuilder::builderListResponse).toList();

        List<TotalesProjection> totalValoresProjection = vtVentaRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial());

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
        List<VtRetencionesEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filter.getSucursal(), filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getNumeroIdentificacion(), filter.getSerie(), filter.getSecuencial());

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
                    VtRetencionesEntity factura = facturas.get(i);
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

                    for (VtRetencionesValoresEntity valores : factura.getValoresEntity()) {
                        // IVA
                        if (valores.getCodigo().equals("2")) {
                            // cero
                            if (valores.getCodigoRetencion().equals("0")) {
                                baseCero = valores.getBaseImponible();
                            }
                            // noobjeto
                            if (valores.getCodigoRetencion().equals("6")) {
                                baseNoObjeto = valores.getBaseImponible();
                            }
                            // exenta
                            if (valores.getCodigoRetencion().equals("7")) {
                                baseExenta = valores.getBaseImponible();
                            }
                            //5 %
                            if (valores.getCodigoRetencion().equals("5")) {
                                baseGrav5 = valores.getBaseImponible();
                                valorIva5 = valores.getValorRetenido();
                            }
                            // 8%
                            if (valores.getCodigoRetencion().equals("8")) {
                                baseGrav8 = valores.getBaseImponible();
                                valorIva8 = valores.getValorRetenido();
                            }
                            // 15%
                            if (valores.getCodigoRetencion().equals("4")) {
                                baseGrav15 = valores.getBaseImponible();
                                valorIva15 = valores.getValorRetenido();
                            }
                        }
                    }

                    row.createCell(0).setCellValue("RET");
                    row.createCell(1).setCellValue(factura.getSerieRetencion());
                    row.createCell(2).setCellValue(factura.getSecuencialRetencion());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmisionRetencion()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacionRetencion());
                    row.createCell(5).setCellValue(factura.getCliente().getTercero());
                    row.createCell(6).setCellValue(factura.getCliente().getNumeroIdentificacion());

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


    public void exportarPDF(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {

        List<VtRetencionesEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getNumeroIdentificacion(), filters.getSerie(), filters.getSecuencial());

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
            for (VtRetencionesEntity factura : facturas) {

                BigDecimal baseCero = BigDecimal.valueOf(0);
                BigDecimal baseNoObjeto = BigDecimal.valueOf(0);
                BigDecimal baseExenta = BigDecimal.valueOf(0);

                BigDecimal baseGrav15 = BigDecimal.valueOf(0);
                BigDecimal valorIva15 = BigDecimal.valueOf(0);

                BigDecimal baseGrav8 = BigDecimal.valueOf(0);
                BigDecimal valorIva8 = BigDecimal.valueOf(0);

                BigDecimal baseGrav5 = BigDecimal.valueOf(0);
                BigDecimal valorIva5 = BigDecimal.valueOf(0);

                for (VtRetencionesValoresEntity valores : factura.getValoresEntity()) {
                    // IVA
                    if (valores.getCodigo().equals("2")) {
                        // cero
                        if (valores.getCodigoRetencion().equals("0")) {
                            baseCero = valores.getBaseImponible();
                        }
                        // noobjeto
                        if (valores.getCodigoRetencion().equals("6")) {
                            baseNoObjeto = valores.getBaseImponible();
                        }
                        // exenta
                        if (valores.getCodigoRetencion().equals("7")) {
                            baseExenta = valores.getBaseImponible();
                        }
                        //5 %
                        if (valores.getCodigoRetencion().equals("5")) {
                            baseGrav5 = valores.getBaseImponible();
                            valorIva5 = valores.getValorRetenido();
                        }
                        // 8%
                        if (valores.getCodigoRetencion().equals("8")) {
                            baseGrav8 = valores.getBaseImponible();
                            valorIva8 = valores.getValorRetenido();
                        }
                        // 15%
                        if (valores.getCodigoRetencion().equals("4")) {
                            baseGrav15 = valores.getBaseImponible();
                            valorIva15 = valores.getValorRetenido();
                        }
                    }
                }

                table.addCell(factura.getSerieRetencion());
                table.addCell(factura.getSecuencialRetencion());
                table.addCell(factura.getFechaEmisionRetencion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                table.addCell(factura.getNumeroAutorizacionRetencion());
                table.addCell(factura.getCliente().getNumeroIdentificacion());

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


    private VtRetencionesEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                       FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return vtVentaRepository
                        .findById(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return vtVentaRepository
                            .findById(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return vtVentaRepository
                        .findById(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }
}
