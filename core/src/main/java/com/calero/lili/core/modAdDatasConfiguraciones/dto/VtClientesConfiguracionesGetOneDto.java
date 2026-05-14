package com.calero.lili.core.modAdDatasConfiguraciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VtClientesConfiguracionesGetOneDto {

    private UUID idConfiguracion;
    private String clave;
    private String fechaVencimiento;
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

    private String modulos;
    private String rucsActivados;
    private String clavesPcs;

    private Long tipoBlo;
    private String fechaBlo;


    private UUID idTercero;
    private String nombreTercero;

    private List<ModuloDto> moduloList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModuloDto {
        private Long idModulo;
        private String modulo;
    }


}
