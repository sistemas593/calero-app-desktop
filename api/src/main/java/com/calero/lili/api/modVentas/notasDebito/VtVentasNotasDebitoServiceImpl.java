package com.calero.lili.api.modVentas.notasDebito;

import com.calero.lili.api.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.api.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.api.modAuditoria.TipoPermiso;
import com.calero.lili.api.modComprasItems.GeItemsRepository;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modVentas.VtVentaEntity;
import com.calero.lili.api.modVentas.VtVentasRepository;
import com.calero.lili.api.modVentas.builder.GetListResponseBuilder;
import com.calero.lili.api.modVentas.dto.GetListDto;
import com.calero.lili.api.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.api.modVentas.notasDebito.builder.VtNotasDebitoBuilder;
import com.calero.lili.api.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import com.calero.lili.api.modVentas.notasDebito.dto.FilterListDto;
import com.calero.lili.api.modVentas.notasDebito.dto.GetNotaDebitoDto;
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
import com.calero.lili.core.utils.DateUtils;
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
public class VtVentasNotasDebitoServiceImpl {

    private final VtVentasRepository vtVentaRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final VtNotasDebitoBuilder vtNotasDebitoBuilder;
    private final GetListResponseBuilder getListResponseBuilder;
    private final ComprobanteServiceImpl vtComprobanteService;
    private final GeItemsRepository geItemsRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;

    public ResponseDto create(Long idData, Long idEmpresa, CreationNotaDebitoRequestDto request, String usuario) {

        ValidarCampoAscii.validarStrings(request);

        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));

        Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa,
                TipoVenta.NDB.name(), request.getSerie(), request.getSecuencial());

        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe Tipo: {0} Serie: {1} Secuencial: {2}",
                    TipoVenta.NDB.name(), request.getSerie(), request.getSecuencial()));
        }

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarAmbiente(request);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtVentaEntity vtVentaEntity = vtNotasDebitoBuilder.builderEntity(request, idData, idEmpresa);
        vtVentaEntity.setTercero(tercero);
        vtVentaEntity.setEmail(tercero.getEmail());
        vtVentaEntity.setTerceroNombre(tercero.getTercero());
        vtVentaEntity.setCreatedBy(usuario);
        vtVentaEntity.setCreatedDate(LocalDateTime.now());

        vtVentaEntity.setTipoEmision(getTipoEmision(request));
        vtComprobanteService.getComprobanteXmlNotaDebito(idData, idEmpresa, vtVentaEntity);
        VtVentaEntity saved = vtVentaRepository.save(vtVentaEntity);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), TipoVenta.NDB.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe", request.getSerie(), TipoVenta.NCR.name())));

        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return responseApiBuilder.builderResponse(saved.getIdVenta().toString());

    }

    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationNotaDebitoRequestDto request, String usuario,
                              FilterListDto filters, TipoPermiso tipoBusqueda) {

        ValidarCampoAscii.validarStrings(request);

        adIvaPorcentajeService.validateIvaPorcentaje(getIntegerTarifaIva(request.getValores()),
                DateUtils.toLocalDate(request.getFechaEmision()));

        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!vtVentaEntity.getSerie().equals(request.getSerie()) || !vtVentaEntity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existingFactura = vtVentaRepository.findExistBySecuencial(idData, idEmpresa, TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial());
            if (existingFactura.isPresent()) {
                throw new GeneralException(MessageFormat.format("La nota de debito ya existe TipoIngreso: {0} Serie: {1} Secuencia: {2}", TipoVenta.NCR.name(), request.getSerie(), request.getSecuencial()));
            }
        }

        validarItem(request, idData, idEmpresa);
        validarInfoAddicional(request);
        validarEstadoDocumento(vtVentaEntity);
        validarAmbiente(request);

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));


        VtVentaEntity update = vtNotasDebitoBuilder.builderUpdateEntity(request, vtVentaEntity);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        update.setTercero(tercero);
        update.setEmail(tercero.getEmail());
        update.setTerceroNombre(tercero.getTercero());

        update.setTipoEmision(getTipoEmision(request));
        vtComprobanteService.getComprobanteXmlNotaDebito(idData, idEmpresa, update);
        VtVentaEntity vtVentaEntityDto = vtVentaRepository.save(update);

        return responseApiBuilder.builderResponse(vtVentaEntityDto.getIdVenta().toString());

    }

    private void validarEstadoDocumento(VtVentaEntity vtVentaEntity) {
        if (vtVentaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT.getEstadoDocumento())) {
            throw new GeneralException("El documento ya se encuentra autorizado, no se puede realizar modificaciones");
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario, FilterListDto filters,
                       TipoPermiso tipoBusqueda) {

        VtVentaEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(venta);
    }

    public GetNotaDebitoDto findById(Long idData, Long idEmpresa, UUID idVenta, FilterListDto filters,
                                     TipoPermiso tipoBusqueda, String usuario) {


        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return vtNotasDebitoBuilder.builderResponse(vtVentaEntity);
    }


    public List<Mensajes> findByIdMensajes(Long idData, Long idEmpresa, UUID idVenta, FilterListDto filters,
                                           TipoPermiso tipoBusqueda, String usuario) {


        VtVentaEntity vtVentaEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
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
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

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
                throw new GeneralException("La información adicional se envia pero esta vacia");
            }
        }

        for (CreationNotaDebitoRequestDto.DetailDto item : request.getDetalle()) {
            if (Objects.nonNull(item.getDetAdicional())) {
                if (item.getDetAdicional().isEmpty()) {
                    throw new GeneralException("En la lista de detalles, se envia detalle adicional pero este esta vacio");
                }
            }
        }
    }

    private void validarItem(CreationNotaDebitoRequestDto request, Long idData, Long idEmpresa) {
        for (CreationNotaDebitoRequestDto.DetailDto model : request.getDetalle()) {
            geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException("El item con id  " + model.getIdItem() + " no existe "));
        }

    }

    private List<Integer> getIntegerTarifaIva(List<CreationNotaDebitoRequestDto.ValoresDto> valores) {
        return valores.stream()
                .map(CreationNotaDebitoRequestDto.ValoresDto::getTarifa)
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

}

