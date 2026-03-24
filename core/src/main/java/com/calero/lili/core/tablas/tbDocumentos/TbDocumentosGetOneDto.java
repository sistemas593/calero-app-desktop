package com.calero.lili.core.tablas.tbDocumentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbDocumentosGetOneDto {
    private String codigoDocumento;
    private String documento;
}
