package com.calero.lili.api.comprobantes.objetosXml.factura;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlType(propOrder = {"motivoTraslado","codEstabDestino","ruta"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Destino {
    private String motivoTraslado;
    private String ruta;
    private String codEstabDestino;

    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(String motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    public void setCodEstabDestino(String codEstabDestino) {
        this.codEstabDestino = codEstabDestino;
    }
}
