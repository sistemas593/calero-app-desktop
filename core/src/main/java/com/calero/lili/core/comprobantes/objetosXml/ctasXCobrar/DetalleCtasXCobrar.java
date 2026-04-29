package com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"nombreDeudor", "tipoDeudor", "tipoIdentificacion", "numeroIdentificacion", "ubicacion", "pais", "partesRelacionadas", "saldo"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCtasXCobrar {

    private String nombreDeudor;
    private String tipoDeudor;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String ubicacion;
    private String pais;
    private String partesRelacionadas;
    private String saldo;

    public String getNombreDeudor() {
        return nombreDeudor;
    }

    public void setNombreDeudor(String nombreDeudor) {
        this.nombreDeudor = nombreDeudor;
    }

    public String getTipoDeudor() {
        return tipoDeudor;
    }

    public void setTipoDeudor(String tipoDeudor) {
        this.tipoDeudor = tipoDeudor;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPartesRelacionadas() {
        return partesRelacionadas;
    }

    public void setPartesRelacionadas(String partesRelacionadas) {
        this.partesRelacionadas = partesRelacionadas;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
}
