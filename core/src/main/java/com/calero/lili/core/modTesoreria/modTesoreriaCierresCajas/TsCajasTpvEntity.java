package com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_cajas_tpv")
public class TsCajasTpvEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCajaTpv;

    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;
         
    @Column(name = "sucursal")
    private String sucursal;

    @Column(name = "fecha_apertura")
    private  LocalDate fechaApertura;
         
    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;
         
    @Column(name = "efectivo")
    private BigDecimal efectivo;
         
    @Column(name = "cheque")
    private BigDecimal cheque;
         
    @Column(name = "tarjeta")
    private BigDecimal tarjeta;
         
    @Column(name = "transferencia")
    private BigDecimal transferencia;
         
    @Column(name = "retencion")
    private BigDecimal retencion;
         
    @Column(name = "otras")
    private BigDecimal otras;
         
    @Column(name = "credito")
    private BigDecimal credito;
         
    @Column(name = "saldo_utilizado")
    private BigDecimal saldoUtilizado;
         
    @Column(name = "base")
    private BigDecimal base;

    @Column(name = "iva")
    private BigDecimal iva;

    @Column(name = "ice")
    private BigDecimal ice;

    @Column(name = "propina")
    private BigDecimal propina;

}
