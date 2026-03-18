package com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;

@XmlType(propOrder = {"codImpuestoDocSustento", "codigoPorcentaje","baseImponible", "tarifa",  "valorImpuesto"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImpuestoDocSustento {
    private String codImpuestoDocSustento;
    private String codigoPorcentaje;
    private String baseImponible;
    private String tarifa;
    private String valorImpuesto;

    public String getCodImpuestoDocSustento() {
        return codImpuestoDocSustento;
    }

    public void setCodImpuestoDocSustento(String codImpuestoDocSustento) {
        this.codImpuestoDocSustento = codImpuestoDocSustento;
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public void setCodigoPorcentaje(String codigoPorcentaje) {
        this.codigoPorcentaje = codigoPorcentaje;
    }

    public String getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(String baseImponible) {
        this.baseImponible = baseImponible;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public String getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(String valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }
}
