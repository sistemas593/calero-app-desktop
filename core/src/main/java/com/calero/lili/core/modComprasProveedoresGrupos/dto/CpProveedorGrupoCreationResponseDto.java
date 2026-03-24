package com.calero.lili.core.modComprasProveedoresGrupos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpProveedorGrupoCreationResponseDto {

    private UUID idGrupo;
    private String grupo;

    private CuentaCredito cuentaCredito;
    private CuentaAnticipo cuentaAnticipo;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CuentaCredito {
        private UUID idCuentaCredito;
        private String cuenta;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CuentaAnticipo {
        private UUID idCuentaAnticipo;
        private String cuenta;
    }

}
