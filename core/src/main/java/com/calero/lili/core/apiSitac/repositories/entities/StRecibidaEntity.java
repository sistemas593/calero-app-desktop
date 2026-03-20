package com.calero.lili.core.apiSitac.repositories.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "st_recibidas")
public class StRecibidaEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroAutorizacion;

    private String fechaAutorizacion;

    private String periodo;

    private LocalDate fechaCreacion;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

}
