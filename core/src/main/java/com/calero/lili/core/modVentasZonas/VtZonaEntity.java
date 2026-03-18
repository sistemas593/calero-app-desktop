package com.calero.lili.core.modVentasZonas;

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
@Entity
@Table(name = "vt_zonas")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted = false")
public class VtZonaEntity extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idZona;
         
    @Column(name = "zona")
    private String zona;

}