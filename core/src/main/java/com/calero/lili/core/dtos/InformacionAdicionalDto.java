package com.calero.lili.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformacionAdicionalDto {
    private String nombre;
    private String valor;
}
