package com.calero.lili.api.comprobantes.objetosXml.guiaRemision;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = { "identificacionDestinatario","razonSocialDestinatario","dirDestinatario","motivoTraslado",
        "docAduaneroUnico","codEstabDestino","ruta","codDocSustento","numDocSustento", "numAutDocSustento", "fechaEmisionDocSustento","detalle"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Destinatario {

    private String identificacionDestinatario;
    private String razonSocialDestinatario;
    private String dirDestinatario;
    private String motivoTraslado;

    private String docAduaneroUnico;
    private String codEstabDestino;
    private String ruta;

    private String codDocSustento;
    private String numDocSustento;
    private String numAutDocSustento;
    private String fechaEmisionDocSustento;

    private List<Detalle> detalle;
//    private List<DetAdicional> detAdicional;

    @XmlElementWrapper(name = "detalles") // envoltorio
    public List<Detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Detalle> detalle) {
        this.detalle = detalle;
    }


//    @XmlElementWrapper(name = "detallesAdicionales") // envoltorio
//    public List<DetAdicional> getDetAdicional() {
//        return detAdicional;
//    }
//
//    public void setDetAdicional(List<DetAdicional> detAdicional) {
//        this.detAdicional = detAdicional;
//    }

    public String getIdentificacionDestinatario() {
        return identificacionDestinatario;
    }

    public void setIdentificacionDestinatario(String identificacionDestinatario) {
        this.identificacionDestinatario = identificacionDestinatario;
    }

    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    public void setRazonSocialDestinatario(String razonSocialDestinatario) {
        this.razonSocialDestinatario = razonSocialDestinatario;
    }

    public String getDirDestinatario() {
        return dirDestinatario;
    }

    public void setDirDestinatario(String dirDestinatario) {
        this.dirDestinatario = dirDestinatario;
    }

    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(String motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }


    public String getDocAduaneroUnico() {
        return docAduaneroUnico;
    }

    public void setDocAduaneroUnico(String docAduaneroUnico) {
        this.docAduaneroUnico = docAduaneroUnico;
    }


    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    public void setCodEstabDestino(String docAduaneroUnico) {
        this.codEstabDestino = codEstabDestino;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }


    public String getCodDocSustento() {
        return codDocSustento;
    }

    public void setCodDocSustento(String codDocSustento) {
        this.codDocSustento = codDocSustento;
    }

    public String getNumDocSustento() {
        return numDocSustento;
    }

    public void setNumDocSustento(String numDocSustento) {
        this.numDocSustento = numDocSustento;
    }

    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    public void setNumAutDocSustento(String numAutDocSustento) {
        this.numAutDocSustento = numAutDocSustento;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }
}
