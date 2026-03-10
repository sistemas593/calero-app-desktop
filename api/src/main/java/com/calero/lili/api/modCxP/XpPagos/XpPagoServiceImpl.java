package com.calero.lili.api.modCxP.XpPagos;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modCxP.XpFacturas.XpFacturasEntity;
import com.calero.lili.api.modCxP.XpFacturas.XpFacturasRepository;
import com.calero.lili.api.modCxP.XpPagos.builder.XpPagosBuilder;
import com.calero.lili.api.modCxP.XpPagos.dto.RequestPagoXpDto;
import com.calero.lili.api.modCxP.XpPagos.dto.ResponsePagoXpDto;
import com.calero.lili.api.modTesoreria.TsComprabanteEgreso.TsComprobanteEgresoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;
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
    public ResponseDto create(Long idData, Long idEmpresa, RequestPagoXpDto request) {

        UUID idPagoGrupo = UUID.randomUUID();

        List<XpPagosEntity> pagosEntities = xpPagosRepository.saveAll(xpPagosBuilder
                .builderListEntity(request, idPagoGrupo, idData, idEmpresa));

        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteEgresoService.create(idData, idEmpresa, idPagoGrupo, request.getComprobantesEgreso());
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    @Transactional
    public ResponseDto update(Long idData, UUID idPagoGrupo, Long idEmpresa, RequestPagoXpDto request) {


        List<XpPagosEntity> list = xpPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);

        if (!list.isEmpty()) {

            validarValoresFactura(request.getIdFactura(), list);
            xpPagosRepository.deleteAll(list);
            List<XpPagosEntity> pagosEntities = xpPagosRepository
                    .saveAll(xpPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa));
            actualizarCamposFactura(request.getIdFactura(), pagosEntities);
            tsComprobanteEgresoService.update(idData, idEmpresa, idPagoGrupo, request.getComprobantesEgreso());
            return responseApiBuilder.builderResponse(idPagoGrupo.toString());
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }

    }


    public void delete(UUID idPagoGrupo) {
        List<XpPagosEntity> list = xpPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            validarValoresFactura(list.getFirst().getFactura().getIdFactura(), list);
            xpPagosRepository.deleteAll(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
    }

    public List<ResponsePagoXpDto> getFindByIdFactura(UUID idPagoGrupo) {
        List<XpPagosEntity> list = xpPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            return xpPagosBuilder.builderListResponse(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
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
