package com.calero.lili.core.modCxP.XpFacturas;

import com.calero.lili.core.enums.TipoPermiso;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto;
import com.calero.lili.core.modCxP.XpFacturas.builder.XpFacturasBuilder;
import com.calero.lili.core.modCxP.XpFacturas.dto.FilterXpFacturaDto;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasRequestDto;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasResponseDto;
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
public class XpFacturaServiceImpl {

    private final XpFacturasRepository xpFacturasRepository;
    private final XpFacturasBuilder xpFacturasBuilder;
    private final ResponseApiBuilder responseApiBuilder;


    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, XpFacturasRequestDto request, String usuario) {

        Optional<XpFacturasEntity> existFactura = xpFacturasRepository
                .findByExistFactura(idData, idEmpresa, request.getTipoDocumento().name(), request.getSerie(), request.getSecuencial());

        if (existFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La factura ya existe con sucursal: {0}," +
                            " serie: {1}, tipo documento: {2}",
                    request.getSucursal(), request.getSerie(), request.getTipoDocumento()));
        }


        validarSaldoValor(request);
        XpFacturasEntity entidad = xpFacturasBuilder.builderEntity(request, idData, idEmpresa);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(xpFacturasRepository.save(entidad).getIdFactura().toString());
    }


    @Transactional
    public ResponseDto update(Long idData, Long idEmpresa, UUID idFactura, XpFacturasRequestDto request,
                              String usuario, FilterXpFacturaDto filters, TipoPermiso tipoBusqueda) {

        XpFacturasEntity existFactura = validacionTipoBusqueda(idData, idEmpresa, idFactura, filters, tipoBusqueda, usuario);

        validarSaldoValor(request);
        validarRegistroAplicados(existFactura);

        XpFacturasEntity update = xpFacturasBuilder.builderUpdateEntity(request, existFactura);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return responseApiBuilder.builderResponse(xpFacturasRepository
                .save(update)
                .getIdFactura().toString());

    }


    public void delete(Long idData, Long idEmpresa, UUID idFactura, String usuario,
                       FilterXpFacturaDto filters, TipoPermiso tipoBusqueda) {
        XpFacturasEntity existFactura = validacionTipoBusqueda(idData, idEmpresa, idFactura, filters, tipoBusqueda, usuario);

        existFactura.setDelete(Boolean.TRUE);
        existFactura.setDeletedBy(usuario);
        existFactura.setDeletedDate(LocalDateTime.now());

        xpFacturasRepository.save(existFactura);
    }


    public XpFacturasResponseDto findById(Long idData, Long idEmpresa, UUID idFactura,
                                          FilterXpFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        return xpFacturasBuilder.builderResponse(
                validacionTipoBusqueda(idData, idEmpresa, idFactura, filters, tipoBusqueda, usuario));
    }

    public PaginatedDto<XpFacturasResponseDto> findAllPaginate(Long idData, Long idEmpresa, FilterXpFacturaDto filter, Pageable pageable) {

        Page<XpFacturasEntity> page = xpFacturasRepository
                .findAllPaginate(idData, idEmpresa, filter.getFechaRegistroDesde(), filter.getFechaRegistroHasta(), filter.getIdTercero(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaGetListDto>();

        List<XpFacturasResponseDto> dtoList = page.stream().map(xpFacturasBuilder::builderResponse).toList();
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


    private XpFacturasEntity validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idFactura,
                                                    FilterXpFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS:
                return xpFacturasRepository
                        .findByIdEntity(idData, idEmpresa, idFactura, null, null)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("La factura con ID {0} no existe", idFactura)));

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    return xpFacturasRepository
                            .findByIdEntity(idData, idEmpresa, idFactura, filters.getSucursal(), null)
                            .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento en la sucursal {0}", filters.getSucursal())));
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {
                return xpFacturasRepository
                        .findByIdEntity(idData, idEmpresa, idFactura, null, usuario)
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("No tiene acceso al documento el usuario: {0}", usuario)));
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private void validarSaldoValor(XpFacturasRequestDto request) {
        if (request.getSaldo().compareTo(request.getValor()) != 0) {
            throw new GeneralException("El saldo y el valor no pueden ser diferentes");
        }
    }

    private void validarRegistroAplicados(XpFacturasEntity existFactura) {
        if (!existFactura.getRegistrosAplicados().equals(BigInteger.ZERO)) {
            throw new GeneralException("No se puede realizar una modificación por que ya cuenta con registros de pagos");
        }
    }
}
