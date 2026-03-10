package com.calero.lili.core.modAdminEmpresas.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdEmpresaListFilterDto {
    private String filter;
    private Integer estado;
}
