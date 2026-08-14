package com.calero.lili.core.modCompras.modCodigos;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "codigo_retencion_formulario")
@Builder
public class CodigoRetencionFormulario {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo_retencion")
    private String codigoRetencion;

    @Column(name = "codigo_formulario")
    private String codigoFormulario;

    private LocalDate fechaDesde;

    private LocalDate fechaHasta;


}
