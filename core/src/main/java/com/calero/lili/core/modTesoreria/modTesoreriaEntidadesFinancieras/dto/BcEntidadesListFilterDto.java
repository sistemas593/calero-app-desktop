package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BcEntidadesListFilterDto {

    private String entidad;
    private String numeroCuenta;
    private String filter;
    private String tipoEntidad;

}
