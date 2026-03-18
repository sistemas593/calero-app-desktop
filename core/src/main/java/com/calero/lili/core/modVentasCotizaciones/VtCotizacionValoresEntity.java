package com.calero.lili.core.modVentasCotizaciones;

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
@Table(name = "vtCotizacionesValores") //invoice_details
@Builder
public class VtCotizacionValoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCotizacionValores;

    private String codigo;

    private String codigoPorcentaje;

    private BigDecimal baseImponible;

    private BigDecimal valor;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

}
