package com.calero.lili.core.modAdminEmpresas.dto;

import com.calero.lili.core.enums.TipoContribuyente;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdEmpresaRequestDto {

    @NotEmpty(message = "No existe razón social")
    private String razonSocial;

    @NotEmpty(message = "No existe RUC")
    private String ruc;

    private String telefono1;
    private String telefono2;
    private String ciudad;

    @NotEmpty(message = "No existe dirección matriz")
    private String direccionMatriz;

    private String numero;
    private String contadorNombre;
    private String contadorRuc;
    private String representanteNombre;
    private String representanteTipoIdentificacion;
    private String representanteIdentificacion;
    private String email;

    @NotNull(message = "No existe el tipo contribuyente")
    private TipoContribuyente tipoContribuyente;

    @NotEmpty(message = "No existe obligado a la contabilidad")
    private String obligadoContabilidad;

    private String devolucionIva;
    private String agenteRetencion;

    @NotEmpty(message = "No existe contribuyente especial")
    private String contribuyenteEspecial;
    private String codigoSustento;
    private String formaPagoSri;

    private String contraseniaFirma;

    private String fechaCaducidadCertificado;

    // Ruta absoluta del .p12 en el sistema local
    private String rutaArchivoFirma;

    // Ruta absoluta del logo en el sistema local (opcional)
    private String rutaLogo;

    private int momentoEnvioFactura;
    private int momentoEnvioNotaCredito;
    private int momentoEnvioNotaDebito;
    private int momentoEnvioGuiaRemision;
    private int momentoEnvioLiquidacion;
    private int momentoEnvioComprobanteRetencion;

}
