package com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TsCajasRequestDto {

    private String nombre;
    @NotNull(message = "Es requerido el codigo de caja")
    private String codigoCaja;
    private UUID idEntidad;

}
