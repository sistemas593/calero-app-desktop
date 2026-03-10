package com.calero.lili.api.modLocalidades.modCantones;

import com.calero.lili.api.modLocalidades.modProvincias.ProvinciaEntity;
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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_cantones")
public class CantonEntity {


    @Id
    @Column(unique = true, updatable = false, nullable = false, name = "codigo_canton")
    private String codigoCanton;

    @Column(name = "canton")
    private String canton;

    @ManyToOne()
    @JoinColumn(name = "codigo_provincia", referencedColumnName = "codigo_provincia")
    private ProvinciaEntity provincia;


}
