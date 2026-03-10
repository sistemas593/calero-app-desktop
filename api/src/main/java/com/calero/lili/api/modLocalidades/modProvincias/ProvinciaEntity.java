package com.calero.lili.api.modLocalidades.modProvincias;

import com.calero.lili.api.modLocalidades.modCantones.CantonEntity;
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

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_provincias")
public class ProvinciaEntity {

    @Id
    @Column(name = "codigo_provincia",unique = true, updatable = false, nullable = false)
    private String codigoProvincia;

    @Column(name = "provincia")
    private String provincia;

    @OneToMany(mappedBy = "provincia", fetch = FetchType.EAGER)
    private List<CantonEntity> cantones;
}
