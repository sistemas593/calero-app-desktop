package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modImpuestosAnexos.ats.DetalleCompras;
import com.calero.lili.core.modImpuestosAnexos.ats.PagoExterior;
import com.calero.lili.core.modImpuestosAnexos.ats.Reembolso;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class CpImpuestoDetalleErrorBuilder {


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
                .reembolso(builderListReembolsos(request.getReembolsos()))
                .build();
    }

    private List<Reembolso> builderListReembolsos(List<com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso> reembolsos) {
        return reembolsos.stream()
                .map(this::builderReembolso)
                .toList();
    }

    private Reembolso builderReembolso(com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso model) {
        Reembolso reembolso = Reembolso.builder()
                .tipoComprobanteReemb(model.getCodigoDocumentoReemb())
                .tpIdProvReemb(model.getTipoProveedorReemb())
                .idProvReemb(model.getNumeroIdentificacionReemb())
                .establecimientoReemb(model.getSerieReemb().substring(0, 3))
                .puntoEmisionReemb(model.getSerieReemb().substring(3, 6))
                .fechaEmisionReemb(Objects.nonNull(model.getFechaEmisionReemb()) ? model.getFechaEmisionReemb() : null)
                .autorizacionReemb(model.getNumeroAutorizacionReemb())
                .build();
        setearValores(reembolso, model);
        return reembolso;
    }

    private void setearValores(Reembolso reembolso, com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso model) {

        List<String> codigoIva = Arrays.asList("4", "5", "8");

        BigDecimal baseImponibleNoGravada = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        item.getCodigoPorcentaje().equals("0"))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseImponibleGravada = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        codigoIva.contains(item.getCodigoPorcentaje()))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseImponibleExcenta = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        item.getCodigoPorcentaje().equals("7"))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal montoIva = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        codigoIva.contains(item.getCodigoPorcentaje()))
                .map(ValoresCompraImpuestoDto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalBaseImponible = baseImponibleExcenta.add(baseImponibleGravada).add(baseImponibleNoGravada);

        reembolso.setBaseImponibleReemb(totalBaseImponible.toString());
        reembolso.setBaseImpExeReemb(baseImponibleExcenta.toString());
        reembolso.setBaseNoGraIvaReemb(baseImponibleNoGravada.toString());
        reembolso.setBaseImpGravReemb(baseImponibleGravada.toString());
        reembolso.setMontoIvaRemb(montoIva.toString());

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
