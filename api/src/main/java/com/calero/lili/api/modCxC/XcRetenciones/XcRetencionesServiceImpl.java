package com.calero.lili.api.modCxC.XcRetenciones;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasEntity;
import com.calero.lili.api.modCxC.XcFacturas.XcFacturasRepository;
import com.calero.lili.api.modCxC.XcPagos.XcPagosEntity;
import com.calero.lili.api.modCxC.XcRetenciones.builder.XcRetencionesBuilder;
import com.calero.lili.api.modCxC.XcRetenciones.dto.RequestRetencionesDto;
import com.calero.lili.api.modCxC.XcRetenciones.dto.ResponseXcRetencionesDto;
import com.calero.lili.api.modVentasRetenciones.VtRetencionesValoresEntity;
import com.calero.lili.api.modVentasRetenciones.VtRetencionesValoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XcRetencionesServiceImpl {

    private final XcRetencionesRepository xcRetencionesRepository;
    private final XcRetencionesBuilder xcRetencionesBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final XcFacturasRepository xcFacturasRepository;
    private final VtRetencionesValoresRepository vtRetencionesValoresRepository;


    @Transactional
    public ResponseDto create(Long idData, Long idEmpresa, RequestRetencionesDto request, String usuario) {


        UUID idPagoGrupo = UUID.randomUUID();
        List<XcPagosEntity> entidades = xcRetencionesBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
        entidades.forEach(e -> {
            e.setCreatedBy(usuario);
            e.setCreatedDate(LocalDateTime.now());
        });
        xcRetencionesRepository.saveAll(entidades);
        actualizarCamposFactura(request);
        return responseApiBuilder.builderResponse(idPagoGrupo.toString());
    }


    @Transactional
    public ResponseDto update(Long idData, UUID idPagoGrupo, Long idEmpresa, RequestRetencionesDto request, String usuario) {


        List<XcPagosEntity> list = xcRetencionesRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);

        if (!list.isEmpty()) {

            validarValoresFactura(request);
            xcRetencionesRepository.deleteAll(list);
            List<XcPagosEntity> nuevasEntidades = xcRetencionesBuilder.builderListEntity(request, idPagoGrupo, idData, idEmpresa);
            nuevasEntidades.forEach(e -> {
                e.setModifiedBy(usuario);
                e.setModifiedDate(LocalDateTime.now());
            });
            xcRetencionesRepository.saveAll(nuevasEntidades);
            actualizarCamposFactura(request);
            return responseApiBuilder.builderResponse(idPagoGrupo.toString());
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }

    }


    public void delete(UUID idPagoGrupo, String usuario) {
        List<XcPagosEntity> list = xcRetencionesRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            validarValoresEliminar(list.getFirst().getFactura().getIdFactura());
            list.forEach(e -> {
                e.setDeletedBy(usuario);
                e.setDeletedDate(LocalDateTime.now());
                e.setDelete(Boolean.TRUE);
                xcRetencionesRepository.save(e);
            });
            xcRetencionesRepository.deleteAll(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
    }


    public List<ResponseXcRetencionesDto> getFindByIdFactura(UUID idPagoGrupo) {
        List<XcPagosEntity> list = xcRetencionesRepository.getAllForFindByIdPagoGrupo(idPagoGrupo);
        if (!list.isEmpty()) {
            return xcRetencionesBuilder.builderListResponse(list);
        } else {
            throw new GeneralException(MessageFormat.format("No existe lista de pagos del id grupo:  {0}",
                    idPagoGrupo));
        }
    }


    private void validarValoresFactura(RequestRetencionesDto model) {

        XcFacturasEntity entidad = getFacturaEntidad(model.getIdFactura());
        List<VtRetencionesValoresEntity> valores = vtRetencionesValoresRepository
                .findAllIds(model.getRetencionesValores());

        if (!valores.isEmpty()) {
            BigDecimal totalPagado = valores.stream()
                    .map(VtRetencionesValoresEntity::getValorRetenido)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (VtRetencionesValoresEntity retencion : valores) {
                if (retencion.getCodigo().equals("1")) {
                    entidad.setRetencionesRenta(entidad.getRetencionesRenta().subtract(retencion.getValorRetenido()));
                } else {
                    entidad.setRetencionesIva(entidad.getRetencionesIva().subtract(retencion.getValorRetenido()));
                }
            }

            entidad.setSaldo(entidad.getSaldo().add(totalPagado));
            entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().subtract(BigInteger.valueOf(valores.size())));
            xcFacturasRepository.save(entidad);
            actualizarRetencionesSinEntidad(valores);
        }


    }

    private void actualizarRetencionesSinEntidad(List<VtRetencionesValoresEntity> valores) {
        for (VtRetencionesValoresEntity retencionesValores : valores) {
            retencionesValores.setFactura(null);
            vtRetencionesValoresRepository.save(retencionesValores);
        }
    }


    private void actualizarCamposFactura(RequestRetencionesDto model) {

        XcFacturasEntity entidad = getFacturaEntidad(model.getIdFactura());
        List<VtRetencionesValoresEntity> valores = vtRetencionesValoresRepository.findAllIds(model.getRetencionesValores());

        if (!valores.isEmpty()) {
            BigDecimal totalPago = valores.stream()
                    .map(VtRetencionesValoresEntity::getValorRetenido)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            validaciones(entidad, totalPago);

            for (VtRetencionesValoresEntity retencion : valores) {
                if (retencion.getCodigo().equals("1")) {
                    entidad.setRetencionesRenta(entidad.getRetencionesRenta().add(retencion.getValorRetenido()));
                } else {
                    entidad.setRetencionesIva(entidad.getRetencionesIva().add(retencion.getValorRetenido()));
                }
            }

            entidad.setSaldo(entidad.getSaldo().subtract(totalPago));
            entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().add(BigInteger.valueOf(valores.size())));
            xcFacturasRepository.save(entidad);
            actualizarRetencionesConEntidad(valores, entidad);
        } else {
            throw new GeneralException("Los identificadores de valores de retenciones no corresponden a ningún registro");
        }

    }

    private void actualizarRetencionesConEntidad(List<VtRetencionesValoresEntity> valores, XcFacturasEntity entidad) {
        for (VtRetencionesValoresEntity retencionesValores : valores) {
            retencionesValores.setFactura(entidad);
            vtRetencionesValoresRepository.save(retencionesValores);
        }
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


    private void validarValoresEliminar(UUID idFactura) {
        XcFacturasEntity entidad = getFacturaEntidad(idFactura);
        List<VtRetencionesValoresEntity> valores = vtRetencionesValoresRepository.findAllForIdFactura(idFactura);
        if (!valores.isEmpty()) {
            BigDecimal totalPagado = valores.stream()
                    .map(VtRetencionesValoresEntity::getValorRetenido)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (VtRetencionesValoresEntity retencion : valores) {
                if (retencion.getCodigo().equals("1")) {
                    entidad.setRetencionesRenta(entidad.getRetencionesRenta().subtract(retencion.getValorRetenido()));
                } else {
                    entidad.setRetencionesIva(entidad.getRetencionesIva().subtract(retencion.getValorRetenido()));
                }
            }

            entidad.setSaldo(entidad.getSaldo().add(totalPagado));
            entidad.setRegistrosAplicados(entidad.getRegistrosAplicados().subtract(BigInteger.valueOf(valores.size())));
            xcFacturasRepository.save(entidad);
            actualizarRetencionesSinEntidad(valores);
        }
    }

}
