package com.calero.lili.core.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@XmlType(propOrder = {"tipoComprobanteReemb", "tpIdProvReemb", "idProvReemb",
        "establecimientoReemb", "puntoEmisionReemb", "secuencialReemb", "",
        "fechaEmisionReemb", "autorizacionReemb", "baseImponibleReemb", "baseImpGravReemb",
        "baseNoGraIvaReemb", "baseImpExeReemb", "montoIceRemb", "montoIvaRemb"})
@Builder
@Data
@AllArgsConstructor
public class Reembolso {

    private String tipoComprobanteReemb;
    private String tpIdProvReemb;
    private String idProvReemb;
    private String establecimientoReemb;
    private String puntoEmisionReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String autorizacionReemb;
    private String baseImponibleReemb;
    private String baseImpGravReemb;
    private String baseNoGraIvaReemb;
    private String baseImpExeReemb;
    private String montoIceRemb;
    private String montoIvaRemb;

}
