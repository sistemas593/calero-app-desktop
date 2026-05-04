package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.builder;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CpLiquidacionesReembolsosBuilder {

    public CpLiquidacionesReembolsosEntity builderReembolso(ReembolsoRequestDto model, Long idData, Long idEmpresa) {

        UUID idReembolso = UUID.randomUUID();
        return CpLiquidacionesReembolsosEntity.builder()
                .idLiquidacionReembolsos(idReembolso)
                .idEmpresa(idEmpresa)
                .idData(idData)
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListValores(model.getReembolsosValores(), idData, idEmpresa))
                .build();
    }

    public CpLiquidacionesReembolsosEntity builderUpdateReembolso(ReembolsoRequestDto model, CpLiquidacionesReembolsosEntity item) {
        return CpLiquidacionesReembolsosEntity.builder()
                .idLiquidacionReembolsos(item.getIdLiquidacionReembolsos())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListValores(model.getReembolsosValores(), item.getIdData(), item.getIdEmpresa()))
                .build();
    }

    private List<CpLiquidacionesReembolsosValoresEntity> builderListValores(List<ValoresDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(item -> builderValores(item, idData, idEmpresa))
                .toList();
    }


    private CpLiquidacionesReembolsosValoresEntity builderValores(ValoresDto model, Long idData, Long idEmpresa) {
        return CpLiquidacionesReembolsosValoresEntity.builder()
                .idLiquidacionValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
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
                .pais(Objects.nonNull(model.getPais()) ? model.getPais().getPais() : "")
                .codigoPais(Objects.nonNull(model.getPais()) ? model.getPais().getCodigoPais() : "")
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toString(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .fechaAutorizacionReemb(DateUtils.toLocalDateTimeString(model.getFechaAutorizacionReemb()))
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
}
