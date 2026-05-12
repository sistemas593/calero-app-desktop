package com.calero.lili.core.modLocalidades.modCantones;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_cantones")
@Where(clause = "deleted = false")
public class CantonEntity extends Auditable {


    @Id
    @Column(unique = true, updatable = false, nullable = false, name = "codigo_canton")
    private String codigoCanton;

    @Column(name = "canton")
    private String canton;

    @ManyToOne()
    @JoinColumn(name = "codigo_provincia", referencedColumnName = "codigo_provincia")
    private ProvinciaEntity provincia;

    @OneToMany(mappedBy = "canton", fetch = FetchType.EAGER)
    private List<ParroquiaEntity> parroquias;

}
