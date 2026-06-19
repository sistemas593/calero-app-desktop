package com.calero.lili.core.modTerceros.dto;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;


@Data
@ToString
public class GeTerceroFilterDto {

    private String filter;
    private Integer tipoTercero;

}
