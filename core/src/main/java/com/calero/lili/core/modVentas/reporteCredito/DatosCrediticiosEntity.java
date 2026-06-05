package com.calero.lili.core.modVentas.reporteCredito;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "datos_crediticios_cabecera")
@Builder
public class DatosCrediticiosEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idDatosCrediticios;

    private Long idData;

    private Long idEmpresa;

    private String codigoEntidad;

    private String periodo;


    @Builder.Default
    @JoinColumn(name = "id_datos_crediticios", referencedColumnName = "idDatosCrediticios")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatosCrediticiosDetalleEntity> datosCrediticiosDetalle = new ArrayList<>();

}
