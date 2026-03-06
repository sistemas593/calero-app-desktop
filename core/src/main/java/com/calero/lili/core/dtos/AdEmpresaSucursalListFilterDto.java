package com.calero.lili.core.dtos;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdEmpresaSucursalListFilterDto {
    private int idSucursal;
    private String filter;
    private Boolean bloqueado;
    private String sucursal;
}
