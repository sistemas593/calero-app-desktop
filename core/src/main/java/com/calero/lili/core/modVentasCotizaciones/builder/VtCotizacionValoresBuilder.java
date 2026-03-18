package com.calero.lili.core.modVentasCotizaciones.builder;


import com.calero.lili.core.modVentasCotizaciones.VtCotizacionValoresEntity;
import com.calero.lili.core.modVentasCotizaciones.dto.CreationVentasCotizacionesRequestDto;
import com.calero.lili.core.modVentasCotizaciones.dto.ResponseValoresDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VtCotizacionValoresBuilder {

    public List<VtCotizacionValoresEntity> builderList(List<CreationVentasCotizacionesRequestDto.ValoresDto> list, Long idData, Long idEmpresa) {
        return list
                .stream()
                .map(x -> builderValores(x, idData, idEmpresa))
                .toList();
    }

    private VtCotizacionValoresEntity builderValores(CreationVentasCotizacionesRequestDto.ValoresDto model, Long idData, Long idEmpresa) {
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

    public List<ResponseValoresDto> builderListValoresDto(List<VtCotizacionValoresEntity> list) {
        return list.stream()
                .map(this::builderValoresDto)
                .toList();
    }

    private ResponseValoresDto builderValoresDto(VtCotizacionValoresEntity model) {
        return ResponseValoresDto.builder()
                .valor(model.getValor())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(model.getBaseImponible())
                .codigo(model.getCodigo())
                .build();
    }
}
