package com.calero.lili.api.modTerceros.dto;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;


@Data
@ToString
public class GeTerceroFilterDto {

    private String filter;

    private String tipoIdentificacion;

    private UUID idGrupo;

    private Integer tipoTercero;

}
