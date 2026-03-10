package com.calero.lili.api.modVentas.facturas.builder;

import com.calero.lili.api.modVentas.facturas.dto.GetFacturaDto;
import com.calero.lili.api.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.api.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VtReembolsosResponseBuilder {

    public List<GetFacturaDto.Reembolso> builderListDto(List<VtVentaReembolsosEntity> list) {
        return list.
                stream()
                .map(this::builderReembolsoDto)
                .toList();
    }

    private GetFacturaDto.Reembolso builderReembolsoDto(VtVentaReembolsosEntity model) {
        return GetFacturaDto.Reembolso.builder()
                .idReembolso(model.getIdVentaReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(model.getFechaEmisionReemb().toString())
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderResponseValoresList(model.getReembolsosValores()))
                .build();
    }

    private List<GetFacturaDto.Valores> builderResponseValoresList(List<VtVentaReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresResponse)
                .toList();
    }

    private GetFacturaDto.Valores builderValoresResponse(VtVentaReembolsosValoresEntity model) {
        return GetFacturaDto.Valores.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }
}
