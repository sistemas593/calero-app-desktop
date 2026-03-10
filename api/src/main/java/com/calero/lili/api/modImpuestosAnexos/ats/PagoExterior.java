package com.calero.lili.api.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@XmlType(propOrder = {"pagoLocExt", "paisEfecPago", "aplicConvDobTrib", "pagExtSujRetNorLeg"})
@Builder
@Data
@AllArgsConstructor
public class PagoExterior {


    public PagoExterior(){}

    private String pagoLocExt;
    private String paisEfecPago;
    private String aplicConvDobTrib;
    private String pagExtSujRetNorLeg;
}
