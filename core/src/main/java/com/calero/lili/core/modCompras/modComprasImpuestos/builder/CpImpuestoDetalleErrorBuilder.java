package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.service.CpImpuestoAtsProjection;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CpImpuestoDetalleErrorBuilder {


    private final JsonUtils jsonUtils;

    public DetalleCompras builderValidacion(CreationCompraImpuestoRequestDto request) {
        return DetalleCompras.builder()
                .codSustento(request.getCodigoSustento().getCodigo())
                .tipoComprobante(request.getDocumento().getCodigo())
                .establecimiento(request.getSerie().substring(0, 3))
                .puntoEmision(request.getSerie().substring(3, 6))
                .secuencial(request.getSecuencial())
                .fechaEmision(request.getFechaEmision())
                .autorizacion(request.getNumeroAutorizacion())
                .pagoExterior(builderPagoExterior(request.getPagoExterior(), request.getPagoLocExt()))
                .fechaRegistro(request.getFechaRegistro())
                .build();
    }


    public CpImpuestoDetalleError builder(String message) {
        return CpImpuestoDetalleError.builder()
                .detalle(message)
                .build();
    }

    public List<DetalleCompras> builderListValidacion(List<CpImpuestosEntity> lista) {
        return lista
                .stream()
                .map(this::builderValidacionExcel)
                .toList();
    }

    public DetalleCompras builderValidacionExcel(CpImpuestosEntity request) {
        return DetalleCompras.builder()
                .codSustento(request.getCodigoSustento().getCodigo())
                .tipoComprobante(request.getDocumento().getCodigo())
                .establecimiento(request.getSerie().substring(0, 3))
                .puntoEmision(request.getSerie().substring(3, 6))
                .secuencial(request.getSecuencial())
                .fechaEmision(DateUtils.toString(request.getFechaEmision()))
                .autorizacion(request.getNumeroAutorizacion())
                .pagoExterior(builderPagoExterior(request.getPagoExterior(), request.getPagoLocExt()))
                .fechaRegistro(DateUtils.toString(request.getFechaRegistro()))
                .build();
    }

    public DetalleCompras builderValidacionImpuestoRetencion(CpImpuestosEntity request,
                                                             CpRetencionesEntity retencion) {
        return DetalleCompras.builder()
                .codSustento(request.getCodigoSustento().getCodigo())
                .tipoComprobante(request.getDocumento().getCodigo())
                .establecimiento(request.getSerie().substring(0, 3))
                .puntoEmision(request.getSerie().substring(3, 6))
                .secuencial(request.getSecuencial())
                .fechaEmision(DateUtils.toString(request.getFechaEmision()))
                .autorizacion(request.getNumeroAutorizacion())
                .pagoExterior(builderPagoExterior(request.getPagoExterior(), request.getPagoLocExt()))
                .fechaRegistro(DateUtils.toString(request.getFechaRegistro()))
                .autRetencion1(retencion.getNumeroAutorizacionRetencion())
                .estabRetencion1(retencion.getSerieRetencion().substring(0, 3))
                .ptoEmiRetencion1(retencion.getSerieRetencion().substring(3, 6))
                .fechaEmiRet1(DateUtils.toString(retencion.getFechaEmisionRetencion()))
                .secRetencion1(retencion.getSecuencialRetencion())
                .build();
    }


    private List<ValoresCompraImpuestoDto> builderValores(List<CpImpuestosValoresEntity> valoresEntity) {
        return valoresEntity.stream()
                .map(this::builderValor)
                .toList();
    }

    private ValoresCompraImpuestoDto builderValor(CpImpuestosValoresEntity model) {
        return ValoresCompraImpuestoDto.builder()
                .codigo(model.getCodigo())
                .valor(model.getValor())
                .baseImponible(model.getBaseImponible())
                .tarifa(model.getTarifa())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .build();
    }

    private PagoExterior builderPagoExterior(com.calero.lili.core.modCompras.modComprasImpuestos.dto.PagoExterior model, String pagoLocExt) {
        if (Objects.isNull(model)) return null;
        return PagoExterior.builder()
                .pagoLocExt(pagoLocExt)
                .paisEfecPago(model.getPaisEfecPago())
                .aplicConvDobTrib(model.getAplicConvDobTrib())
                .pagExtSujRetNorLeg(model.getPagExtSujRetNorLeg())
                .tipoRegi(model.getTipoRegi())
                .paisEfecPagoGen(model.getPaisEfecPagoGen())
                .paisEfecPagoParFis(model.getPaisEfecPagoParFis())
                .denopagoRegFis(model.getDenopagoRegFis())
                .pagoRegFis(model.getPagoRegFis())
                .build();
    }
}
