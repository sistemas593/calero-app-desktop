package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_paraisos_fiscales")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class TbParaisoFiscalEntity extends Auditable {

    @Id
    private String codigo;
    private String paraisoFiscal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_pais")
    private TbPaisEntity pais;

}
