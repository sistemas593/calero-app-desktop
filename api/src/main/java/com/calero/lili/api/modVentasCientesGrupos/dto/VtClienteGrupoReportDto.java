package com.calero.lili.api.modVentasCientesGrupos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
public class VtClienteGrupoReportDto {

    private UUID idGrupo;
    private String grupo;

    private CuentaCredito cuentaCredito;
    private CuentaAnticipo cuentaAnticipo;
    private Boolean predeterminado;


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
