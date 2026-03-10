package com.calero.lili.api.modContabilidad.modAsientos.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibroDiarioDto {

    private UUID idAsiento;
    private String fechaAsiento;
    private String numeroAsiento;
    private String concepto;

    private List<DetalleCuenta> detalleCuentas;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleCuenta {

        private UUID idCuenta;
        private String codigoCuenta;
        private String nombreCuenta;
        private String numeroDocumento;
        private String tipoDocumento;
        private UUID idCentroCostos;
        private String nombreCentroCostos;
        private String codigoCentroCostos;
        private BigDecimal debe;
        private BigDecimal haber;
    }

}
