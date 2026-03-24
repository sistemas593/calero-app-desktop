package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto;

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
public class BcBancoMovimientoCreationRequestDto {

    private String sucursal;
    private String numeroIdentificacion;
    private String tipoDocumento;
    private String numeroDocumento;
    private String movimiento;
    private String fechaRegistro;
    private String fechaDocumento;
    private BigDecimal valor;
    private String concepto;
    private String nombre;
    private String observaciones;
    private Boolean fisico;
    private String codigoSerie;
    private String numeroComprobante;
    private UUID idConciliacion;
    private String tipomovbc;
    private String chejercant;
    private UUID idEntidad;
    private UUID idTercero;


}
