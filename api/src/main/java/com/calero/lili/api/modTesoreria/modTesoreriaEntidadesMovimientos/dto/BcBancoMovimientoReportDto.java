package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class BcBancoMovimientoReportDto {


    //private Long idData;
    //private Long idEmpresa;
    private String idSucursal;
    private int idRegistro;
    private Long idMovimiento;
    private int idBalance;
    private int idTercero;
    private String numeroIdentificacion;
    private String tipoDocumento;
    private String numeroDocumento;
    private String movimiento;
    private LocalDate fechaRegistro;
    private LocalDate fechaDocumento;
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
