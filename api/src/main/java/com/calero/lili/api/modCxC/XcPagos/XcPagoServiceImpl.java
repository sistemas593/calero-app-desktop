package com.calero.lili.api.modCxC.XcPagos;

import com.calero.lili.api.modAuditoria.TipoPermiso;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.api.modCxC.XcFacturas.dto.FilterXcFacturaDto;
import com.calero.lili.api.modCxC.XcPagos.builder.XcPagosBuilder;
import com.calero.lili.api.modCxC.XcPagos.dto.RequestPagoDto;
import com.calero.lili.api.modCxC.XcPagos.dto.ResponseXcPagosDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.TsComprobanteIngresoServiceImpl;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XcPagoServiceImpl {


    private final XcPagosRepository xcPagosRepository;
    private final XcPagosBuilder xcPagosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final XcFacturasRepository xcFacturasRepository;
    private final TsComprobanteIngresoServiceImpl tsComprobanteIngresoService;

    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, RequestPagoDto request, String usuario) {


        UUID idPagoGrupo = UUID.randomUUID();
        List<XcPagosEntity> entidades = xcPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
        entidades.forEach(e -> {
            e.setCreatedBy(usuario);
            e.setCreatedDate(LocalDateTime.now());
        });
        List<XcPagosEntity> pagosEntities = xcPagosRepository.saveAll(entidades);
        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteIngresoService.create(idData, idEmpresa, idPagoGrupo, request.getComprobanteIngreso(), usuario);
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    @Transactional
    public ResponseDto update(Long idData, UUID idPagoGrupo, Long idEmpresa, RequestPagoDto request,
                              String usuario, FilterXcFacturaDto filters, TipoPermiso tipoBusqueda) {

        List<XcPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);

        validarValoresFactura(request.getIdFactura(), list);
        xcPagosRepository.deleteAll(list);
        List<XcPagosEntity> nuevasEntidades = xcPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
        nuevasEntidades.forEach(e -> {
            e.setModifiedBy(usuario);
            e.setModifiedDate(LocalDateTime.now());
        });
        List<XcPagosEntity> pagosEntities = xcPagosRepository.saveAll(nuevasEntidades);
        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteIngresoService.update(idData, idEmpresa, idPagoGrupo, request.getComprobanteIngreso(), usuario);
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());

    }


    public void delete(Long idData, Long idEmpresa, UUID idPagoGrupo, String usuario,
                       FilterXcFacturaDto filters, TipoPermiso tipoBusqueda) {
        List<XcPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);

        validarValoresFactura(list.getFirst().getFactura().getIdFactura(), list);
        list.forEach(e -> {
            e.setDeletedBy(usuario);
            e.setDeletedDate(LocalDateTime.now());
            e.setDelete(Boolean.TRUE);
            xcPagosRepository.save(e);
        });
        xcPagosRepository.deleteAll(list);
    }

    public List<ResponseXcPagosDto> getFindByIdFactura(Long idData, Long idEmpresa, UUID idPagoGrupo,
                                                        FilterXcFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        List<XcPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);
        return xcPagosBuilder.builderListResponse(list);
    }


    private void validarValoresFactura(UUID idFactura, List<XcPagosEntity> list) {

        XcFacturasEntity entidad = getFacturaEntidad(idFactura);

        BigDecimal totalPagado = list.stream()
                .map(XcPagosEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        entidad.setSaldo(entidad.getSaldo().add(totalPagado));
        entidad.setPagos(entidad.getPagos().subtract(totalPagado));
        entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().subtract(BigInteger.valueOf(list.size())));
        xcFacturasRepository.save(entidad);

    }


    private void actualizarCamposFactura(UUID idFactura, List<XcPagosEntity> pagosEntities) {
        XcFacturasEntity entidad = getFacturaEntidad(idFactura);

        BigDecimal totalPago = pagosEntities.stream()
                .map(XcPagosEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        validaciones(entidad, totalPago);

        entidad.setSaldo(entidad.getSaldo().subtract(totalPago));
        entidad.setPagos(totalPago);
        entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().add(BigInteger.valueOf(pagosEntities.size())));
        xcFacturasRepository.save(entidad);

    }


    private void validaciones(XcFacturasEntity entidad, BigDecimal totalPago) {
        if (entidad.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            throw new GeneralException("La factura ya se encuentra cancelada");
        }

        if (totalPago.compareTo(entidad.getSaldo()) > 0) {
            throw new GeneralException("El valor de los pagos supera al monto total a pagar");
        }

    }

    private XcFacturasEntity getFacturaEntidad(UUID idFactura) {
        return xcFacturasRepository.getForFindById(idFactura)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("No existe factura con el id: {0}", idFactura)));
    }

    private List<XcPagosEntity> validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idPagoGrupo,
                                                       FilterXcFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {

        switch (tipoBusqueda) {
            case TODAS:
                List<XcPagosEntity> list1 = xcPagosRepository
                        .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, null, null);

                if (list1.isEmpty()) {
                    throw new GeneralException(MessageFormat.format("Los documentos con id grupo de pago: {0} no existe", idPagoGrupo));
                }
                return list1;

            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {

                    List<XcPagosEntity> list2 = xcPagosRepository
                            .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, filters.getSucursal(), null);

                    if (list2.isEmpty()) {
                        throw new GeneralException(MessageFormat.format("No tiene acceso a los documento en la sucursal {0}", filters.getSucursal()));
                    }
                    return list2;

                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {

                List<XcPagosEntity> list3 = xcPagosRepository
                        .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, null, usuario);

                if (list3.isEmpty()) {
                    throw new GeneralException(MessageFormat.format("No tiene acceso a los documentos el usuario: {0}", usuario));
                }
                return list3;

            }
        }

        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

}
