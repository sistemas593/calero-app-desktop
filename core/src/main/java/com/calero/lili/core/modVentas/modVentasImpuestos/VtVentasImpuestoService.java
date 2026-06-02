package com.calero.lili.core.modVentas.modVentasImpuestos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.OrigenEnum;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentas.builder.GetListResponseBuilder;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.core.modVentas.modVentasImpuestos.builder.VtVentasImpuestoBuilder;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.CreationVentaImpuestoRequestDto;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.VentaImpuestoResponseDto;
import com.calero.lili.core.modVentas.projection.OneProjection;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class VtVentasImpuestoService {

    private final VtVentasRepository vtVentaRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final VtVentasImpuestoBuilder vtVentasImpuestoBuilder;
    private final GetListResponseBuilder getListResponseBuilder;
    private final GeTercerosRepository geTercerosRepository;

    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa,
                              CreationVentaImpuestoRequestDto request, String usuario) {

        ValidacionDocumentosGeneral.validarSizeSecuencial(request.getSecuencial());
        validarNumeroAutorizacion(request);

        Optional<OneProjection> existente = vtVentaRepository
                .findExistBySecuencial(idData, idEmpresa, request.getTipoVenta().name(),
                        request.getSerie(), request.getSecuencial());

        if (existente.isPresent()) {
            throw new GeneralException(MessageFormat.format("El documento ya existe TipoVenta:" +
                            " {0} Serie: {1} Secuencial: {2}", request.getTipoVenta().name(),
                    request.getSerie(), request.getSecuencial()));
        }

        GeTerceroEntity tercero = validacionTercero(idData, request);

        VtVentaEntity entity = vtVentasImpuestoBuilder.builderEntity(request, idData, idEmpresa);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setTercero(tercero);
        entity.setEmail(tercero.getEmail());

        VtVentaEntity saved = vtVentaRepository.save(entity);

        return responseApiBuilder.builderResponse(saved.getIdVenta().toString());
    }

    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta,
                              CreationVentaImpuestoRequestDto request,
                              FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        ValidacionDocumentosGeneral.validarSizeSecuencial(request.getSecuencial());
        validarNumeroAutorizacion(request);

        VtVentaEntity entity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        validacionModulo(entity);

        if (!entity.getSerie().equals(request.getSerie()) || !entity.getSecuencial().equals(request.getSecuencial())) {
            Optional<OneProjection> existente = vtVentaRepository
                    .findExistBySecuencial(idData, idEmpresa, request.getTipoVenta().name(),
                            request.getSerie(), request.getSecuencial());
            if (existente.isPresent()) {
                throw new GeneralException(MessageFormat.format("El documento ya existe TipoVenta: {0} Serie: {1} Secuencial: {2}",
                        request.getTipoVenta().name(), request.getSerie(), request.getSecuencial()));
            }
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        VtVentaEntity update = vtVentasImpuestoBuilder.builderUpdateEntity(request, entity);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        update.setTercero(tercero);
        update.setEmail(tercero.getEmail());

        VtVentaEntity saved = vtVentaRepository.save(update);

        return responseApiBuilder.builderResponse(saved.getIdVenta().toString());
    }

    @Transactional
    public ResponseDto delete(Long idData, Long idEmpresa, UUID idVenta,
                              FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity entity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        entity.setDelete(Boolean.TRUE);
        entity.setDeletedBy(usuario);
        entity.setDeletedDate(LocalDateTime.now());

        vtVentaRepository.save(entity);

        return responseApiBuilder.builderResponse(idVenta.toString());
    }

    @Transactional(readOnly = true)
    public VentaImpuestoResponseDto findById(Long idData, Long idEmpresa, UUID idVenta,
                                             FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity entity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        return vtVentasImpuestoBuilder.builderResponse(entity);
    }

    @Transactional(readOnly = true)
    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa,
                                                    FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<VtVentaEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        List<GetListDto> dtoList = page.stream().map(item -> {
            if (item.getAnulada()) {
                return getListResponseBuilder.builderAnuladoResponse(item);
            }
            return getListResponseBuilder.builderListResponse(item);
        }).toList();

        PaginatedDto<GetListDto> paginatedDto = new PaginatedDto<>();
        paginatedDto.setContent(dtoList);

        Paginator paginator = new Paginator();
        paginator.setTotalElements(page.getTotalElements());
        paginator.setTotalPages(page.getTotalPages());
        paginator.setNumberOfElements(page.getNumberOfElements());
        paginator.setSize(page.getSize());
        paginator.setFirst(page.isFirst());
        paginator.setLast(page.isLast());
        paginator.setPageNumber(page.getPageable().getPageNumber());
        paginator.setPageSize(page.getPageable().getPageSize());
        paginator.setEmpty(page.isEmpty());
        paginator.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginator);

        return paginatedDto;
    }

    @Transactional
    public ResponseDto updateAnulada(Long idData, Long idEmpresa, UUID idVenta,
                                     FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        VtVentaEntity entity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        if (!entity.getAnulada()) {
            entity.setAnulada(Boolean.TRUE);
            entity.setFechaAnulacion(LocalDate.now());
            vtVentaRepository.save(entity);
        }

        return responseApiBuilder.builderResponse(idVenta.toString());
    }

    private GeTerceroEntity validacionTercero(Long idData, CreationVentaImpuestoRequestDto request) {

        if (Objects.nonNull(request.getIdTercero())) {

            GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                    .orElseThrow(() -> new GeneralException("No existe tercero"));

            request.setTipoIdentificacion(null);
            request.setNumeroIdentificacion(null);
            request.setTerceroNombre(null);
            request.setDireccion(null);
            request.setEmail(null);

            return tercero;

        } else if (Objects.nonNull(request.getTipoIdentificacion())
                && Objects.nonNull(request.getNumeroIdentificacion())
                && Objects.nonNull(request.getTerceroNombre())
                && Objects.nonNull(request.getDireccion())
                && Objects.nonNull(request.getEmail())) {

            return geTercerosRepository
                    .getFindExistByNumeroIdentificacion(idData, request.getNumeroIdentificacion())
                    .orElseGet(() -> {
                        GeTerceroEntity nuevo = new GeTerceroEntity();
                        nuevo.setIdTercero(UUID.randomUUID());
                        nuevo.setIdData(idData);
                        nuevo.setTipoIdentificacion(request.getTipoIdentificacion().name());
                        nuevo.setNumeroIdentificacion(request.getNumeroIdentificacion());
                        nuevo.setTercero(request.getTerceroNombre());
                        nuevo.setDireccion(request.getDireccion());
                        nuevo.setEmail(request.getEmail());
                        return geTercerosRepository.save(nuevo);
                    });

        } else {
            throw new GeneralException("No existe información del tercero," +
                    " es necesario enviar el id del tercero o la información completa del tercero");
        }
    }

    private void validarNumeroAutorizacion(CreationVentaImpuestoRequestDto request) {

        if (request.getFormatoDocumento().equals(FormatoDocumento.F)) {
            if (Objects.isNull(request.getNumeroAutorizacion()) || request.getNumeroAutorizacion().isEmpty()) {
                throw new GeneralException("Un documento físico debe tener número de autorización");
            }
            if (request.getNumeroAutorizacion().length() == 49 || request.getNumeroAutorizacion().length() == 10) {
                if (!request.getNumeroAutorizacion().matches("\\d+")) {
                    throw new GeneralException("El número de autorización no puede contener caracteres que no sean numéricos");
                }
            } else {
                throw new GeneralException("El número de autorización no cumple con la cantidad de dígitos");
            }
        }

        if (request.getFormatoDocumento().equals(FormatoDocumento.E)) {
            if (Objects.nonNull(request.getNumeroAutorizacion()) && request.getNumeroAutorizacion().isEmpty()) {
                request.setNumeroAutorizacion(null);
            }
        }
    }

    private VtVentaEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                 FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS:
                return vtVentaRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(
                                MessageFormat.format("El documento con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return vtVentaRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(
                                    MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));
                } else {
                    throw new GeneralException("Es requerido el parámetro de la sucursal");
                }
            }

            case PROPIAS:
                return vtVentaRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(
                                MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));
        }

        throw new GeneralException(MessageFormat.format("El tipo de búsqueda: {0} no existe", tipoBusqueda));
    }

    private Page<VtVentaEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa, FilterListDto filters,
                                                        Pageable pageable, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getIdTercero(), filters.getTipoVenta(),
                        filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), null, OrigenEnum.IMP, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return vtVentaRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), filters.getIdTercero(),
                            filters.getTipoVenta(), filters.getSerie(), filters.getSecuencial(),
                            filters.getNumeroAutorizacion(), null, OrigenEnum.IMP, pageable);
                } else {
                    throw new GeneralException("Es requerido el parámetro de la sucursal");
                }
            }
            case PROPIAS -> {
                return vtVentaRepository.findAllPaginate(idData, idEmpresa, null, filters.getFechaEmisionDesde(),
                        filters.getFechaEmisionHasta(), filters.getIdTercero(), filters.getTipoVenta(),
                        filters.getSerie(), filters.getSecuencial(), filters.getNumeroAutorizacion(), usuario, OrigenEnum.IMP, pageable);
            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de búsqueda: {0} no existe", tipoBusqueda));
    }

    private void validacionModulo(VtVentaEntity vtVentaEntity) {
        if (!vtVentaEntity.getOrigen().equals(OrigenEnum.IMP)) {
            throw new GeneralException(MessageFormat
                    .format("El documento con id: {0} no corresponde al módulo de ventas impuestos", vtVentaEntity.getIdVenta()));
        }
    }
}
