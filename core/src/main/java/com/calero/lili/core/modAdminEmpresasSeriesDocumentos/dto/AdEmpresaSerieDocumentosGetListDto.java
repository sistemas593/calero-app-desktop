package com.calero.lili.core.modAdminEmpresasSeriesDocumentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEmpresaSerieDocumentosGetListDto {

    private UUID idDocumento;
    private String documento;
    private String numeroAutorizacion;
    private String secuencial;
    private String direccion;


}
