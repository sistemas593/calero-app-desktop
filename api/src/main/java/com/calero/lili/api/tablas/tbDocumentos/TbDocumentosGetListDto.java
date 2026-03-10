package com.calero.lili.api.tablas.tbDocumentos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TbDocumentosGetListDto {
    private String codigoDocumento;
    private String documento;
}
