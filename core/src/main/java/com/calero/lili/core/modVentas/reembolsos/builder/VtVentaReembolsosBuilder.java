package com.calero.lili.core.modVentas.reembolsos.builder;

import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import com.calero.lili.core.modVentas.reembolsos.dto.CreationRequestReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.ResponseReembolsoDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtVentaReembolsosBuilder {


    public VtVentaReembolsosEntity builderEntity(CreationRequestReembolsoDto model) {
        return VtVentaReembolsosEntity.builder()
                .idVentaReembolsos(UUID.randomUUID())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListReembolsoValores(model.getReembolsosValores()))
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .build();
    }

    public VtVentaReembolsosEntity builderUpdateEntity(CreationRequestReembolsoDto model,
                                                       VtVentaReembolsosEntity item) {
        return VtVentaReembolsosEntity.builder()
                .idVentaReembolsos(item.getIdVentaReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListReembolsoValores(model.getReembolsosValores()))
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .build();
    }

    private TbPaisEntity builderPais(String codigoPais) {
        return TbPaisEntity.builder()
                .codigoPais(codigoPais)
                .build();
    }

    private List<VtVentaReembolsosValoresEntity> builderListReembolsoValores(List<CreationRequestReembolsoDto.ValoresDto> list) {
        return list.stream()
                .map(this::builderReembolsoValores)
                .toList();
    }

    private VtVentaReembolsosValoresEntity builderReembolsoValores(CreationRequestReembolsoDto.ValoresDto model) {
        return VtVentaReembolsosValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .valor(model.getValor())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .tarifa(model.getTarifa())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .build();
    }


    public ResponseReembolsoDto builderReembolso(VtVentaReembolsosEntity model) {
        return ResponseReembolsoDto.builder()
                .idVentaReembolsos(model.getIdVentaReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toString(model.getFechaEmisionReemb()))
                .reembolsosValores(builderResponseValoresList(model.getReembolsosValores()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .pais(builderPaisResponse(model.getPais()))
                .idVenta(model.getIdVenta())
                .build();
    }

    private ResponseReembolsoDto.PaisDto builderPaisResponse(TbPaisEntity pais) {
        return ResponseReembolsoDto.PaisDto.builder()
                .codigoPais(pais.getCodigoPais())
                .pais(pais.getPais())
                .build();
    }

    private List<ResponseReembolsoDto.ValoresDto> builderResponseValoresList(List<VtVentaReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresResponse)
                .toList();
    }

    private ResponseReembolsoDto.ValoresDto builderValoresResponse(VtVentaReembolsosValoresEntity model) {
        return ResponseReembolsoDto.ValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }
}
