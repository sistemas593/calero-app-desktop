package com.calero.lili.api.modComprasItems.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GeMedidasItemsDto {

    private UUID idMedida;
    private Integer factor;

}
