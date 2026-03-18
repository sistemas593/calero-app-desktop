package com.calero.lili.core.tablas.tbDocumentos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbDocumentosGetOneDto {
    private String codigoDocumento;
    private String documento;
}
