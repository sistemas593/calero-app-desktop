package com.calero.lili.core.modVentas.notasDebito;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.services.BuscarDatosEmpresa;
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl;
import com.calero.lili.core.dtos.DetallesDto;
import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.OrigenEnum;
import com.calero.lili.core.enums.TipoEmision;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesRepository;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasPersistenceService;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.builder.GetListResponseBuilder;
import com.calero.lili.core.modVentas.dto.GetVentasListDto;
import com.calero.lili.core.modVentas.notasDebito.builder.VtNotasDebitoBuilder;
import com.calero.lili.core.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import com.calero.lili.core.modVentas.notasDebito.dto.FilterListNotasDebitoDto;
import com.calero.lili.core.modVentas.notasDebito.dto.GetNotaDebitoDto;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.modVentas.service.ValidarServiceImpl;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.calcularValores.CalcularValoresDocumentos;
import com.calero.lili.core.utils.validaciones.ValidarCampoAscii;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VtVentasNotasDebitoServiceImpl {

    private final VtVentasRepository vtVentaRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final VtNotasDebitoBuilder vtNotasDebitoBuilder;
    private final GetListResponseBuilder getListResponseBuilder;
    private final ComprobanteServiceImpl vtComprobanteService;
    private final GeItemsRepository geItemsRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;
    private final VtVentasPersistenceService vtVentasPersistenceService;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final AdLogsBuilder adLogsBuilder;
    private final ProcesarDocumentosServiceImpl procesarDocumentosService;
    private final AdEmpresasRepository adEmpresasRepository;
    private final ValidarServiceImpl validarService;
    private final AdEmpresasSeriesRepository adEmpresasSeriesRepository;
    private final CalcularValoresDocumentos calcularValoresDocumentos;


    public RespuestaProcesoGetDto create(Long idData, Long idEmpresa,
                                         CreationNotaDebitoRequestDto request, String usuario,
                                         String origenCertificado) {


        DateUtils.validarFechaEmision(request.getFechaEmision());
        ValidarCampoAscii.validarStrings(request);
        validarNumeroAutorizacion(request);

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));
        AdEmpresasSeriesEntity serie = adEmpresasSeriesRepository
                .findBySerie(idData, idEmpresa, request.getSerie())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0}, serie {1} no existe", idEmpresa, request.getSerie())));


        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));

        List<ValoresDto> valores = calcularValoresDocumentos.validarValores(request.getDetalle());
        request.setValores(valores);
        setearValoresCabecera(valores, request);

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        validarTotalConsumidorFinal(request, tercero);
        VtVentaEntity vtVentaEntity = vtNotasDebitoBuilder.builderEntity(request, idData, idEmpresa);
        vtVentaEntity.setTercero(tercero);
        vtVentaEntity.setEmail(tercero.getEmail());
        vtVentaEntity.setCreatedBy(usuario);
        vtVentaEntity.setCreatedDate(LocalDateTime.now());

        vtVentaEntity.setTipoEmision(getTipoEmision(request));
        VtVentaEntity saved = vtVentasPersistenceService.guardarNotaDebito(vtVentaEntity, empresa, serie, idData, idEmpresa);

        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (empresa.getMomentoEnvioNotaDebito() == 2) {

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

            respuestaProcesoGetDto = procesarDocumentosService.procesarFacNcNd(saved,
                    adLogsBuilder.builderVentasDocumentos(saved, Boolean.FALSE), datosEmpresaDto);
            respuestaProcesoGetDto.setIdDocumento(saved.getIdVenta());
        }

        if (Objects.isNull(respuestaProcesoGetDto.getNumeroAutorizacion())) {
            respuestaProcesoGetDto.setIdDocumento(saved.getIdVenta());
            respuestaProcesoGetDto.setNumeroAutorizacion("");
            respuestaProcesoGetDto.setEmailEstado(saved.getEmailEstado());
            respuestaProcesoGetDto.setEstadoDocumento(saved.getEstadoDocumento().getEstadoDocumento());
        }

        return respuestaProcesoGetDto;

    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationNotaDebitoRequestDto request, String usuario,
                              FilterListNotasDebitoDto filters, TipoPermiso tipoBusqueda) {

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        List<ValoresDto> valores = calcularValoresDocumentos.validarValores(request.getDetalle());
        request.setValores(valores);
        setearValoresCabecera(valores, request);

        validarNumeroAutorizacion(request);

        AdEmpresaEntity empresa = adEmpresasRepository
                .findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} Empresa {1} no existe", idData, idEmpresa)));
        AdEmpresasSeriesEntity serie = adEmpresasSeriesRepository
                .findBySerie(idData, idEmpresa, request.getSerie())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Empresa {0}, serie {1} no existe", idEmpresa, request.getSerie())));


        validarService.validarNoModificacion(vtVentaEntity);

        DateUtils.validarFechaEmision(request.getFechaEmision());
        ValidarCampoAscii.validarStrings(request);

        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));


        if (!vtVentaEntity.getSerie().equals(request.getSerie())) {
            Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa,
                    TipoVenta.NDB.name(), request.getSerie(), vtVentaEntity.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La nota de debito ya existe TipoIngreso: {0}" +
                        " Serie: {1} Secuencia: {2}", TipoVenta.NCR.name(), request.getSerie(), vtVentaEntity.getSecuencial()));
            }
        }

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarEstadoDocumento(vtVentaEntity);
        validarAmbiente(request);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        validarTotalConsumidorFinal(request, tercero);
        VtVentaEntity update = vtNotasDebitoBuilder.builderUpdateEntity(request, vtVentaEntity);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        update.setTercero(tercero);
        update.setEmail(tercero.getEmail());

        update.setTipoEmision(getTipoEmision(request));
        vtComprobanteService.getComprobanteXmlNotaDebito(idData, update, empresa, serie);
        VtVentaEntity vtVentaEntityDto = vtVentaRepository.save(update);

        return responseApiBuilder.builderResponse(vtVentaEntityDto.getIdVenta().toString());

    }

    private void validarEstadoDocumento(VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListNotasDebitoDto filters,
                       TipoPermiso tipoBusqueda) {

        VtVentaEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(venta);
    }

    public GetNotaDebitoDto findById(Long idData, Long idEmpresa, UUID idVenta, FilterListNotasDebitoDto filters,
                                     TipoPermiso tipoBusqueda, String usuario) {


        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return vtNotasDebitoBuilder.builderResponse(vtVentaEntity);
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idVenta, FilterListNotasDebitoDto filters,
                                           TipoPermiso tipoBusqueda, String usuario) {


        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return vtVentaEntity.getMensajes();
    }


    public PaginatedDto<GetVentasListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListNotasDebitoDto filters, Pageable pageable,
                                                          TipoPermiso tipoBusqueda, String usuario) {

        filters.setTipoVenta("NDB");
        Page<VtVentaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        if (page.isEmpty()) {
            throw new GeneralException("No existen datos a mostrar");
        }

        List<GetVentasListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoResponse(item);
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

    /*public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                                      TipoPermiso tipoBusqueda, String usuario) {

        Page<VtVentaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        if (page.isEmpty()) {
            throw new GeneralException("No existen datos a mostrar");
        }

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoResponse(item);
            }
            return getListResponseBuilder.builderListResponse(item);
        }).toList();

        List<TotalesProjection> totalValoresProjection = vtVentaRepository.totalValores(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                filters.getFechaEmisionHasta(), filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial());

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

    }*/

    private void validarAmbiente(CreationNotaDebitoRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E) && Objects.isNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente para el tipo de documento electronico no existe");
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.F) && Objects.nonNull(request.getAmbiente())) {
            throw new GeneralException("El ambiente en los documentos físicos no es necesario");
        }
    }

    private Integer getTipoEmision(CreationNotaDebitoRequestDto request) {
        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            return TipoEmision.NORMAL.getCodigoEmision();
        }
        return null;
    }

    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idVenta,
                                     FilterListNotasDebitoDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtVentaEntity.getAnulada()) {
            vtVentaEntity.setAnulada(Boolean.TRUE);
            vtVentaEntity.setFechaAnulacion(LocalDate.now());
            vtVentaRepository.save(vtVentaEntity);
        }
        return responseApiBuilder.builderResponse(idVenta.toString());
    }


    public void validarInfoAddicional(CreationNotaDebitoRequestDto request) {

        if (Objects.nonNull(request.getInformacionAdicional())) {
            if (request.getInformacionAdicional().isEmpty()) {
                request.setInformacionAdicional(null);
            }
        }

        for (DetallesDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    item.setDetAdicional(null);
                }
            }
        }
    }

    private void validarItem(CreationNotaDebitoRequestDto request, Long idData, Long idEmpresa) {
        for (DetallesDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }

    private List<Integer> getIntegerTarifaIva(List<ValoresDto> valores) {
        return valores.stream()
                .map(ValoresDto::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }

    private Page<VtVentaEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa, FilterListNotasDebitoDto filters,
                                                        Pageable pageable, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), null, filters.getTipoVenta(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), null, OrigenEnum.VTS, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(), filters.getFechaEmisionDesde(),
                            filters.getFechaEmisionHasta(), null, filters.getTipoVenta(), filters.getSerie(),
                            filters.getSecuencial(), filters.getNumeroAutorizacion(), null, OrigenEnum.VTS, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), null, filters.getTipoVenta(), filters.getSerie(),
                        filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, OrigenEnum.VTS, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private VtVentaEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                 FilterListNotasDebitoDto filters, TipoPermiso tipoBusqueda, String usuario) {

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

    private void validarTotalConsumidorFinal(CreationNotaDebitoRequestDto request, GeTerceroEntity tercero) {
        if (tercero.getNumeroIdentificacion().equals("9999999999")) {
            if (request.getTotal().compareTo(new BigDecimal("50")) > 0) {
                throw new GeneralException("El total no puede ser mayor a 50 para el consumidor final");
            }
        }
    }

    private void validarNumeroAutorizacion(CreationNotaDebitoRequestDto request) {


        if (request.getFormatoDocumento().equals(FormatoDocumento.F)) {
            if (Objects.isNull(request.getNumeroAutorizacion()) || request.getNumeroAutorizacion().isEmpty()) {
                throw new GeneralException("Un documento físico debe tener número de autorización");
            }

            if (request.getNumeroAutorizacion().length() == 49 || request.getNumeroAutorizacion().length() == 10) {

                if (!request.getNumeroAutorizacion().matches("\\d+")) {
                    throw new GeneralException("El número de autorización no puede contener caracteres que no sean númericos");
                }

            } else {
                throw new GeneralException("El número de autorización no cumple con la cantidad de dígitos");
            }
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            if (Objects.nonNull(request.getNumeroAutorizacion())) {
                if (request.getNumeroAutorizacion().isEmpty()) {
                    request.setNumeroAutorizacion(null);
                }
            }
        }
    }

    private void setearValoresCabecera(List<ValoresDto> valores, CreationNotaDebitoRequestDto request) {

        BigDecimal totalDescuento = request.getDetalle().stream()
                .map(DetallesDto::getDescuento)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotal = request.getDetalle().stream()
                .map(DetallesDto::getSubtotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalImpuesto = valores.stream()
                .map(ValoresDto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotalConDescuento = subtotal.subtract(totalDescuento);
        BigDecimal total = subtotalConDescuento.add(totalImpuesto);

        if (request.getTotal().compareTo(total) != 0) {
            throw new GeneralException(MessageFormat
                    .format("El total calculado no coincide con el total enviado " +
                            " TOTAL ENVIADO: {0} | TOTAL CALCULADO: {1}", request.getTotal(), total));
        }

        request.setTotalDescuento(totalDescuento);
        request.setSubtotal(subtotal);
        request.setTotal(total);
        validarTotalPagoSri(request);
    }

    private void validarTotalPagoSri(CreationNotaDebitoRequestDto request) {

        BigDecimal totalPagoSri = request.getFormasPagoSri().stream()
                .map(FormasPagoDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagoSri.compareTo(request.getTotal()) != 0) {
            throw new GeneralException(MessageFormat.format("El valor total del documento y el el valor de total de las formas de pago SRI no coinciden:" +
                    " TOTAL DOCUMENTO: {0}  | TOTAL FORMA DE PAGO SRI: {1}", request.getTotal(), totalPagoSri));
        }

    }

}

