package com.calero.lili.api.modContabilidad.modReportes.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceValoresDto {

    @JsonIgnore
    private UUID idCuenta;
    @JsonIgnore
    private UUID idCuentaPadre;
    @JsonIgnore
    private String codigoCuentaOriginal;
    private String codigoCuenta;
    private String cuenta;
    private BigDecimal saldoInicial;
    private BigDecimal debe;
    private BigDecimal haber;
    private BigDecimal saldoFinal;
    @JsonIgnore
    private String cuentaIndentada;
    @JsonIgnore
    private Boolean esMayor;
    @JsonIgnore
    private Integer grupo;

    @JsonIgnore
    private Boolean tieneMovimiento;


    private LocalDate fechaMes;

    private BigDecimal enero;
    private BigDecimal febrero;
    private BigDecimal marzo;
    private BigDecimal abril;
    private BigDecimal mayo;
    private BigDecimal junio;
    private BigDecimal julio;
    private BigDecimal agosto;
    private BigDecimal septiembre;
    private BigDecimal octubre;
    private BigDecimal noviembre;
    private BigDecimal diciembre;

    private Boolean totalMayor;

    private String codigoCentroCostos;
    private String centroCostos;


    public void limpiarMeses() {
        this.enero = null;
        this.febrero = null;
        this.marzo = null;
        this.abril = null;
        this.mayo = null;
        this.junio = null;
        this.julio = null;
        this.agosto = null;
        this.septiembre = null;
        this.octubre = null;
        this.noviembre = null;
        this.diciembre = null;
    }
}
