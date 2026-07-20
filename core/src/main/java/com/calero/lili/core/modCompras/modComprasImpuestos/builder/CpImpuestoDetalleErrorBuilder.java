package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CpImpuestoDetalleErrorBuilder {


    public CompraImpuestoDto builderValidacion(CreationCompraImpuestoRequestDto request) {
        return CompraImpuestoDto.builder()
                .serie(request.getSerie())
                .secuencial(request.getSecuencial())
                .numeroAutorizacion(request.getNumeroAutorizacion())
                .codigoSustento(request.getCodigoSustento().name())
                .codigoDocumento(request.getDocumento().getCodigo())
                .pagoExterior(request.getPagoExterior())
                .pagoLocExt(request.getPagoLocExt())
                .fechaEmision(request.getFechaEmision())
                .reembolsos(request.getReembolsos())
                .valores(request.getValores())
                .fechaRegistro(request.getFechaRegistro())
                .build();
    }

    public CpImpuestoDetalleError builder(String message) {
        return CpImpuestoDetalleError.builder()
                .detalle(message)
                .build();
    }

    public List<CompraImpuestoDto> builderListValidacion(List<CpImpuestosEntity> lista) {
        return lista
                .stream()
                .map(this::builderValidacionExcel)
                .toList();
    }

    public CompraImpuestoDto builderValidacionExcel(CpImpuestosEntity request) {
        return CompraImpuestoDto.builder()
                .serie(request.getSerie())
                .secuencial(request.getSecuencial())
                .numeroAutorizacion(request.getNumeroAutorizacion())
                .codigoSustento(request.getCodigoSustento().name())
                .codigoDocumento(request.getDocumento().getCodigo())
                .pagoExterior(request.getPagoExterior())
                .pagoLocExt(request.getPagoLocExt())
                .fechaEmision(DateUtils.toString(request.getFechaEmision()))
                .reembolsos(null)
                .valores(builderValores(request.getValoresEntity()))
                .build();
    }

    public CompraImpuestoDto builderValidacionImpuestoRetencion(CpImpuestosEntity request,
                                                                CpRetencionesEntity retencion,
                                                                List<CompraImpuestosDto> codigoImpuestos) {
        return CompraImpuestoDto.builder()
                .serie(request.getSerie())
                .secuencial(request.getSecuencial())
                .numeroAutorizacion(request.getNumeroAutorizacion())
                .codigoSustento(request.getCodigoSustento().name())
                .codigoDocumento(request.getDocumento().getCodigo())
                .pagoExterior(request.getPagoExterior())
                .pagoLocExt(request.getPagoLocExt())
                .fechaEmision(DateUtils.toString(request.getFechaEmision()))
                .valores(builderValores(request.getValoresEntity()))
                .numeroAutorizacionRetencion(retencion.getNumeroAutorizacionRetencion())
                .serieRetencion(retencion.getSerieRetencion())
                .secuencialRetencion(retencion.getSecuencialRetencion())
                .fechaEmisionRetencion(DateUtils.toString(retencion.getFechaEmisionRetencion()))
                .fechaRegistro(DateUtils.toString(request.getFechaRegistro()))
                .compraImpuestos(codigoImpuestos)
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
}
