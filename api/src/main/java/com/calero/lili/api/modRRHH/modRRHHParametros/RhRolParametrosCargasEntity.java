package com.calero.lili.api.modRRHH.modRRHHParametros;

import com.calero.lili.api.modTerceros.GeTerceroEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "rh_rol_parametro_cargas")
public class RhRolParametrosCargasEntity {

    @Id
    @Column(name = "id_carga", unique = true, updatable = false, nullable = false)
    private UUID idCarga;

    private Long idData;

    private Long idEmpresa;

    private String anio;

    private String nombres;

    private String tipo; //hijo, conyugue y padre

    private LocalDate fechaDesde;

    private LocalDate fechaHasta;

    private Integer estado;

    private Integer numeroCargas;

    @ManyToOne()
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;
}
