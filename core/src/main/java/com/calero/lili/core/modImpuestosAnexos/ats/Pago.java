package com.calero.lili.core.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@XmlType(propOrder = {"formaPago"})
@Builder
@Data
@AllArgsConstructor
public class Pago {
    private List<String> formaPago;
}
