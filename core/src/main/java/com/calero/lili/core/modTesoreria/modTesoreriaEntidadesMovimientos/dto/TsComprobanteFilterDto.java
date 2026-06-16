package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@ToString
public class TsComprobanteFilterDto {

    private UUID idTercero;
    private String numeroIdentificacion;
    private String tipoDocumento;
    private String numeroDocumento;
    private String movimiento;
    private Timestamp fechaRegistro;
    private Timestamp fechaDocumento;
    private BigDecimal valor;
    private String concepto;
    private String nombre;
    private String observaciones;
    private Boolean fisico;
    private String cserie;
    private String nfisico;
    private Integer idConciliacion;
    private String tipomovbc;
    private String chejercant;
    private String cubano;
    private String cubperiodo;
    private String cubtipoasi;
    private String cubnasient;
}
