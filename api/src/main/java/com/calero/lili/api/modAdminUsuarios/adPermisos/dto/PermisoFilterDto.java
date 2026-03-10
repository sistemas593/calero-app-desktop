package com.calero.lili.api.modAdminUsuarios.adPermisos.dto;


import lombok.Data;

import java.util.Objects;

@Data
public class PermisoFilterDto {

    private String filter;

    public String getFilter() {
        if (Objects.isNull(filter) || filter.isEmpty()) return "";
        return filter;
    }

}
