package com.calero.lili.core.modAdModulos;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Entity
@Table(name = "ad_modulos")
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class AdModulosEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModulo;

    private String modulo;


}
