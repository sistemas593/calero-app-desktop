package com.calero.lili.api.modAdminUsuarios.adRol.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class RolFilterDto {

    private String filter;

    public String getFilter() {
        if (Objects.isNull(filter) || filter.isEmpty()) return "";
        return filter;
    }

}
