package com.calero.lili.core.modVentasCientesGrupos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VtClienteGrupoCreationRequestDto {

    private String grupo;
    private UUID idCuentaCredito;
    private UUID idCuentaAnticipos;

}
