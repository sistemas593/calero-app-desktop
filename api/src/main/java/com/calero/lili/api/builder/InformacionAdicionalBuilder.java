package com.calero.lili.api.builder;


import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class InformacionAdicionalBuilder {


    public List<InformacionAdicionalDto> builderListDto(List<InformacionAdicional> modelList) {
        if (Objects.isNull(modelList)) return null;
        return modelList
                .stream()
                .map(this::builder)
                .toList();
    }

    public List<InformacionAdicional> builderList(List<InformacionAdicionalDto> modelList) {
        if (Objects.isNull(modelList)) return null;
        return modelList
                .stream()
                .map(this::builder)
                .toList();
    }


    public InformacionAdicional builder(InformacionAdicionalDto model) {
        return InformacionAdicional.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }

    public InformacionAdicionalDto builder(InformacionAdicional model) {
        return InformacionAdicionalDto.builder()
                .nombre(model.getNombre())
                .valor(model.getValor())
                .build();
    }


}
