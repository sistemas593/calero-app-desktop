package com.calero.lili.api.adEmpresasPeriodo;

import com.calero.lili.core.Auditable;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_empresas_periodos")
public class AdEmpresaPeriodoEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPeriodo;
    private Long idData;
    private Long idEmpresa;
    private String ano;
    private String cPeriodo;
    private String periodo;
    private  LocalDate fechaDesde;
    private LocalDate fechaHasta;
    
}
