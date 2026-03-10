package com.calero.lili.api.modCompras.modComprasImpuestos.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformacionAdicionalDto {

    private String nombre;
    private String valor;
}
