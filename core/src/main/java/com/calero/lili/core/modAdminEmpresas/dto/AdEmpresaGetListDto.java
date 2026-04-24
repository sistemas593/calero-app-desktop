package com.calero.lili.core.modAdminEmpresas.dto;

import com.calero.lili.core.enums.TipoContribuyente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdEmpresaGetListDto {

    private Long idEmpresa;
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
    private String representanteTipoIdenficacion;
    private String representanteIdentificacion;
    private String email;
    private TipoContribuyente tipoContribuyente;
    private String obligadoContabilidad;
    private String devolucionIva;
    private String agenteRetencion;
    private String contribuyenteEspecial;
    private int ambienteFactura;
    private int ambienteNotaCredito;
    private int ambienteNotaDebito;
    private int ambienteGuiaRemision;
    private int ambienteLiquidacion;
    private int ambienteComprobanteRetencion;
    private int momentoEnvioFactura;
    private int momentoEnvioNotaCredito;
    private int momentoEnvioNotaDebito;
    private int momentoEnvioGuiaRemision;
    private int momentoEnvioLiquidacion;
    private int momentoEnvioComprobanteRetencion;
}
