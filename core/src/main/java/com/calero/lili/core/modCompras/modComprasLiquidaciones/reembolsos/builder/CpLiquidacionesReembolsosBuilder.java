package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.builder;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresLiquidacionesCompraDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoTotalizadoDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                .existeComprobante(Boolean.FALSE)
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
                .existeComprobante(Boolean.FALSE)
                .build();
    }

    private List<CpLiquidacionesReembolsosValoresEntity> builderListValores(List<ValoresLiquidacionesCompraDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(item -> builderValores(item, idData, idEmpresa))
                .toList();
    }


    private CpLiquidacionesReembolsosValoresEntity builderValores(ValoresLiquidacionesCompraDto model, Long idData, Long idEmpresa) {
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
                .idLiquidacion(model.getIdLiquidacion())
                .total(setearTotal(model.getReembolsosValores()))
                .existeComprobante(model.getExisteComprobante())
                .reembolsosValores(builderListValoresResponse(model.getReembolsosValores()))
                .build();
    }


    public GetReembolsoDto builderPaginadoResponse(CpLiquidacionesReembolsosEntity model) {
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
                .idLiquidacion(model.getIdLiquidacion())
                .total(setearTotal(model.getReembolsosValores()))
                .existeComprobante(model.getExisteComprobante())
                .reembolsosValores(new ArrayList<>())
                .build();
    }


    public GetReembolsoTotalizadoDto builderResponseTotalizado(CpLiquidacionesReembolsosEntity model) {
        return GetReembolsoTotalizadoDto.builder()
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
                .idLiquidacion(model.getIdLiquidacion())
                .reembolsosValores(builderListValoresResponse(model.getReembolsosValores()))
                .existeComprobante(model.getExisteComprobante())
                .build();
    }


    private List<ValoresLiquidacionesCompraDto> builderListValoresResponse(List<CpLiquidacionesReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresResponse)
                .toList();
    }

    private ValoresLiquidacionesCompraDto builderValoresResponse(CpLiquidacionesReembolsosValoresEntity model) {
        return ValoresLiquidacionesCompraDto.builder()
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

    private BigDecimal setearTotal(List<CpLiquidacionesReembolsosValoresEntity> reembolsosValores) {

        BigDecimal totalBase = reembolsosValores.stream()
                .map(CpLiquidacionesReembolsosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalValor = reembolsosValores.stream()
                .map(CpLiquidacionesReembolsosValoresEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);


        return totalBase.add(totalValor);
    }


}
