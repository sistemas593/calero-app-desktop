package com.calero.lili.core.comprobantes.objetosXml.factura;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement // DEFINE RAIZ DEL DOC XML
@XmlType(propOrder = {"ambiente", "tipoEmision", "razonSocial","nombreComercial","ruc","claveAcceso","codDoc","estab","ptoEmi","secuencial","dirMatriz", "agenteRetencion", "contribuyenteRimpe"}) // define el orden
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoTributaria {
    private String ambiente;
    private String tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String claveAcceso;
    private String codDoc;
    private String estab;
    private String ptoEmi;
    private String secuencial;
    private String dirMatriz;

    private String contribuyenteRimpe;

    private String agenteRetencion;

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getTipoEmision() {
        return tipoEmision;
    }

    public void setTipoEmision(String tipoEmision) {
        this.tipoEmision = tipoEmision;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String codDoc) {
        this.codDoc = codDoc;
    }

    public String getEstab() {
        return estab;
    }

    public void setEstab(String estab) {
        this.estab = estab;
    }

    public String getPtoEmi() {
        return ptoEmi;
    }

    public void setPtoEmi(String ptoEmi) {
        this.ptoEmi = ptoEmi;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public String getDirMatriz() {
        return dirMatriz;
    }

    public void setDirMatriz(String dirMatriz) {
        this.dirMatriz = dirMatriz;
    }

    public String getContribuyenteRimpe() {
        return contribuyenteRimpe;
    }

    public void setContribuyenteRimpe(String contribuyenteRimpe) {
        this.contribuyenteRimpe = contribuyenteRimpe;
    }

    public String getAgenteRetencion() {
        return agenteRetencion;
    }

    public void setAgenteRetencion(String agenteRetencion) {
        this.agenteRetencion = agenteRetencion;
    }
}
