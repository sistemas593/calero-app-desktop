package com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "decPat")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"ctasXCobrar", "pasivo"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecPat {

    @XmlElement(name = "ctasXCobrar")
    private CtasXCobrar ctasXCobrar;

    @XmlElement(name = "pasivo")
    private Pasivo pasivo;

    @XmlAttribute(name = "version")
    private String version;

    public CtasXCobrar getCtasXCobrar() {
        return ctasXCobrar;
    }

    public void setCtasXCobrar(CtasXCobrar ctasXCobrar) {
        this.ctasXCobrar = ctasXCobrar;
    }

    public Pasivo getPasivo() {
        return pasivo;
    }

    public void setPasivo(Pasivo pasivo) {
        this.pasivo = pasivo;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
