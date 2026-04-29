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
@XmlType(propOrder = {"detallePasivo"})

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pasivo {

    @XmlElement(name = "detallePasivo")
    private List<DetallePasivo> detallePasivo;

    public List<DetallePasivo> getDetallePasivo() {
        return detallePasivo;
    }

    public void setDetallePasivo(List<DetallePasivo> detallePasivo) {
        this.detallePasivo = detallePasivo;
    }
}
