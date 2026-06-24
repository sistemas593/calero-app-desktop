package com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TsCajasUsuarioResponseDto {

    private UUID idCajaUsuario;
    private Long idUsuario;
    private UUID idCajas;
    private String nombreCaja;
    private String codigoCaja;

}
