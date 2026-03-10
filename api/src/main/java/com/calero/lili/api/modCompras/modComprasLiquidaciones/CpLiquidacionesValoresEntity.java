package com.calero.lili.api.modCompras.modComprasLiquidaciones;

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
@Table(name = "cpLiquidacionesValores")
@Builder
public class CpLiquidacionesValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idLiquidacionValores;
    private String codigo;
    private String codigoPorcentaje;
    private BigDecimal baseImponible;
    private BigDecimal valor;
    private BigDecimal tarifa;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;


}
