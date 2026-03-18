package com.calero.lili.core.modVentas.reembolsos;

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
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtVentasReembolsosValores") //invoice_details
@Builder
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class VtVentaReembolsosValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idVentaValores;

    private String codigo;

    private String codigoPorcentaje;

    private BigDecimal tarifa;

    private BigDecimal baseImponible;

    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta") // NOMBRE DEL CAMPO RELACIONADO, PONER EL MIMO NOMBRE QUE TIENE LA TABLA PRINCIPAL
    private VtVentaReembolsosEntity rembolsosValores; // NOMBRE DE LA RELACION, ESTE VA EN LA TABLA PRINCIPAL

}
