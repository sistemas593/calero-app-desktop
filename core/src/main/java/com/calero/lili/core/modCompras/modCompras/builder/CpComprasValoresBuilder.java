package com.calero.lili.core.modCompras.modCompras.builder;

import com.calero.lili.core.modCompras.modCompras.CpComprasValoresEntity;
import com.calero.lili.core.modCompras.modCompras.dto.CompraRequestDto;
import com.calero.lili.core.modCompras.modCompras.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CpComprasValoresBuilder {

    public List<CpComprasValoresEntity> builderListValores(List<CompraRequestDto.ValoresDto> list,
                                                           Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpComprasValoresEntity builderEntity(CompraRequestDto.ValoresDto model, Long idData, Long idEmpresa) {
        return CpComprasValoresEntity.builder()
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

    public List<ResponseValoresDto> builderResponseListValores(List<CpComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private ResponseValoresDto builderValores(CpComprasValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }
}
