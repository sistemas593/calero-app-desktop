package com.calero.lili.api.modAdminUsuarios.adGrupos.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class GruposFilter {

    private String filter;

    public String getFilter() {
        if (Objects.isNull(filter) || filter.isEmpty()) return "";
        return filter;
    }
}
