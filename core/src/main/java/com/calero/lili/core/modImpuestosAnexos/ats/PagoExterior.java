package com.calero.lili.core.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@XmlType(propOrder = {"pagoLocExt", "tipoRegi" ,"paisEfecPagoGen", "paisEfecPagoParFis", "denopagoRegFis", "paisEfecPago", "aplicConvDobTrib", "pagExtSujRetNorLeg", "pagoRegFis"})
@Builder
@Data
@AllArgsConstructor
public class PagoExterior {


    public PagoExterior() {
    }

    private String pagoLocExt;
    private String paisEfecPago;
    private String aplicConvDobTrib;
    private String pagExtSujRetNorLeg;
    private String tipoRegi;
    private String paisEfecPagoGen;
    private String paisEfecPagoParFis;
    private String denopagoRegFis;
    private String pagoRegFis;
}
