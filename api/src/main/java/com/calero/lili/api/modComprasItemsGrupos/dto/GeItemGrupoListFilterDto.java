package com.calero.lili.api.modComprasItemsGrupos.dto;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class GeItemGrupoListFilterDto {

    private UUID idGrupo;
    private String grupo;
    private String filter;
}
