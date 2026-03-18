package com.calero.lili.core.modAdminPorcentajes;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Table(name = "ad_iva_porcentajes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class AdIvaPorcentajesEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIvaPorcentaje;

    private Integer iva1;
    private Integer iva2;
    private Integer iva3;
    private LocalDate fechaDesde;


}
