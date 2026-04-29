package com.calero.lili.core.comprobantes.objetosXml.ctasXCobrar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"detalleCtasXCobrar"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CtasXCobrar {

    @XmlElement(name = "detalleCtasXCobrar")
    List<DetalleCtasXCobrar> detalleCtasXCobrar;

    public List<DetalleCtasXCobrar> getDetalleCtasXCobrar() {
        return detalleCtasXCobrar;
    }

    public void setDetalleCtasXCobrar(List<DetalleCtasXCobrar> detalleCtasXCobrar) {
        this.detalleCtasXCobrar = detalleCtasXCobrar;
    }
}
