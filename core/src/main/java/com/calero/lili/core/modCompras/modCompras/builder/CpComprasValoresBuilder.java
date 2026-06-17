package com.calero.lili.core.modCompras.modCompras.builder;

import com.calero.lili.core.modCompras.modCompras.CpComprasValoresEntity;
import com.calero.lili.core.modCompras.modCompras.dto.CompraRequestDto;
import com.calero.lili.core.modCompras.modCompras.dto.ResponseCompraValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CpComprasValoresBuilder {

    public List<CpComprasValoresEntity> builderListValores(List<CompraRequestDto.ValoresCompraDto> list,
                                                           Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpComprasValoresEntity builderEntity(CompraRequestDto.ValoresCompraDto model, Long idData, Long idEmpresa) {
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

    public List<ResponseCompraValoresDto> builderResponseListValores(List<CpComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private ResponseCompraValoresDto builderValores(CpComprasValoresEntity model) {
        return ResponseCompraValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }
}
