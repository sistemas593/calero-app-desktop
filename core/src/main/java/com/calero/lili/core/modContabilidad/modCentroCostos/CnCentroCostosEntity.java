package com.calero.lili.core.modContabilidad.modCentroCostos;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cn_centro_costos")
@Where(clause = "deleted = false")
public class CnCentroCostosEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCentroCostos;

    @Column(name = "id_codigo_centro_costos_padre")
    private UUID idCodigoCentroCostosPadre;

    @Column(name = "codigo_centro_costos")
    private String codigoCentroCostos;

    @Column(name = "codigo_centro_costos_original")
    private String codigoCentroCostosOriginal;

    @Column(name = "centro_costos")
    private String centroCostos;

    @Column(name = "mayor")
    private Boolean mayor;

    @Column(name = "nivel")
    private Integer nivel;

}
