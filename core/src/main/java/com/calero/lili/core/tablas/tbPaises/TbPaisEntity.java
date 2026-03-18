package com.calero.lili.core.tablas.tbPaises;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_paises")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbPaisEntity extends Auditable {
         
    @Id
    private String codigoPais;
    private String pais;

}