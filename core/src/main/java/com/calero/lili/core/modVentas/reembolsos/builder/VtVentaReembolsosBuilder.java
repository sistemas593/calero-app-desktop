package com.calero.lili.core.modVentas.reembolsos.builder;

import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import com.calero.lili.core.modVentas.reembolsos.dto.CreationRequestReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.ReembolsoValoresDto;
import com.calero.lili.core.modVentas.reembolsos.dto.ResponseVentaReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.ResponseVentaReembolsoTotalizadoDto;
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
public class VtVentaReembolsosBuilder {


    public VtVentaReembolsosEntity builderEntity(Long idData, Long idEmpresa, CreationRequestReembolsoDto model) {
        return VtVentaReembolsosEntity.builder()
                .idVentaReembolsos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListReembolsoValores(model.getReembolsosValores(), idData, idEmpresa))
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .existeComprobante(Boolean.FALSE)
                .build();
    }

    public VtVentaReembolsosEntity builderUpdateEntity(CreationRequestReembolsoDto model,
                                                       VtVentaReembolsosEntity item) {
        return VtVentaReembolsosEntity.builder()
                .idVentaReembolsos(item.getIdVentaReembolsos())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toLocalDate(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .reembolsosValores(builderListReembolsoValores(model.getReembolsosValores(), item.getIdData(), item.getIdEmpresa()))
                .pais(builderPais(model.getCodPaisPagoReemb()))
                .existeComprobante(Boolean.FALSE)
                .build();
    }

    private TbPaisEntity builderPais(String codigoPais) {
        return TbPaisEntity.builder()
                .codigoPais(codigoPais)
                .build();
    }

    private List<VtVentaReembolsosValoresEntity> builderListReembolsoValores(List<CreationRequestReembolsoDto.ValoresVentaReembolsoDto> list, Long iData, Long idEmpresa) {
        return list.stream()
                .map(item -> builderReembolsoValores(item, iData, idEmpresa))
                .toList();
    }

    private VtVentaReembolsosValoresEntity builderReembolsoValores(CreationRequestReembolsoDto.ValoresVentaReembolsoDto model, Long idData, Long idEmpresa) {
        return VtVentaReembolsosValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valor(model.getValor())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .tarifa(model.getTarifa())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .build();
    }


    public ResponseVentaReembolsoDto builderReembolso(VtVentaReembolsosEntity model) {
        return ResponseVentaReembolsoDto.builder()
                .idVentaReembolsos(model.getIdVentaReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toString(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .fechaAutorizacionReemb(DateUtils.toLocalDateTimeString(model.getFechaAutorizacionReemb()))
                .pais(Objects.nonNull(model.getPais()) ? model.getPais().getPais() : "")
                .codigoPais(Objects.nonNull(model.getPais()) ? model.getPais().getCodigoPais() : "")
                .idVenta(model.getIdVenta())
                .total(seterValores(model.getReembolsosValores()))
                .existeComprobante(model.getExisteComprobante())
                .reembolsosValores(builderResponseValoresList(model.getReembolsosValores()))
                .build();
    }

    public ResponseVentaReembolsoDto builderPaginadoReembolso(VtVentaReembolsosEntity model) {
        return ResponseVentaReembolsoDto.builder()
                .idVentaReembolsos(model.getIdVentaReembolsos())
                .tipoIdentificacionReemb(model.getTipoIdentificacionReemb())
                .numeroIdentificacionReemb(model.getNumeroIdentificacionReemb())
                .codigoDocumentoReemb(model.getCodigoDocumentoReemb())
                .tipoProveedorReemb(model.getTipoProveedorReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .serieReemb(model.getSerieReemb())
                .secuencialReemb(model.getSecuencialReemb())
                .fechaEmisionReemb(DateUtils.toString(model.getFechaEmisionReemb()))
                .numeroAutorizacionReemb(model.getNumeroAutorizacionReemb())
                .fechaAutorizacionReemb(DateUtils.toLocalDateTimeString(model.getFechaAutorizacionReemb()))
                .pais(Objects.nonNull(model.getPais()) ? model.getPais().getPais() : "")
                .codigoPais(Objects.nonNull(model.getPais()) ? model.getPais().getCodigoPais() : "")
                .idVenta(model.getIdVenta())
                .total(seterValores(model.getReembolsosValores()))
                .existeComprobante(model.getExisteComprobante())
                .reembolsosValores(new ArrayList<>())
                .build();
    }


    public ResponseVentaReembolsoTotalizadoDto builderReembolsoTotalizado(VtVentaReembolsosEntity model) {
        return ResponseVentaReembolsoTotalizadoDto.builder()
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
                .fechaAutorizacionReemb(DateUtils.toLocalDateTimeString(model.getFechaAutorizacionReemb()))
                .pais(Objects.nonNull(model.getPais()) ? model.getPais().getPais() : "")
                .codigoPais(Objects.nonNull(model.getPais()) ? model.getPais().getCodigoPais() : "")
                .idVenta(model.getIdVenta())
                .existeComprobante(model.getExisteComprobante())
                .build();
    }


    private List<ReembolsoValoresDto> builderResponseValoresList(List<VtVentaReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresResponse)
                .toList();
    }

    private ReembolsoValoresDto builderValoresResponse(VtVentaReembolsosValoresEntity model) {
        return ReembolsoValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .build();
    }


    private BigDecimal seterValores(List<VtVentaReembolsosValoresEntity> reembolsosValores) {

        BigDecimal totalBase = reembolsosValores.stream()
                .map(VtVentaReembolsosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalValor = reembolsosValores.stream()
                .map(VtVentaReembolsosValoresEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);


        return totalBase.add(totalValor);
    }

}
