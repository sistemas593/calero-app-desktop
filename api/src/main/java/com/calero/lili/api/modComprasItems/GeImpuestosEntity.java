package com.calero.lili.api.modComprasItems;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_impuestos")
@Builder
public class GeImpuestosEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImpuesto;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "codigo_porcentaje")
    private String codigoPorcentaje;

    @Column(name = "tarifa")
    private BigDecimal tarifa;


    @ManyToMany(mappedBy = "impuestos")
    private List<GeItemEntity> items;

}
