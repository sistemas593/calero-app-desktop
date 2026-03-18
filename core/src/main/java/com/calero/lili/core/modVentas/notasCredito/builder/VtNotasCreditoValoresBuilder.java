package com.calero.lili.core.modVentas.notasCredito.builder;


import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.core.modVentas.notasCredito.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtNotasCreditoValoresBuilder {

    public List<VtVentaValoresEntity> builderList(List<CreationNotaCreditoRequestDto.ValoresDto> list, Long idData, Long idEmpresa) {
        return list
                .stream()
                .map(x -> builderValores(x, idData, idEmpresa))
                .toList();
    }

    private VtVentaValoresEntity builderValores(CreationNotaCreditoRequestDto.ValoresDto model, Long idData, Long idEmpresa) {
        return VtVentaValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .tarifa(model.getTarifa())
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
                .tarifa(model.getTarifa())
                .build();
    }
}
