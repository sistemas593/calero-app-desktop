package com.calero.lili.core.modImpuestosAnexos.retencion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@XmlRootElement(name = "rdep")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rdep {


    public Rdep(){}

    private String numRuc;
    private String anio;
    private String tipoEmpleador;
    private String enteSegSocial;
    @XmlElement(name = "retRelDep")
    private RetRelDep retRelDep;

}
