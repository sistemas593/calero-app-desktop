package com.calero.lili.core.modContabilidad.modSecuenciales.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CnSecuenciasResponseDto {

    private UUID idSecuencia;
    private String sucursal;
    private Integer anio;
    private Integer mes;
    private Integer ultimoNumero;

}
