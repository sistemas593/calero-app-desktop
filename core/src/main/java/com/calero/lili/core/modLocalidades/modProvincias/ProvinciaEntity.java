package com.calero.lili.core.modLocalidades.modProvincias;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_provincias")
@Where(clause = "deleted = false")
public class ProvinciaEntity extends Auditable {

    @Id
    @Column(name = "codigo_provincia",unique = true, updatable = false, nullable = false)
    private String codigoProvincia;

    @Column(name = "provincia")
    private String provincia;

    @OneToMany(mappedBy = "provincia", fetch = FetchType.EAGER)
    private List<CantonEntity> cantones;
}
