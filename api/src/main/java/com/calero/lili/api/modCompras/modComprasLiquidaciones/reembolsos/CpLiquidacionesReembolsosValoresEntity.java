package com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpLiquidacionesReembolsosValores")
@Builder
public class  CpLiquidacionesReembolsosValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idLiquidacionValores;

    private String codigo;

    private String codigoPorcentaje;

    private BigDecimal tarifa;

    private BigDecimal baseImponible;

    private BigDecimal valor;

}
