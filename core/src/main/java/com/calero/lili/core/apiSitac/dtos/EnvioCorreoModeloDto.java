package com.calero.lili.core.apiSitac.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioCorreoModeloDto {

    private String nombreDocumento;
    private String inicialesDocumento;
    private String subject;
    private String body;
    private String xml;
    private String pdf;
    private String claveAcceso;
    private String secuencial;
    private String serie;

    private String tokenApi;
    private String emailFrom;
    private String emailTo;

}
