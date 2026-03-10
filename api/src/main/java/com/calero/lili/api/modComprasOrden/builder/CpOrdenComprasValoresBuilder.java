package com.calero.lili.api.modComprasOrden.builder;

import com.calero.lili.api.modComprasOrden.CpOrdenComprasValoresEntity;
import com.calero.lili.api.modComprasOrden.dto.OrdenCompraRequestDto;
import com.calero.lili.api.modComprasOrden.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CpOrdenComprasValoresBuilder {

    public List<CpOrdenComprasValoresEntity> builderListValores(List<OrdenCompraRequestDto.ValoresDto> list,
                                                                Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpOrdenComprasValoresEntity builderEntity(OrdenCompraRequestDto.ValoresDto model, Long idData, Long idEmpresa) {
        return CpOrdenComprasValoresEntity.builder()
                .idLiquidacionValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

    public List<ResponseValoresDto> builderResponseListValores(List<CpOrdenComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private ResponseValoresDto builderValores(CpOrdenComprasValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }


    public List<ResponseValoresDto> builderAnuladoResponseListValores(List<CpOrdenComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderAnuladoValores)
                .toList();
    }

    private ResponseValoresDto builderAnuladoValores(CpOrdenComprasValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal("0.00"))
                .valor(new BigDecimal("0.00"))
                .tarifa(new BigDecimal("0.00"))
                .build();
    }
}
