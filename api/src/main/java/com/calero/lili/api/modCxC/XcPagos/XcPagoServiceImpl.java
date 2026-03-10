package com.calero.lili.api.modCxC.XcPagos;

import com.calero.lili.api.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.api.modCxC.XcPagos.builder.XcPagosBuilder;
import com.calero.lili.api.modCxC.XcPagos.dto.RequestPagoDto;
import com.calero.lili.api.modCxC.XcPagos.dto.ResponseXcPagosDto;
import com.calero.lili.api.modTesoreria.TsComprobanteIngreso.TsComprobanteIngresoServiceImpl;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XcPagoServiceImpl {


    private final XcPagosRepository xcPagosRepository;
    private final XcPagosBuilder xcPagosBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final XcFacturasRepository xcFacturasRepository;
    private final TsComprobanteIngresoServiceImpl tsComprobanteIngresoService;
    private final AuditorAware<String> auditorAware;

    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, RequestPagoDto request) {


        UUID idPagoGrupo = UUID.randomUUID();
        List<XcPagosEntity> pagosEntities = xcPagosRepository.saveAll(xcPagosBuilder
                .builderListEntity(request, idPagoGrupo, idData, idEmpresa));
        actualizarCamposFactura(request.getIdFactura(), pagosEntities);
        tsComprobanteIngresoService.create(idData, idEmpresa, idPagoGrupo, request.getComprobanteIngreso());
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    @Transactional
    public ResponseDto update(Long idData, UUID idPagoGrupo, Long idEmpresa, RequestPagoDto request) {

        List<XcPagosEntity> list = xcPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);

        if (!list.isEmpty()) {

            validarValoresFactura(request.getIdFactura(), list);
            xcPagosRepository.deleteAll(list);
            List<XcPagosEntity> pagosEntities = xcPagosRepository
                    .saveAll(xcPagosBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa));
            actualizarCamposFactura(request.getIdFactura(), pagosEntities);
            tsComprobanteIngresoService.update(idData,idEmpresa, idPagoGrupo, request.getComprobanteIngreso());
            return responseApiBuilder.builderResponse(idPagoGrupo.toString());
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }

    }


    public void delete(UUID idPagoGrupo) {
        List<XcPagosEntity> list = xcPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            validarValoresFactura(list.getFirst().getFactura().getIdFactura(), list);
            xcPagosRepository.deleteAll(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
    }

    public List<ResponseXcPagosDto> getFindByIdFactura(UUID idPagoGrupo) {
        List<XcPagosEntity> list = xcPagosRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            return xcPagosBuilder.builderListResponse(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
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

}
