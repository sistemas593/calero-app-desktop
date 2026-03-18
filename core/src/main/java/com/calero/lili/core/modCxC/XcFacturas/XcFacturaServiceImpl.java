package com.calero.lili.core.modCxC.XcFacturas;

import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.modCxC.XcFacturas.builder.XcFacturasBuilder;
import com.calero.lili.core.modCxC.XcFacturas.dto.FilterXcFacturaDto;
import com.calero.lili.core.modCxC.XcFacturas.dto.RequestXcFacturasDto;
import com.calero.lili.core.modCxC.XcFacturas.dto.ResponseXcFacturasDto;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XcFacturaServiceImpl {

    private final XcFacturasRepository xcFacturasRepository;
    private final XcFacturasBuilder xcFacturasBuilder;
    private final ResponseApiBuilder responseApiBuilder;


    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, RequestXcFacturasDto request, UUID idFactura, String usuario) {

        Optional<XcFacturasEntity> existFactura = xcFacturasRepository
                .findByExistFactura(idData, idEmpresa, request.getTipoDocumento().name(), request.getSerie(), request.getSecuencial());

        if (existFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La factura ya existe con sucursal: {0}," +
                            " serie: {1}, tipo documento: {2}",
                    request.getSucursal(), request.getSerie(), request.getTipoDocumento()));
        }


        validarSaldoValor(request);
        XcFacturasEntity entidad = xcFacturasBuilder.builderEntity(request, idData, idEmpresa, idFactura);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(xcFacturasRepository.save(entidad).getIdFactura().toString());
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idComprobante, RequestXcFacturasDto request,
                              String usuario, FilterXcFacturaDto filters, TipoPermiso tipoBusqueda) {

        XcFacturasEntity existFactura = validacionTipoBusqueda(idData, idEmpresa, idComprobante, filters, tipoBusqueda, usuario);

        validarSaldoValor(request);
        validarRegistroAplicados(existFactura);

        XcFacturasEntity update = xcFacturasBuilder.builderUpdateEntity(request, existFactura);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(xcFacturasRepository
                .save(update)
                .getIdFactura().toString());

    }


    public void delete(Long idData, Long idEmpresa, UUID idComprobante, String usuario,
                       FilterXcFacturaDto filters, TipoPermiso tipoBusqueda) {
        XcFacturasEntity existFactura = validacionTipoBusqueda(idData, idEmpresa, idComprobante, filters, tipoBusqueda, usuario);

        existFactura.setDelete(Boolean.TRUE);
        existFactura.setDeletedBy(usuario);
        existFactura.setDeletedDate(LocalDateTime.now());

        xcFacturasRepository.save(existFactura);
    }


    public ResponseXcFacturasDto findById(Long idData, Long idEmpresa, UUID idComprobante,
                                          FilterXcFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        return xcFacturasBuilder.builderResponse(
                validacionTipoBusqueda(idData, idEmpresa, idComprobante, filters, tipoBusqueda, usuario));
    }

    public PaginatedDto<ResponseXcFacturasDto> findAllPaginate(Long idData, Long idEmpresa, FilterXcFacturaDto filter, Pageable pageable) {

        Page<XcFacturasEntity> page = xcFacturasRepository
                .findAllPaginate(idData, idEmpresa, filter.getFechaEmisionDesde(), filter.getFechaEmisionHasta(), filter.getIdTercero(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaGetListDto>();

        List<ResponseXcFacturasDto> dtoList = page.stream().map(xcFacturasBuilder::builderResponse).toList();
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
        paginated.setNumber(page.getNumber());
        paginatedDto.setPaginator(paginated);
        return paginatedDto;

    }


    private void validarSaldoValor(RequestXcFacturasDto request) {
        if (request.getSaldo().compareTo(request.getValor()) != 0) {
            throw new GeneralException("El saldo y el valor no pueden ser diferentes");
        }
    }

    private void validarRegistroAplicados(XcFacturasEntity existFactura) {
        if (!existFactura.getRegistrosAplicados().equals(BigInteger.ZERO)) {
            throw new GeneralException("No se puede realizar una modificación por que ya cuenta con registros de pagos");
        }
    }


    private XcFacturasEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idVenta,
                                                    FilterXcFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                return xcFacturasRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idVenta)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    return xcFacturasRepository
                            .findByIdEntity(idData, idEmpresa, idVenta, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                return xcFacturasRepository
                        .findByIdEntity(idData, idEmpresa, idVenta, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

}
