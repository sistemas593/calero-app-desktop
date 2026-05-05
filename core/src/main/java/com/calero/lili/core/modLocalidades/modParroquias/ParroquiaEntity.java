package com.calero.lili.core.modLocalidades.modParroquias;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_parroquias")
@Where(clause = "deleted = false")
public class ParroquiaEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false, name = "codigo_parroquia")
    private String codigoParroquia;

    @Column(name = "parroquia")
    private String parroquia;

    @ManyToOne()
    @JoinColumn(name = "codigo_canton", referencedColumnName = "codigo_canton")
    private CantonEntity canton;

}
