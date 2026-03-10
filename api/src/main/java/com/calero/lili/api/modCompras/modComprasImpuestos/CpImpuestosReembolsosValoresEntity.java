package com.calero.lili.api.modCompras.modComprasImpuestos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpImpuestosReembolsosValores") //invoice_details

public class CpImpuestosReembolsosValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idImpuestosValores;

    private String codigo;

    private String codigoPorcentaje;

    private int tarifa;

    private BigDecimal baseImponible;

    private BigDecimal valor;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

}
