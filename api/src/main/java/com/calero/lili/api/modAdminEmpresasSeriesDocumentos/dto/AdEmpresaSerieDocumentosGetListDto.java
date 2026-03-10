package com.calero.lili.api.modAdminEmpresasSeriesDocumentos.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdEmpresaSerieDocumentosGetListDto {

    private UUID idDocumento;
    private String documento;
    private String numeroAutorizacion;
    private String secuencial;
    private String direccion;


}
