package com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TsCajasResponseDto {

    private UUID idCaja;
    private String nombre;
    private String codigoCaja;
    private UUID idEntidad;
    private String entidad;

}
