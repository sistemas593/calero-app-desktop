package com.calero.lili.core.modClientesConfiguraciones.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class VtClientesConfiguracionesRequestDto {

    private UUID idConfiguracion;

    @NotEmpty(message = "No existe clave")
    private String clave;

    @NotEmpty(message = "No existe fecha vencimiento")
    private String fechaVencimiento;

    @NotEmpty(message = "No existe ruc")
    private String ruc;

    private String enviarCorreos;

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
    private UUID idTercero;

    private String modulos;
    private String rucsActivados;
    private String clavesPcs;

    private Long tipoBlo;
    private String fechaBlo;
}
