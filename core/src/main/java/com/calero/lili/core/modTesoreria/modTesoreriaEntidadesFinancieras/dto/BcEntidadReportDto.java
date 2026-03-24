package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.dto;

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
public class BcEntidadReportDto {

    private UUID idEntidad;
    private String tipoEntidad;
    private String entidad;
    private String numeroCuenta;
    private String agencia;
    private String contacto;
    private String telefono1;
    private String telefono2;
    private String secuencialCheque;
    private String archivoCheque;
    private BigDecimal saldo;

    private PlanCuenta planCuenta;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PlanCuenta {
        private UUID idCuenta;
        private String cuenta;
    }

}
