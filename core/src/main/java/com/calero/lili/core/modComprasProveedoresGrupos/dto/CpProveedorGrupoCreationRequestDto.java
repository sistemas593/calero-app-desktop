package com.calero.lili.core.modComprasProveedoresGrupos.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CpProveedorGrupoCreationRequestDto {

    private String grupo;
    private UUID idCuentaCredito;
    private UUID idCuentaAnticipo;

}
