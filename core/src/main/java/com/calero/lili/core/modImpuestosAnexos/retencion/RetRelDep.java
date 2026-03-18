package com.calero.lili.core.modImpuestosAnexos.retencion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RetRelDep {

    @XmlElementWrapper(name = "retRelDep")
    @XmlElement(name = "datRetRelDep")
    private List<DatRetRelDep> datRetRelDepList;

}
