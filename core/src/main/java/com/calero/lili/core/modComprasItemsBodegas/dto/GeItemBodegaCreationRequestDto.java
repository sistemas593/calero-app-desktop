package com.calero.lili.core.modComprasItemsBodegas.dto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class GeItemBodegaCreationRequestDto {

    private String bodega;
    private String sucursal;

}
