package com.calero.lili.core.modVentasZonas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VtZonaCreationRequestDto {

    //private UUID idZona;

    private String zona;

}
