package com.calero.lili.api.modComprasItemsMedidas;

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
@Entity
@Table(name = "ge_medidas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class GeItemsMedidasEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idUnidadMedida;

    @Column(name = "id_data")
    private Long idData;

    private String unidadMedida;


}
