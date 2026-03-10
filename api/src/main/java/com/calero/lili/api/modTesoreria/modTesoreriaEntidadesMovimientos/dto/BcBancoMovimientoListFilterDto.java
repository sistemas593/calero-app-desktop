package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
public class BcBancoMovimientoListFilterDto {

    //private Long idData;
    //private int idEmpresa;
    private int idSucursal;
    private int idRegistro;
    private int idMovimiento;
    private int idBalance;
    private int idTercero;
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
