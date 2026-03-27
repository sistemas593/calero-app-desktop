package com.calero.lili.core.modVentas.facturas;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.NotFoundException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosRepository;
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosRepository;
import com.calero.lili.core.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.GenerarAsientoServiceImpl;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.builder.GetListResponseBuilder;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.facturas.builder.VtFacturasBuilder;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.core.modVentas.facturas.dto.GetFacturaDto;
import com.calero.lili.core.modVentas.facturas.dto.PaisesResponseDto;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.modVentas.projection.TotalesProjection;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentasReembolsoRepository;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisesRepository;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.validaciones.ValidarCampoAscii;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VtVentasFacturasServiceImpl {

    private final VtVentasRepository vtVentaRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    private final ResponseApiBuilder responseApiBuilder;
    private final VtFacturasBuilder vtFacturasBuilder;
    private final GetListResponseBuilder getListResponseBuilder;
    private final ComprobanteServiceImpl vtComprobanteService;
    private final VtVentasReembolsoRepository vtVentasReembolsoRepository;
    private final XcFacturasRepository xcFacturasRepository;
    private final XcFacturasBuilder xcFacturasBuilder;
    private final GeTercerosRepository geTercerosRepository;
    private final TbPaisesRepository tbPaisesRepository;
    private final GeItemsRepository geItemsRepository;
    private final CnCentroCostosRepository cnCentroCostosRepository;
    private final CnAsientosRepository cnAsientosRepository;
    private final GenerarAsientoServiceImpl generarAsientoService;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;

    public ResponseDto create(Long idData, Long idEmpresa, CreationFacturaRequestDto request, String usuario) {


        DateUtils.validarFechaEmision(request.getFechaEmision());
        ValidarCampoAscii.validarStrings(request);
        adIvaPorcentajeService.validateIvaPorcentaje(getTarifaInteger(request.getValores()), DateUtils.toLocalDate(request.getFechaEmision()));

        Optional<OneProjection> existingFactura = vtVentaRepository
                .findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe TipoIngreso:" +
                    " {0} Serie: {1} Secuencia: {2}", TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial()));
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        validarItem(request, idData, idEmpresa);
        validarInformacion(request);
        validarInfoAddicional(request);
        validarAmbiente(request);
        validarCentroCostos(request, idData, idEmpresa);

        VtVentaEntity vtVentaEntity = vtFacturasBuilder.builderEntity(request, idData, idEmpresa);

        vtVentaEntity.setCreatedBy(usuario);
        vtVentaEntity.setCreatedDate(LocalDateTime.now());

        vtVentaEntity.setTercero(tercero);
        vtVentaEntity.setEmail(tercero.getEmail());
        vtVentaEntity.setTipoEmision(getTipoEmision(request));


        validateReembolso(request, vtVentaEntity);
        vtComprobanteService.getComprobanteXmlFactura(idData, idEmpresa, vtVentaEntity);
        VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);

        if (request.getCuentaPorCobrar()) {
            //xcFacturaService.create(idData, idEmpresa, facturaCuentasXCobrarBuilder.builder(request), vtVentaEntityDto.getIdVenta());
            // le paso primero al request y luego del request a la entidad esta mal//////////////////////////
            //RequestXcFacturasDto cxc = facturaCuentasXCobrarBuilder.builder(request);

            xcFacturasRepository.save(xcFacturasBuilder.builderEntityFac(request, idData, idEmpresa, vtVentaEntity.getIdVenta()));
        }

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.FAC.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, Secuencial {1}, documento {2} no existe", request.getSerie(), request.getSecuencial(), TipoVenta.FAC.name())));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));


        return responseApiBuilder.builderResponse(saved.getIdVenta().toString());

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationFacturaRequestDto request,
                              FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        DateUtils.validarFechaEmision(request.getFechaEmision());
        ValidarCampoAscii.validarStrings(request);

        adIvaPorcentajeService.validateIvaPorcentaje(getTarifaInteger(request.getValores()), DateUtils.toLocalDate(request.getFechaEmision()));

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtVentaEntity.getSerie().equals(request.getSerie()) || !vtVentaEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La factura ya existe TipoIngreso: {0} Serie: {1} Secuencia: {2}", TipoVenta.FAC.name(), request.getSerie(), request.getSecuencial()));
            }
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        validarItem(request, idData, idEmpresa);
        validarInformacion(request);
        validarInfoAddicional(request);
        validarEstadoDocumento(vtVentaEntity);
        validarAmbiente(request);
        validarCentroCostos(request, idData, idEmpresa);

        VtVentaEntity update = vtFacturasBuilder.builderUpdateEntity(request, vtVentaEntity);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        update.setTercero(tercero);
        update.setEmail(tercero.getEmail());

        update.setTipoEmision(getTipoEmision(request));

        validarReembolsoUpdate(request, update);
        vtComprobanteService.getComprobanteXmlFactura(idData, idEmpresa, update);
        VtVentaEntity vtVentaEntityDto = vtVentaRepository.save(update);

        if (request.getCuentaPorCobrar()) {

            //xcFacturaService.create(idData, idEmpresa, facturaCuentasXCobrarBuilder.builder(request), vtVentaEntityDto.getIdVenta());
            // le paso primero al request y luego del request a la entidad esta mal//////////////////////////
            //RequestXcFacturasDto cxc = facturaCuentasXCobrarBuilder.builder(request);

            xcFacturasRepository.save(xcFacturasBuilder.builderEntityFac(request, idData, idEmpresa, update.getIdVenta()));
        }

        return responseApiBuilder.builderResponse(vtVentaEntityDto.getIdVenta().toString());

    }


    private void validateReembolso(CreationFacturaRequestDto request, VtVentaEntity vtVentaEntity) {
        validateInfoReembolso(request);
        if (Objects.nonNull(request.getListIdReembolsos())) {

            List<VtVentaReembolsosEntity> list = vtVentasReembolsoRepository.findAllForIds(request.getListIdReembolsos());

            for (VtVentaReembolsosEntity entidad : list) {
                if (Objects.nonNull(entidad.getIdVenta())) {
                    throw new GeneralException("El reembolso con id: " + entidad.getIdVentaReembolsos() +
                            " ya se encuentra registrado en otra factura");
                }
            }
            vtVentaEntity.setReembolsosEntity(list);
        }
    }

    private void validarInformacion(CreationFacturaRequestDto request) {

        validateInfoExportacion(request);

        if (Objects.nonNull(request.getGuiaRemisionSerie()) && Objects.isNull(request.getGuiaRemisionSecuencial())) {
            throw new GeneralException("Debe existir guia de remisión secuencial");
        }

        if (Objects.isNull(request.getGuiaRemisionSerie()) && Objects.nonNull(request.getGuiaRemisionSecuencial())) {
            throw new GeneralException("Debe existir guia de remisión serie");
        }

        if (Objects.nonNull(request.getSustitutivaGuiaRemision()) && Objects.nonNull(request.getGuiaRemisionSerie())) {
            throw new GeneralException("Debe existir serie/secuencial o info sustitutiva de la guia de remisión");
        }

        if (!request.getTipoIngreso().equals(TipoIngreso.EX)) {
            request.setFleteInternacional(BigDecimal.ZERO);
            request.setSeguroInternacional(BigDecimal.ZERO);
            request.setGastosAduaneros(BigDecimal.ZERO);
            request.setGastosTransporteOtros(BigDecimal.ZERO);
        }
    }

    private void validarItem(CreationFacturaRequestDto request, Long idData, Long idEmpresa) {
        for (CreationFacturaRequestDto.DetailDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }


    private void validarReembolsoUpdate(CreationFacturaRequestDto request, VtVentaEntity vtVentaEntity) {

        validateInfoReembolso(request);
        List<VtVentaReembolsosEntity> reembolsosNuevos = vtVentasReembolsoRepository
                .findAllForIds(request.getListIdReembolsos());

        if (Objects.isNull(vtVentaEntity.getReembolsosEntity())) {
            if (Objects.nonNull(reembolsosNuevos) && !reembolsosNuevos.isEmpty()) {

                for (VtVentaReembolsosEntity entidad : reembolsosNuevos) {
                    if (Objects.nonNull(entidad.getIdVenta())) {
                        throw new GeneralException("El reembolso con id: " + entidad.getIdVentaReembolsos() +
                                " ya se encuentra registrado en otra factura");
                    }
                }

                vtVentaEntity.setReembolsosEntity(reembolsosNuevos);
            }
        } else {
            if (Objects.isNull(reembolsosNuevos) || reembolsosNuevos.isEmpty()) {
                vtVentaEntity.getReembolsosEntity().clear();
            } else {
                vtVentaEntity.getReembolsosEntity().clear();
                vtVentaEntity.setReembolsosEntity(reembolsosNuevos);
            }
        }

    }


    private void validarEstadoDocumento(VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, FilterListDto filters,
                       TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(venta);

    }

    public GetFacturaDto findById(Long idData, Long idEmpresa, UUID idVenta,
                                  FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        GetFacturaDto response = vtFacturasBuilder.builderResponse(vtVentaEntity);

        if (Objects.nonNull(vtVentaEntity.getSustitutivaGuiaRemision())) {
            GeTerceroEntity entidad = geTercerosRepository.findByIdCliente(idData,
                            vtVentaEntity.getSustitutivaGuiaRemision().getIdTransportista())
                    .orElseThrow(() -> new GeneralException("No existe transportista"));

            return vtFacturasBuilder.builderResponseWithGuiaSustitutiva(vtVentaEntity, entidad);
        }

        if (Objects.nonNull(vtVentaEntity.getExportacion())) {
            PaisesResponseDto paises = getPaises(vtVentaEntity.getExportacion());
            response.getExportacion().setNombrePaisDestino(paises.getNombrePaisDestino());
            response.getExportacion().setNombrePaisOrigen(paises.getNombrePaisOrigen());
            response.getExportacion().setNombrePaisAdquisicion(paises.getNombrePaisAdquisicion());
        }
        return response;
    }


    public List<Mensajes> findMensajeById(Long idData, Long idEmpresa, UUID idVenta,
                                          FilterListDto filters,
                                          TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return vtVentaEntity.getMensajes();
    }


    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa,
                                                    FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {


        Page<VtVentaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        if (page.isEmpty()) {
            throw new GeneralException("No existen datos a mostrar");
        }

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


    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa,
                                                                      FilterListDto filters, TipoPermiso tipoBusqueda, String usuario,
                                                                      Pageable pageable) {

        Page<VtVentaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        if (page.isEmpty()) {
            throw new GeneralException("No existen datos a mostrar");
        }

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoListResponse(item);
            }
            return getListResponseBuilder.builderListResponse(item);
        }).toList();

        List<TotalesProjection> totalValoresProjection = vtVentaRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial());

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


    /*public void exportarExcel(Long idData, Long idEmpresa, HttpServletResponse response, FilterListDto filter) throws IOException {

        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        List<VtVentaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filter.getSucursal(), filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getTipoVenta(), filter.getSerie(), filter.getSecuencial());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "Facturas_" + currentDateTime + ".xlsx" + "\"");

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

                    row.createCell(0).setCellValue(factura.getTipoVenta());
                    row.createCell(1).setCellValue(factura.getSerie());
                    row.createCell(2).setCellValue(factura.getSecuencial());
                    row.createCell(3).setCellValue(DateUtils.toString(factura.getFechaEmision()));
                    row.createCell(4).setCellValue(factura.getNumeroAutorizacion());
                    row.createCell(5).setCellValue(factura.getTercero().getNumeroIdentificacion());

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


        List<VtVentaEntity> facturas = vtVentaRepository.findAll(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial());

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
                table.addCell(factura.getTercero().getNumeroIdentificacion());

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
    }*/

    private void validarAmbiente(CreationFacturaRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }

    private Integer getTipoEmision(CreationFacturaRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }

    private void validateInfoReembolso(CreationFacturaRequestDto request) {
        if (request.getTipoIngreso().equals(TipoIngreso.RG)) {

            if (Objects.isNull(request.getListIdReembolsos())) {
                throw new GeneralException("El tipo de ingreso corresponde a un reembolso, pero no existe información de los reembolsos");
            }
        }

        if (!request.getTipoIngreso().equals(TipoIngreso.RG)) {
            if (Objects.nonNull(request.getListIdReembolsos())) {
                throw new GeneralException("El tipo de ingreso no corresponde a un reembolso, no es necesario enviar la información de los reembolsos");
            }
        }
    }

    private void validateInfoExportacion(CreationFacturaRequestDto request) {
        if (request.getTipoIngreso().equals(TipoIngreso.EX)) {

            if (Objects.isNull(request.getExportacion())) {
                throw new GeneralException("El tipo de ingreso corresponde a una exportación, pero no existe información de exportación");
            }

            if (Objects.isNull(request.getFleteInternacional()) || Objects.isNull(request.getSeguroInternacional())
                    || Objects.isNull(request.getGastosAduaneros()) || Objects.isNull(request.getGastosTransporteOtros())) {

                throw new GeneralException("Los valores de flete, seguros y gastos internacionales deben existir");
            }

            validacionPaises(request);

        }

        if (!request.getTipoIngreso().equals(TipoIngreso.EX)) {
            if (Objects.nonNull(request.getExportacion())) {
                throw new GeneralException("El tipo de ingreso no corresponde a una exportación, no es necesario enviar la información de exportación");
            }
        }


    }

    private void validacionPaises(CreationFacturaRequestDto request) {
        tbPaisesRepository.findById(request.getExportacion().getPaisDestino())
                .orElseThrow(() -> new GeneralException("El país de destino no existe"));

        tbPaisesRepository.findById(request.getExportacion().getPaisOrigen())
                .orElseThrow(() -> new GeneralException("El país de origen no existe"));

        tbPaisesRepository.findById(request.getExportacion().getPaisAdquisicion())
                .orElseThrow(() -> new GeneralException("El país de adquisición no existe"));

    }


    private PaisesResponseDto getPaises(VtVentaEntity.Exportacion model) {
        TbPaisEntity paisDestino = tbPaisesRepository.findById(model.getPaisDestino())
                .orElseThrow(() -> new GeneralException("El país de destino no existe"));

        TbPaisEntity paisOrigen = tbPaisesRepository.findById(model.getPaisOrigen())
                .orElseThrow(() -> new GeneralException("El país de origen no existe"));

        TbPaisEntity paisAdquisicion = tbPaisesRepository.findById(model.getPaisAdquisicion())
                .orElseThrow(() -> new GeneralException("El país de adquisición no existe"));

        return PaisesResponseDto.builder()
                .nombrePaisOrigen(paisOrigen.getPais())
                .nombrePaisAdquisicion(paisAdquisicion.getPais())
                .nombrePaisDestino(paisDestino.getPais())
                .build();

    }

    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idVenta, FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtVentaEntity.getAnulada()) {
            vtVentaEntity.setAnulada(Boolean.TRUE);
            vtVentaEntity.setFechaAnulacion(LocalDate.now());
            vtVentaRepository.save(vtVentaEntity);
        }
        return responseApiBuilder.builderResponse(idVenta.toString());
    }

    public void validarInfoAddicional(CreationFacturaRequestDto request) {

        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }

        for (CreationFacturaRequestDto.DetailDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    throw new GeneralException("En la lista de detalles, se envia detalle adicional pero este esta vacio");
                }
            }
        }

    }

    private void validarCentroCostos(CreationFacturaRequestDto request, Long idData, Long idEmpresa) {

        for (CreationFacturaRequestDto.DetailDto detalle : request.getDetalle()) {

            if (Objects.nonNull(detalle.getIdCentroCostos())) {
                Optional<CnCentroCostosEntity> item = cnCentroCostosRepository
                        .findByIdCentroCostos(idData, idEmpresa, detalle.getIdCentroCostos());

                if (item.isEmpty()) {
                    throw new GeneralException(MessageFormat.format("El centro de costos con id {0} no existe",
                            detalle.getIdCentroCostos()));
                }

            }
        }

    }


    public ResponseDto createAsientoVenta(Long idData, Long idEmpresa, UUID idVenta) {

        VtVentaEntity vtVentaEntity = vtVentaRepository
                .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

        Boolean generarAsiento = Boolean.TRUE;

        if (generarAsiento) {
            CnAsientosEntity asiento = cnAsientosRepository.save(generarAsientoService.generarAsiento(idData, idEmpresa, vtVentaEntity));
            vtVentaEntity.setIdAsiento(asiento.getIdAsiento());
            vtVentaRepository.save(vtVentaEntity);
        }

        return responseApiBuilder.builderResponse(idVenta.toString());
    }

    private List<Integer> getTarifaInteger(List<CreationFacturaRequestDto.ValoresDto> valores) {
        return valores.stream()
                .map(CreationFacturaRequestDto.ValoresDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }

    private VtVentaEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
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


    private Page<VtVentaEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa, FilterListDto filters,
                                                        Pageable pageable, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getIdTercero(), filters.getTipoVenta(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
            }

            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                            filters.getFechaEmisionHasta(), filters.getIdTercero(), filters.getTipoVenta(), filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }

            case PROPIAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getIdTercero(), filters.getTipoVenta(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, pageable);
            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));

    }


}

