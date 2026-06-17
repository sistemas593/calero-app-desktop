package com.calero.lili.core.modComprasOrden.builder;

import com.calero.lili.core.modComprasOrden.CpOrdenComprasValoresEntity;
import com.calero.lili.core.modComprasOrden.dto.OrdenCompraRequestDto;
import com.calero.lili.core.modComprasOrden.dto.ResponseCompraOrdenValoresDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CpOrdenComprasValoresBuilder {

    public List<CpOrdenComprasValoresEntity> builderListValores(List<OrdenCompraRequestDto.ValoresComprasOrdenDto> list,
                                                                Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builderEntity(x, idData, idEmpresa))
                .toList();
    }

    private CpOrdenComprasValoresEntity builderEntity(OrdenCompraRequestDto.ValoresComprasOrdenDto model, Long idData, Long idEmpresa) {
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

    public List<ResponseCompraOrdenValoresDto> builderResponseListValores(List<CpOrdenComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderValores)
                .toList();
    }

    private ResponseCompraOrdenValoresDto builderValores(CpOrdenComprasValoresEntity model) {
        return ResponseCompraOrdenValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }


    public List<ResponseCompraOrdenValoresDto> builderAnuladoResponseListValores(List<CpOrdenComprasValoresEntity> list) {
        return list.stream()
                .map(this::builderAnuladoValores)
                .toList();
    }

    private ResponseCompraOrdenValoresDto builderAnuladoValores(CpOrdenComprasValoresEntity model) {
        return ResponseCompraOrdenValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal("0.00"))
                .valor(new BigDecimal("0.00"))
                .tarifa(new BigDecimal("0.00"))
                .build();
    }
}
