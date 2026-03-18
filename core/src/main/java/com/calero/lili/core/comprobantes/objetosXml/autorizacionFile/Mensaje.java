package com.calero.lili.core.comprobantes.objetosXml.autorizacionFile;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"identificador", "mensaje", "tipo"})

public class Mensaje {
    private String identificador;
    private String mensaje;
    private String tipo;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


}
