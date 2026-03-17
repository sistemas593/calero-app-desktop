package com.calero.lili.api.modCxP.XpPagos;

import com.calero.lili.api.modAuditoria.TipoPermiso;
import com.calero.lili.api.modCxP.XpFacturas.XpFacturasEntity;
import com.calero.lili.api.modCxP.XpFacturas.XpFacturasRepository;
import com.calero.lili.api.modCxP.XpFacturas.dto.FilterXpFacturaDto;
import com.calero.lili.api.modCxP.XpPagos.builder.XpPagosBuilder;
import com.calero.lili.api.modCxP.XpPagos.dto.RequestPagoXpDto;
import com.calero.lili.api.modCxP.XpPagos.dto.ResponsePagoXpDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.TsComprobanteEgresoServiceImpl;
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
public class XpPagoServiceImpl {

    private final XpPagosRepository xpPagosRepository;
    private final XpPagosBuilder xpPagosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final XpFacturasRepository xpFacturasRepository;
    private final TsComprobanteEgresoServiceImpl tsComprobanteEgresoService;


    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, RequestPagoXpDto request, String usuario) {

        UUID idPagoGrupo = UUID.randomUUID();

        List<XpPagosEntity> entidades = xpPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
        entidades.forEach(e -> {
            e.setCreatedBy(usuario);
            e.setCreatedDate(LocalDateTime.now());
        });
        List<XpPagosEntity> pagosEntities = xpPagosRepository.saveAll(entidades);
        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteEgresoService.create(idData, idEmpresa, idPagoGrupo, request.getComprobantesEgreso(), usuario);
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    @Transactional
    public ResponseDto update(Long idData, UUID idPagoGrupo, Long idEmpresa, RequestPagoXpDto request,
                              String usuario, FilterXpFacturaDto filters, TipoPermiso tipoBusqueda) {

        List<XpPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);

        validarValoresFactura(request.getIdFactura(), list);
        xpPagosRepository.deleteAll(list);
        List<XpPagosEntity> nuevasEntidades = xpPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
        nuevasEntidades.forEach(e -> {
            e.setModifiedBy(usuario);
            e.setModifiedDate(LocalDateTime.now());
        });
        List<XpPagosEntity> pagosEntities = xpPagosRepository.saveAll(nuevasEntidades);
        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteEgresoService.update(idData, idEmpresa, idPagoGrupo, request.getComprobantesEgreso(), usuario);
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    public void delete(Long idData, Long idEmpresa, UUID idPagoGrupo, String usuario,
                       FilterXpFacturaDto filters, TipoPermiso tipoBusqueda) {
        List<XpPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);

        validarValoresFactura(list.getFirst().getFactura().getIdFactura(), list);
        list.forEach(e -> {
            e.setDeletedBy(usuario);
            e.setDeletedDate(LocalDateTime.now());
            e.setDelete(Boolean.TRUE);
            xpPagosRepository.save(e);
        });
        xpPagosRepository.deleteAll(list);
    }

    public List<ResponsePagoXpDto> getFindByIdFactura(Long idData, Long idEmpresa, UUID idPagoGrupo,
                                                       FilterXpFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        List<XpPagosEntity> list = validacionTipoBusqueda(idData, idEmpresa, idPagoGrupo, filters, tipoBusqueda, usuario);
        return xpPagosBuilder.builderListResponse(list);
    }


    private List<XpPagosEntity> validacionTipoBusqueda(Long idData, Long idEmpresa, UUID idPagoGrupo,
                                                        FilterXpFacturaDto filters, TipoPermiso tipoBusqueda, String usuario) {
        switch (tipoBusqueda) {
            case TODAS: {
                List<XpPagosEntity> list = xpPagosRepository
                        .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, null, null);
                if (list.isEmpty()) {
                    throw new GeneralException(MessageFormat.format("Los documentos con id grupo de pago: {0} no existe", idPagoGrupo));
                }
                return list;
            }
            case SUCURSAL: {
                if (Objects.nonNull(filters.getSucursal()) && !filters.getSucursal().isEmpty()) {
                    List<XpPagosEntity> list = xpPagosRepository
                            .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, filters.getSucursal(), null);
                    if (list.isEmpty()) {
                        throw new GeneralException(MessageFormat.format("No tiene acceso a los documento en la sucursal {0}", filters.getSucursal()));
                    }
                    return list;
                } else {
                    throw new GeneralException("Es requerido el parametro de la sucursal");
                }
            }
            case PROPIAS: {
                List<XpPagosEntity> list = xpPagosRepository
                        .getAllForFindByIdPagoGrupo(idData, idEmpresa, idPagoGrupo, null, usuario);
                if (list.isEmpty()) {
                    throw new GeneralException(MessageFormat.format("No tiene acceso a los documentos el usuario: {0}", usuario));
                }
                return list;
            }
        }
        throw new GeneralException(MessageFormat.format("El tipo de busqueda: {0} no existe", tipoBusqueda));
    }

    private void validarValoresFactura(UUID idFactura, List<XpPagosEntity> list) {

        XpFacturasEntity entidad = getFacturaEntidad(idFactura);

        BigDecimal totalPagado = list.stream()
                .map(XpPagosEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        entidad.setSaldo(entidad.getSaldo().add(totalPagado));
        entidad.setPagos(entidad.getPagos().subtract(totalPagado));
        entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().subtract(BigInteger.valueOf(list.size())));
        xpFacturasRepository.save(entidad);

    }


    private void actualizarCamposFactura(UUID idFactura, List<XpPagosEntity> pagosEntities) {
        XpFacturasEntity entidad = getFacturaEntidad(idFactura);

        BigDecimal totalPago = pagosEntities.stream()
                .map(XpPagosEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        validaciones(entidad, totalPago);

        entidad.setSaldo(entidad.getSaldo().subtract(totalPago));
        entidad.setPagos(totalPago);
        entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().add(BigInteger.valueOf(pagosEntities.size())));
        xpFacturasRepository.save(entidad);

    }


    private void validaciones(XpFacturasEntity entidad, BigDecimal totalPago) {
        if (entidad.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            throw new GeneralException("La factura ya se encuentra cancelada");
        }

        if (totalPago.compareTo(entidad.getSaldo()) > 0) {
            throw new GeneralException("El valor de los pagos supera al monto total a pagar");
        }

    }

    private XpFacturasEntity getFacturaEntidad(UUID idFactura) {
        return xpFacturasRepository.getForFindById(idFactura)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("No existe factura con el id: {0}", idFactura)));
    }

}
