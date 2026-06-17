package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ResponseValoresDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ValoresBuilder {

    public List<CpImpuestosValoresEntity> builderList(List<ValoresCompraImpuestoDto> list, Long idData, Long idEmpresa) {
        return list.stream()
                .map(x -> builder(x, idData, idEmpresa))
                .toList();
    }

    private CpImpuestosValoresEntity builder(ValoresCompraImpuestoDto model, Long idData, Long idEmpresa) {
        return CpImpuestosValoresEntity.builder()
                .idImpuestosValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

    public List<ResponseValoresDto> builderListDto(List<CpImpuestosValoresEntity> list) {
        return list.stream()
                .map(this::builderDto)
                .toList();
    }

    private ResponseValoresDto builderDto(CpImpuestosValoresEntity model) {
        return ResponseValoresDto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .valor(model.getValor())
                .tarifa(model.getTarifa())
                .build();
    }

}
