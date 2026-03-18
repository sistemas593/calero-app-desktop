package com.calero.lili.core.modCompras.modComprasLiquidaciones.builder;

import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.ResponseValoresDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CpLiquidacionesValoresBuilder {

    public List<CpLiquidacionesValoresEntity> builderListValores(List<ValoresDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpLiquidacionesValoresEntity builderEntity(ValoresDto model, Long idData, Long idEmpresa) {
        return CpLiquidacionesValoresEntity.builder()
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

    public List<ResponseValoresDto> builderListResponseValores(List<CpLiquidacionesValoresEntity> list) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private ResponseValoresDto builderValores(CpLiquidacionesValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

    public List<ResponseValoresDto> builderAnuladaListResponseValores(List<CpLiquidacionesValoresEntity> list) {
        return list.stream()
                .map(this::builderAnuladaValores)
                .toList();
    }

    private ResponseValoresDto builderAnuladaValores(CpLiquidacionesValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal("0.00"))
                .valor(new BigDecimal("0.00"))
                .tarifa(new BigDecimal("0.00"))
                .build();
    }
}
