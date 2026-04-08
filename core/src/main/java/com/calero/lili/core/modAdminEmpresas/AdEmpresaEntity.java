package com.calero.lili.core.modAdminEmpresas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.enums.TipoContribuyente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ad_empresas")
@Where(clause = "deleted = false")
public class AdEmpresaEntity extends Auditable {

    private Long idData;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID id;

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
    private String representanteTipoIdentificacion;
    private String representanteIdentificacion;
    private String email;

    @Enumerated(EnumType.STRING)
    private TipoContribuyente tipoContribuyente;

    @Column(length = 1)
    private String obligadoContabilidad;

    private String devolucionIva;
    private String agenteRetencion;
    private String contribuyenteEspecial;
    private String codigoSustento;
    private String formaPagoSri;
    private LocalDate fechaCreacion;

    private int estado;

    private int ambienteFactura;
    private int ambienteNotaCredito;
    private int ambienteNotaDebito;
    private int ambienteGuiaRemision;
    private int ambienteLiquidacion;
    private int ambienteComprobanteRetencion;

    @Column(name = "momento_envio_factura")
    private int momentoEnvioFactura;
    @Column(name = "momento_envio_nota_credito")
    private int momentoEnvioNotaCredito;
    @Column(name = "momento_envio_nota_debito")
    private int momentoEnvioNotaDebito;
    @Column(name = "momento_envio_guia_remision")
    private int momentoEnvioGuiaRemision;
    @Column(name = "momento_envio_liquidacion")
    private int momentoEnvioLiquidacion;
    @Column(name = "momento_envio_comprobante_retencion")
    private int momentoEnvioComprobanteRetencion;


    // DEVOLVER CERO O UNO
    private Integer momentoEnvio;

    // ENCRIPTAR LA CONTRASEÑA
    private String contraseniaFirma;

    private LocalDate fechaCaducidadCertificado;

    // Ruta absoluta del archivo .p12 en el sistema local (modo LOC)
    // Ejemplo Windows : C:\Users\Ismael\Documents\firma.p12
    // Ejemplo Linux   : /home/ismael/firmas/empresa.p12
    private String rutaArchivoFirma;

    // Ruta absoluta del logo en el sistema local (modo LOC, opcional)
    private String rutaLogo;
}