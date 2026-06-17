package com.calero.lili.core.modVentasCotizaciones.builder;


import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.modVentasCotizaciones.VtCotizacionValoresEntity;
import com.calero.lili.core.modVentasCotizaciones.dto.ResponseVentasCotizacionesValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtCotizacionValoresBuilder {

    public List<VtCotizacionValoresEntity> builderList(List<ValoresDto> list, Long idData, Long idEmpresa) {
        return list
                .stream()
                .map(x -> builderValores(x, idData, idEmpresa))
                .toList();
    }

    private VtCotizacionValoresEntity builderValores(ValoresDto model, Long idData, Long idEmpresa) {
        return VtCotizacionValoresEntity.builder()
                .idCotizacionValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .build();
    }

    public List<ResponseVentasCotizacionesValoresDto> builderListValoresDto(List<VtCotizacionValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresDto)
                .toList();
    }

    private ResponseVentasCotizacionesValoresDto builderValoresDto(VtCotizacionValoresEntity model) {
        return ResponseVentasCotizacionesValoresDto.builder()
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .build();
    }
}
