package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.builder;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisGetOneDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CpLiquidacionesReembolsosBuilder {

    public List<CpLiquidacionesReembolsosEntity> builderList(List<ReembolsoRequestDto> list) {

        if (Objects.isNull(list)) return new ArrayList<>();
        return list.stream()
                .map(this::builderReembolso)
                .toList();
    }

    public CpLiquidacionesReembolsosEntity builderReembolso(ReembolsoRequestDto model) {

        UUID idReembolso = UUID.randomUUID();
        return CpLiquidacionesReembolsosEntity.builder()
                .idLiquidacionReembolsos(idReembolso)
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListValores(model.getReembolsosValores(), idReembolso))
                .build();
    }

    public CpLiquidacionesReembolsosEntity builderUpdateReembolso(ReembolsoRequestDto model, CpLiquidacionesReembolsosEntity item) {
        return CpLiquidacionesReembolsosEntity.builder()
                .idLiquidacionReembolsos(item.getIdLiquidacionReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListValores(model.getReembolsosValores(), item.getIdLiquidacionReembolsos()))
                .build();
    }

    private List<CpLiquidacionesReembolsosValoresEntity> builderListValores(List<ValoresDto> list, UUID idReembolso) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }


    private CpLiquidacionesReembolsosValoresEntity builderValores(ValoresDto model) {
        return CpLiquidacionesReembolsosValoresEntity.builder()
                .idLiquidacionValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    public List<GetReembolsoDto> builderListResponse(List<CpLiquidacionesReembolsosEntity> model) {
        if (model.isEmpty()) return new ArrayList<>();
        return model.stream()
                .map(this::builderResponse)
                .toList();
    }

    public GetReembolsoDto builderResponse(CpLiquidacionesReembolsosEntity model) {
        return GetReembolsoDto.builder()
                .idLiquidacionReembolsos(model.getIdLiquidacionReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .pais(builderPaisResponse(model.getPais()))
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toString(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListValoresResponse(model.getReembolsosValores()))
                .idLiquidacion(model.getIdLiquidacion())
                .build();
    }

    private List<ValoresDto> builderListValoresResponse(List<CpLiquidacionesReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresResponse)
                .toList();
    }

    private ValoresDto builderValoresResponse(CpLiquidacionesReembolsosValoresEntity model) {
        return ValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }

    private TbPaisEntity builderPais(String codPaisPagoReemb) {
        return TbPaisEntity.builder()
                .codigoPais(codPaisPagoReemb)
                .build();
    }

    private TbPaisGetOneDto builderPaisResponse(TbPaisEntity model) {
        return TbPaisGetOneDto.builder()
                .pais(model.getPais())
                .codigoPais(model.getCodigoPais())
                .build();
    }


}
