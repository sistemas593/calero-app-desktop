package com.calero.lili.api.modClientesConfiguraciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class VtClientesConfiguracionesGetListDto {

    private UUID idConfiguracion;
    private String clave;
    private String ruc;
    private String enviarCorreos;
    private String fechaVencimiento;

    private BigDecimal valorRenovacion;
    private String facturaEmite;
    private String fechaEmitirFactura;
    private String fechaLlamar;
    private String usuarios;
    private String respaldosResponsable;
    private String respaldoUltimoOficina;
    private String conexionBase;
    private String configuraciones;
    private String notas;

    private String modulos;
    private String rucsActivados;
    private String clavesPcs;

    private Long tipoBlo;
    private String fechaBlo;

    private VtClientesConfiguracionesGetOneDto.Cliente cliente;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Cliente {
        private UUID idTercero;
        private String cliente;
    }

}
