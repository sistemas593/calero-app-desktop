package com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1;

import jakarta.xml.bind.annotation.XmlType;

;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "obligadoContabilidad","tipoIdentificacionSujetoRetenido","razonSocialSujetoRetenido","identificacionSujetoRetenido" ,"periodoFiscal"}) // ,"pago"

public class InfoCompRetencion {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String obligadoContabilidad;
    private String tipoIdentificacionSujetoRetenido;
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
