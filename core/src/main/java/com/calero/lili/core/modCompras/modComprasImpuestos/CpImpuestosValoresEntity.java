package com.calero.lili.core.modCompras.modComprasImpuestos;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpImpuestosValores")
public class CpImpuestosValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idImpuestosValores;

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
