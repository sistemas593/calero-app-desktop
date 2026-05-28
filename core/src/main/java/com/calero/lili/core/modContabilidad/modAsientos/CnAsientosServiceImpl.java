package com.calero.lili.core.modContabilidad.modAsientos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesRepository;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modComprasItems.GeItemsRepository;
import com.calero.lili.core.modContabilidad.modAsientos.builder.CnAsientosBuilder;
import com.calero.lili.core.modContabilidad.modAsientos.builder.CnAsientosDetallesBuilder;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetListDto;
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosRepository;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentasRepository;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CnAsientosServiceImpl {

    private final CnAsientosRepository cnAsientosRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final CnAsientosBuilder cnAsientosBuilder;
    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final GeTercerosRepository geTercerosRepository;
    private final CnAsientosDetallesBuilder cnAsientosDetallesBuilder;
    private final GeItemsRepository geItemsRepository;
    private final CnCentroCostosRepository cnCentroCostosRepository;

    public ResponseDto create(Long idData, Long idEmpresa, CreationAsientosRequestDto request, String usuario) {


        validarSucursal(request, idData, idEmpresa);
        validarPlanCuenta(idData, idEmpresa, request);

        CnAsientosEntity entity = cnAsientosBuilder.builderEntity(request, idData, idEmpresa);
        setearDetallesYTercero(idData, idEmpresa, request, entity);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        CnAsientosEntity cnAsientosEntity = cnAsientosRepository.save(entity);
        return responseApiBuilder.builderResponse(cnAsientosEntity.getIdAsiento().toString());
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idVenta, CreationAsientosRequestDto request,
                              String usuario, FilterListDto filters, TipoPermiso tipoBusqueda) {

        CnAsientosEntity exists = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        validarSucursal(request, idData, idEmpresa);
        validarPlanCuenta(idData, idEmpresa, request);
        exists = cnAsientosBuilder.builderUpdateEntity(request, exists);
        updateSetearDetallesYTercero(idData, idEmpresa, request, exists);
        exists.setModifiedBy(usuario);
        exists.setModifiedDate(LocalDateTime.now());

        CnAsientosEntity cnAsientosEntity = cnAsientosRepository.save(exists);
        return responseApiBuilder.builderResponse(cnAsientosEntity.getIdAsiento().toString());

    }

    public void delete(Long idData, Long idEmpresa, UUID idVenta, String usuario,
                       FilterListDto filters, TipoPermiso tipoBusqueda) {

        CnAsientosEntity venta = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);

        venta.setDelete(Boolean.TRUE);
        venta.setDeletedBy(usuario);
        venta.setDeletedDate(LocalDateTime.now());

        cnAsientosRepository.save(venta);

    }


    public GetDto findById(Long idData, Long idEmpresa, UUID idVenta,
                           FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        CnAsientosEntity cnAsientosEntity = validacionTipoBusqueda(idData, idEmpresa, idVenta, filters, tipoBusqueda, usuario);
        return cnAsientosBuilder.builderResponse(cnAsientosEntity);
    }

    public PaginatedDto<GetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterListDto filters, Pageable pageable,
                                                    TipoPermiso tipoBusqueda, String usuario) {

        Page<CnAsientosEntity> page = getTipoBusquedaPaginado(idData, idEmpresa, filters, pageable, tipoBusqueda, usuario);

        List<GetListDto> dtoList = page.stream().map(cnAsientosBuilder::builderListResponse).toList();

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


    public void validarPlanCuenta(Long idData, Long idEmpresa, CreationAsientosRequestDto request) {

        for (CreationAsientosRequestDto.DetailDto item : request.getDetalle()) {
            CnPlanCuentaEntity planCuenta = cnPlanCuentasRepository.findByIdCuenta(idData, idEmpresa, item.getIdCuenta())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("La cuenta con id {0} no existe",
                            item.getIdCuenta())));

            if (planCuenta.getMayor()) {
                throw new GeneralException("En las cuentas mayores no se permiten realizar movimientos.");
            }
        }
    }

    private void validarSucursal(CreationAsientosRequestDto request, Long idData, Long idEmpresa) {
        adEmpresasSucursalesRepository
                .findfirstByIdDataAndIdEmpresaAAndSucursal(idData, idEmpresa, request.getSucursal())
                .orElseThrow(() -> new GeneralException("No existe sucursal"));
    }


    private Page<CnAsientosEntity> getTipoBusquedaPaginado(Long idData, Long idEmpresa,
                                                           FilterListDto filters, Pageable pageable,
                                                           TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS -> {
                return cnAsientosRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), null, pageable);
            }
            case SUCURSAL -> {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return cnAsientosRepository.findAllPaginate(idData, idEmpresa, filters.getSucursal(),
                            filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), null, pageable);
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS -> {
                return cnAsientosRepository.findAllPaginate(idData, idEmpresa, null,
                        filters.getFechaEmisionDesde(), filters.getFechaEmisionHasta(), usuario, pageable);
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private CnAsientosEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                    FilterListDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return cnAsientosRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return cnAsientosRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return cnAsientosRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private void setearDetallesYTercero(Long idData, Long idEmpresa, CreationAsientosRequestDto request, CnAsientosEntity entity) {

        if (Objects.nonNull(request.getIdTercero())) {
            GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format
                            ("El tercero con id {0}, no existe", request.getIdTercero())));
            entity.setTercero(tercero);
        } else {
            entity.setTercero(null);
        }
        setearDetalles(idData, idEmpresa, request, entity);

    }

    private void updateSetearDetallesYTercero(Long idData, Long idEmpresa, CreationAsientosRequestDto request, CnAsientosEntity entity) {

        entity.getDetalleEntity().clear();
        if (Objects.nonNull(request.getIdTercero())) {
            GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format
                            ("El tercero con id {0}, no existe", request.getIdTercero())));
            entity.setTercero(tercero);
        } else {
            entity.setTercero(null);
        }
        updateSetearDetalles(idData, idEmpresa, request, entity);

    }


    private void setearDetalles(Long idData, Long idEmpresa, CreationAsientosRequestDto request, CnAsientosEntity entity) {

        List<CnAsientosDetalleEntity> detalles = new ArrayList<>();
        for (CreationAsientosRequestDto.DetailDto model : request.getDetalle()) {
            CnAsientosDetalleEntity detalle = cnAsientosDetallesBuilder.builderAsientoDetalle(model, idData, idEmpresa);
            setearTerceroDetalle(idData, model, detalle);
            setearItemDetalles(idData, idEmpresa, model, detalle);
            setearCentroCostos(idData, idEmpresa, model, detalle);
            detalles.add(detalle);
        }
        entity.setDetalleEntity(detalles);

    }

    private void updateSetearDetalles(Long idData, Long idEmpresa, CreationAsientosRequestDto request, CnAsientosEntity entity) {

        List<CnAsientosDetalleEntity> detalles = new ArrayList<>();
        for (CreationAsientosRequestDto.DetailDto model : request.getDetalle()) {
            CnAsientosDetalleEntity detalle = cnAsientosDetallesBuilder.builderAsientoDetalle(model, idData, idEmpresa);
            setearTerceroDetalle(idData, model, detalle);
            setearItemDetalles(idData, idEmpresa, model, detalle);
            setearCentroCostos(idData, idEmpresa, model, detalle);
            detalles.add(detalle);
        }
        entity.getDetalleEntity().addAll(detalles);

    }


    private void setearTerceroDetalle(Long idData, CreationAsientosRequestDto.DetailDto model, CnAsientosDetalleEntity detalle) {
        if (Objects.nonNull(model.getIdTercero())) {
            GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, model.getIdTercero())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format
                            ("El tercero con id {0}, no existe", model.getIdTercero())));
            detalle.setTercero(tercero);
        } else {
            detalle.setTercero(null);
        }
    }

    private void setearItemDetalles(Long idData, Long idEmpresa, CreationAsientosRequestDto.DetailDto model,
                                    CnAsientosDetalleEntity detalle) {

        if (Objects.nonNull(model.getIdItem())) {
            GeItemEntity item = geItemsRepository.findByIdItem(idData, idEmpresa, model.getIdItem())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format
                            ("El item con id {0} no existe", model.getIdItem())));
            detalle.setGeItem(item);
        } else {
            detalle.setGeItem(null);
        }
    }

    private void setearCentroCostos(Long idData, Long idEmpresa,
                                    CreationAsientosRequestDto.DetailDto model, CnAsientosDetalleEntity detalle) {

        if (Objects.nonNull(model.getIdCentroCostos())) {
            CnCentroCostosEntity centroCostos = cnCentroCostosRepository.findByIdCentroCostos(idData, idEmpresa, model.getIdCentroCostos())
                    .orElseThrow(() -> new GeneralException(MessageFormat.format
                            ("El centro de costo con id {0}, no existe", model.getIdCentroCostos())));
            detalle.setCentroCostos(centroCostos);

        } else {
            detalle.setCentroCostos(null);
        }

    }
}



