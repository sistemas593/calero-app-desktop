package com.calero.lili.core.modContabilidad.modReportes.dto;

import com.calero.lili.core.dtos.PaginatedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MayorGeneralDto {


    private CabeceraMayorGeneralDto cabecera;
    private PaginatedDto<DetalleMayorGeneralDto> detalle;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CabeceraMayorGeneralDto {
        private String numeroCuenta;
        private String cuenta;
        private BigDecimal saldo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetalleMayorGeneralDto {
        private UUID idAsiento;
        private String fechaAsiento;
        private String cuenta;
        private String codigoCentroCostos;
        private String centroCostos;
        private String tipoAsiento;
        private String numeroAsiento;
        private String tipoDocumento;
        private String numeroDocumento;
        private String concepto;
        private BigDecimal debe;
        private BigDecimal haber;
        private BigDecimal saldo;
    }

}
