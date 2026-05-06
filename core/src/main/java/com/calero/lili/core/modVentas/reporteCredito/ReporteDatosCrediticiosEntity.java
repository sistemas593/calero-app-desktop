package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.modTerceros.GeTerceroEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reporte_datos_crediticios")
@Builder
public class ReporteDatosCrediticiosEntity {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idDatosCrediticios;

    private Long idData;

    private Long idEmpresa;

    private String numeroOperacion;

    private BigDecimal valorOperacion;

    private BigDecimal saldoOperacion;

    private LocalDate fechaConcesion;

    private LocalDate fechaVencimiento;

    private LocalDate fechaExigible;

    private Integer plazoOperacion;

    private Integer periodicidadPago;

    private Integer diasMorosidad;

    private BigDecimal montoMorosidad;

    private BigDecimal montoInteresMora;

    private BigDecimal valorXVencer1a30Dias;

    private BigDecimal valorXVencer31a90Dias;

    private BigDecimal valorXVencer91a180Dias;

    private BigDecimal valorXVencer181a360Dias;

    private BigDecimal valorXVencerMas360Dias;

    private BigDecimal valorVencido1a30Dias;

    private BigDecimal valorVencido31a90Dias;

    private BigDecimal valorVencido91a180Dias;

    private BigDecimal valorVencido181a360Dias;

    private BigDecimal valorVencidoMas360Dias;

    private BigDecimal valorDemandaJudicial;

    private BigDecimal carteraCastigada;

    private BigDecimal coutaCredito;

    private LocalDate fechaCancelacion;

    @Column(length = 1)
    private String formaCancelacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

}
