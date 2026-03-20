package com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto;

import lombok.Data;

@Data
public class EnvioCorreoDto {

    private String comprobante;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private String nombreReceptor;
    private String codigoDocumento;
    private String serie;
    private String secuencial;
    private String fechaEmision;
    private String claveAcceso;
    private String email;

}
