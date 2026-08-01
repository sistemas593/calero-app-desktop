package com.calero.lili.core.modVentas.builder;

import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.dto.GetReporteVentasListDto;
import com.calero.lili.core.modVentas.dto.GetVentasListDto;
import com.calero.lili.core.modVentas.dto.ResponseVentasValoresDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
public class GetListResponseBuilder {

    public GetVentasListDto builderListResponse(VtVentaEntity model) {
        return GetVentasListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(Objects.nonNull(model.getFechaEmision()) ? DateUtils.toString(model.getFechaEmision()) : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .formatoDocumento(model.getFormatoDocumento())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(Objects.nonNull(model.getEmailEstado()) ? model.getEmailEstado().toString() : null)
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .total(model.getTotal())
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .existeComprobante(model.getExisteComprobante())
                .origen(model.getOrigen())
                .placa(model.getPlaca())
                .build();
    }


    public GetReporteVentasListDto builderReporteListResponse(VtVentaEntity model) {
        return GetReporteVentasListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(Objects.nonNull(model.getFechaEmision()) ? DateUtils.toString(model.getFechaEmision()) : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .formatoDocumento(model.getFormatoDocumento())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(Objects.nonNull(model.getEmailEstado()) ? model.getEmailEstado().toString() : null)
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .totalImpuesto(model.getTotalImpuesto())
                .valores(builderListValoresDto(model.getValoresEntity()))
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .existeComprobante(model.getExisteComprobante())
                .origen(model.getOrigen())
                .placa(model.getPlaca())
                .build();
    }


    public List<ResponseVentasValoresDto> builderListValoresDto(List<VtVentaValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresDto)
                .toList();
    }

    private ResponseVentasValoresDto builderValoresDto(VtVentaValoresEntity model) {
        return ResponseVentasValoresDto.builder()
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .build();
    }


    public GetVentasListDto builderAnuladoResponse(VtVentaEntity model) {
        return GetVentasListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(Objects.nonNull(model.getFechaEmision()) ? DateUtils.toString(model.getFechaEmision()) : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .formatoDocumento(model.getFormatoDocumento())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .total(new BigDecimal("0.00"))
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .existeComprobante(model.getExisteComprobante())
                .origen(model.getOrigen())
                .placa(model.getPlaca())
                .build();
    }


    public GetReporteVentasListDto builderReporteAnuladoResponse(VtVentaEntity model) {
        return GetReporteVentasListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(Objects.nonNull(model.getFechaEmision()) ? DateUtils.toString(model.getFechaEmision()) : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .formatoDocumento(model.getFormatoDocumento())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(new BigDecimal("0.00"))
                .totalDescuento(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .totalImpuesto(new BigDecimal("0.00"))
                .valores(builderAnuladoListValoresDto(model.getValoresEntity()))
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .existeComprobante(model.getExisteComprobante())
                .origen(model.getOrigen())
                .placa(model.getPlaca())
                .build();
    }

    public List<ResponseVentasValoresDto> builderAnuladoListValoresDto(List<VtVentaValoresEntity> list) {
        return list.stream()
                .map(this::builderAnuladoValoresDto)
                .toList();
    }

    private ResponseVentasValoresDto builderAnuladoValoresDto(VtVentaValoresEntity model) {
        return ResponseVentasValoresDto.builder()
                .valor(new BigDecimal("0.00"))
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal("0.00"))
                .codigo(model.getCodigo())
                .build();
    }


}
