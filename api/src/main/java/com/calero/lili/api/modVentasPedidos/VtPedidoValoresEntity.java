package com.calero.lili.api.modVentasPedidos;

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
@Table(name = "vtPedidosValores") //invoice_details
@Builder
public class VtPedidoValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPedidoValores;

    private String codigo;

    private String codigoPorcentaje;

    private BigDecimal baseImponible;

    private BigDecimal valor;

}
