package com.calero.lili.core.modComprasProveedoresGrupos.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CpProveedorGrupoCreationRequestDto {

    private String grupo;
    private UUID idCuentaCredito;
    private UUID idCuentaAnticipo;

}
