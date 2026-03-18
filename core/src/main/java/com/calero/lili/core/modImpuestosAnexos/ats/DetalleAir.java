package com.calero.lili.core.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@XmlType(propOrder = {"codRetAir", "baseImpAir", "porcentajeAir", "valRetAir"})
@Data
@Builder
@AllArgsConstructor
public class DetalleAir {

    public DetalleAir(){}

    private String codRetAir;
    private BigDecimal baseImpAir;
    private BigDecimal porcentajeAir;
    private BigDecimal valRetAir;

}
