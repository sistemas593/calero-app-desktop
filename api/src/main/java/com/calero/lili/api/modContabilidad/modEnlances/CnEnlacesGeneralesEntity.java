package com.calero.lili.api.modContabilidad.modEnlances;


import com.calero.lili.api.modAuditoria.Auditable;
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
@Table(name = "cn_enlaces_generales")
@Where(clause = "deleted = false")
public class CnEnlacesGeneralesEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idEnlace;

    private UUID idCuenta;

    private String detalle;

    private String codigo; // IVA_VT_15, IVA_VT_5 Y IVA_VT_8

}
