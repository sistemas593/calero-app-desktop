package com.calero.lili.api.comprobantes.objetosXml.comprobanteRetencion;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad","tipoIdentificacionSujetoRetenido","parteRel","razonSocialSujetoRetenido","identificacionSujetoRetenido", "periodoFiscal" }) // ,"pago"
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InfoCompRetencion {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String tipoIdentificacionSujetoRetenido;
    private String parteRel;
    private String razonSocialSujetoRetenido;
    private String identificacionSujetoRetenido;

    private String periodoFiscal;

    
    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getDirEstablecimiento() {
        return dirEstablecimiento;
    }

    public void setDirEstablecimiento(String dirEstablecimiento) {
        this.dirEstablecimiento = dirEstablecimiento;
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = contribuyenteEspecial;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getTipoIdentificacionSujetoRetenido() {
        return tipoIdentificacionSujetoRetenido;
    }

    public void setTipoIdentificacionSujetoRetenido(String tipoIdentificacionSujetoRetenido) {
        this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
    }

    public String getParteRel() {
        return parteRel;
    }

    public void setParteRel(String parteRel) {
        this.parteRel = parteRel;
    }

    public String getRazonSocialSujetoRetenido() {
        return razonSocialSujetoRetenido;
    }

    public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
        this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
    }

    public String getIdentificacionSujetoRetenido() {
        return identificacionSujetoRetenido;
    }

    public void setIdentificacionSujetoRetenido(String identificacionSujetoRetenido) {
        this.identificacionSujetoRetenido = identificacionSujetoRetenido;
    }

    public String getPeriodoFiscal() {
        return periodoFiscal;
    }

    public void setPeriodoFiscal(String periodoFiscal) {
        this.periodoFiscal = periodoFiscal;
    }

}
