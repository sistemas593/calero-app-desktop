package com.calero.lili.api.modComprasProveedoresGrupos.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CpProveedorGrupoListFilterDto {

    private String filter;
    private Integer idCuentaCredito;
    private Integer idCuentaAnticipos;

}
