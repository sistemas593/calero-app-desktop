package com.calero.lili.core.tablas.tbDocumentos;

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
@Table(name = "tb_documentos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbDocumentoEntity extends Auditable {
         
    @Id
    private String codigoDocumento;
    private String documento;

}