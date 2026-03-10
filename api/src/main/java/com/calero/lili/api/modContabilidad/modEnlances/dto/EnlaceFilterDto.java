package com.calero.lili.api.modContabilidad.modEnlances.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class EnlaceFilterDto {

    private String filter;

    public String getFilter() {
        if (Objects.isNull(filter) || filter.isEmpty()) return "";
        return filter;
    }
}
