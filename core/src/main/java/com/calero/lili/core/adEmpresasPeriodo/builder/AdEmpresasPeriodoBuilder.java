package com.calero.lili.core.adEmpresasPeriodo.builder;

import com.calero.lili.core.adEmpresasPeriodo.AdEmpresaPeriodoEntity;
import com.calero.lili.core.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationRequestDto;
import com.calero.lili.core.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationResponseDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdEmpresasPeriodoBuilder {

    public AdEmpresaPeriodoEntity builderEntity(AdEmpresaPeriodoCreationRequestDto model, Long idData, Long idEmpresa) {
        return AdEmpresaPeriodoEntity.builder()
                .idPeriodo(UUID.randomUUID())
                .idEmpresa(idEmpresa)
                .idData(idData)
                .ano(model.getAno())
                .cPeriodo(model.getCPeriodo())
                .periodo(model.getPeriodo())
                .fechaDesde(DateUtils.toLocalDate(model.getFechaDesde()))
                .fechaHasta(DateUtils.toLocalDate(model.getFechaHasta()))
                .build();
    }

    public AdEmpresaPeriodoEntity builderUpdateEntity(AdEmpresaPeriodoCreationRequestDto model, AdEmpresaPeriodoEntity item) {
        return AdEmpresaPeriodoEntity.builder()
                .idPeriodo(item.getIdPeriodo())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .ano(model.getAno())
                .cPeriodo(model.getCPeriodo())
                .periodo(model.getPeriodo())
                .fechaDesde(DateUtils.toLocalDate(model.getFechaDesde()))
                .fechaHasta(DateUtils.toLocalDate(model.getFechaHasta()))
                .build();
    }

    public AdEmpresaPeriodoCreationResponseDto builderResponse(AdEmpresaPeriodoEntity model) {
        return AdEmpresaPeriodoCreationResponseDto.builder()
                .idPeriodo(model.getIdPeriodo())
                .ano(model.getAno())
                .cPeriodo(model.getCPeriodo())
                .periodo(model.getPeriodo())
                .fechaDesde(DateUtils.toString(model.getFechaDesde()))
                .fechaHasta(DateUtils.toString(model.getFechaHasta()))
                .build();
    }
}
