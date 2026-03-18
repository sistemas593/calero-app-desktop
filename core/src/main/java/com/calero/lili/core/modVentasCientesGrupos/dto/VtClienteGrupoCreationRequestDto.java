package com.calero.lili.core.modVentasCientesGrupos.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VtClienteGrupoCreationRequestDto {

    private String grupo;
    private UUID idCuentaCredito;
    private UUID idCuentaAnticipos;

}
