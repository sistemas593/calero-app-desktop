package com.calero.lili.core.dtos;

import com.calero.lili.core.enums.TipoContribuyente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdEmpresaRequestDto {
    private String razonSocial;
    private String ruc;
    private String telefono1;
    private String telefono2;
    private String ciudad;
    private String direccionMatriz;
    private String numero;
    private String contadorNombre;
    private String contadorRuc;
    private String representanteNombre;
    private String representanteTipoIdentificacion;
    private String representanteIdentificacion;
    private String email;
    private TipoContribuyente tipoContribuyente;
    private String obligadoContabilidad;
    private String devolucionIva;
    private String agenteRetencion;
    private String contribuyenteEspecial;
    private String codigoSustento;
    private String formaPagoSri;
    private String contraseniaFirma;
    private String fechaCaducidadCertificado;
    private int momentoEnvioFactura;
    private int momentoEnvioNotaCredito;
    private int momentoEnvioNotaDebito;
    private int momentoEnvioGuiaRemision;
    private int momentoEnvioLiquidacion;
    private int momentoEnvioComprobanteRetencion;
}
