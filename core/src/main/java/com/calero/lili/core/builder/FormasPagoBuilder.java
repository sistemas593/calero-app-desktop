package com.calero.lili.core.builder;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.FormasPagoSri;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FormasPagoBuilder {


    public List<FormasPagoDto> builderListDto(List<FormasPagoSri> modelList) {
        return modelList
                .stream()
                .map(this::builder)
                .toList();
    }

    public List<FormasPagoSri> builderList(List<FormasPagoDto> modelList) {
        if(Objects.isNull(modelList)) return new ArrayList<>();
        return modelList
                .stream()
                .map(this::builder)
                .toList();
    }


    public FormasPagoSri builder(FormasPagoDto model) {
        return FormasPagoSri.builder()
                .unidadTiempo(model.getUnidadTiempo())
                .formaPago(model.getFormaPago())
                .total(model.getTotal())
                .plazo(model.getPlazo())
                .build();
    }

    public FormasPagoDto builder(FormasPagoSri model) {
        return FormasPagoDto.builder()
                .unidadTiempo(model.getUnidadTiempo())
                .formaPago(model.getFormaPago())
                .total(model.getTotal())
                .plazo(model.getPlazo())
                .build();
    }


}
