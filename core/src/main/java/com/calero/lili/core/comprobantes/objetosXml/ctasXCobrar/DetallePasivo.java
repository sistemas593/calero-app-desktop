package com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"tipoAcreedor", "domicilioAcreedor", "valorDeuda", "paisAcreedor", "nombreAcreedor", "tipoIdentificacionAcreedor", "numeroIdentificacionAcreedor", "partesRelacionadas"})

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetallePasivo {

    private String tipoAcreedor;
    private String domicilioAcreedor;
    private String valorDeuda;
    private String paisAcreedor;
    private String nombreAcreedor;
    private String tipoIdentificacionAcreedor;
    private String numeroIdentificacionAcreedor;
    private String partesRelacionadas;

    public String getTipoAcreedor() {
        return tipoAcreedor;
    }

    public void setTipoAcreedor(String tipoAcreedor) {
        this.tipoAcreedor = tipoAcreedor;
    }

    public String getDomicilioAcreedor() {
        return domicilioAcreedor;
    }

    public void setDomicilioAcreedor(String domicilioAcreedor) {
        this.domicilioAcreedor = domicilioAcreedor;
    }

    public String getValorDeuda() {
        return valorDeuda;
    }

    public void setValorDeuda(String valorDeuda) {
        this.valorDeuda = valorDeuda;
    }

    public String getPaisAcreedor() {
        return paisAcreedor;
    }

    public void setPaisAcreedor(String paisAcreedor) {
        this.paisAcreedor = paisAcreedor;
    }

    public String getNombreAcreedor() {
        return nombreAcreedor;
    }

    public void setNombreAcreedor(String nombreAcreedor) {
        this.nombreAcreedor = nombreAcreedor;
    }

    public String getTipoIdentificacionAcreedor() {
        return tipoIdentificacionAcreedor;
    }

    public void setTipoIdentificacionAcreedor(String tipoIdentificacionAcreedor) {
        this.tipoIdentificacionAcreedor = tipoIdentificacionAcreedor;
    }

    public String getNumeroIdentificacionAcreedor() {
        return numeroIdentificacionAcreedor;
    }

    public void setNumeroIdentificacionAcreedor(String numeroIdentificacionAcreedor) {
        this.numeroIdentificacionAcreedor = numeroIdentificacionAcreedor;
    }

    public String getPartesRelacionadas() {
        return partesRelacionadas;
    }

    public void setPartesRelacionadas(String partesRelacionadas) {
        this.partesRelacionadas = partesRelacionadas;
    }
}
