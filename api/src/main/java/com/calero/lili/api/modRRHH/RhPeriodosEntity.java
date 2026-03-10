package com.calero.lili.api.modRRHH;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rh_periodos")
public class RhPeriodosEntity {

    @Id
    @Column(name = "id_periodo", unique = true, updatable = false, nullable = false)
    private UUID idPeriodo;

    private Long idData;

    private Long idEmpresa;

    private String mes;

    private String anio;

    private String periodo;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer estado; // 0 y 1
}
