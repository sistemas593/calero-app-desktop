package com.calero.lili.core.modVentas.builder;

import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GetListResponseBuilder {

    public GetListDto builderListResponse(VtVentaEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(model.getFechaEmision().toString())
                .idTercero(model.getTercero().getIdTercero())
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .formatoDocumento(model.getFormatoDocumento())
                .estadoDocumento(model.getEstadoDocumento())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .valores(builderListValoresDto(model.getValoresEntity()))
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .numeroIdentificacion(model.getTercero().getNumeroIdentificacion())
                .terceroNombre(model.getTercero().getTercero())
                .build();
    }


    public List<ResponseValoresDto> builderListValoresDto(List<VtVentaValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresDto)
                .toList();
    }

    private ResponseValoresDto builderValoresDto(VtVentaValoresEntity model) {
        return ResponseValoresDto.builder()
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .build();
    }


    public GetListDto builderAnuladoListResponse(VtVentaEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idVenta(model.getIdVenta())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso())
                .codigoDocumento(model.getCodigoDocumento())
                .fechaEmision(model.getFechaEmision().toString())
                .idTercero(model.getTercero().getIdTercero())
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
                .valores(builderAnuladoListValoresDto(model.getValoresEntity()))
                .ambiente(model.getAmbiente())
                .email(model.getEmail())
                .numeroIdentificacion(model.getTercero().getNumeroIdentificacion())
                .terceroNombre(model.getTercero().getTercero())
                .build();
    }


    public List<ResponseValoresDto> builderAnuladoListValoresDto(List<VtVentaValoresEntity> list) {
        return list.stream()
                .map(this::builderAnuladoValoresDto)
                .toList();
    }

    private ResponseValoresDto builderAnuladoValoresDto(VtVentaValoresEntity model) {
        return ResponseValoresDto.builder()
                .valor(new BigDecimal("0.00"))
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal("0.00"))
                .codigo(model.getCodigo())
                .build();
    }


}
