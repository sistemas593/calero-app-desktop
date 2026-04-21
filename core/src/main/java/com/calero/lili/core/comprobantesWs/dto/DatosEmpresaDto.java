package com.calero.lili.core.comprobantesWs.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class DatosEmpresaDto {

    //private String sgn;
    private String pwd;
    private byte[] imageBytes;
    private InputStream inputStreamFileSgn;

    private int momentoEnvioFactura;
    private int momentoEnvioNotaCredito;
    private int momentoEnvioNotaDebito;
    private int momentoEnvioGuiaRemision;
    private int momentoEnvioLiquidacion;
    private int momentoEnvioComprobanteRetencion;
    private String origenDatos;
}
