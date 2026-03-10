package com.calero.lili.api.comprobantes.objetosXml.comprobanteRetencion;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;

@XmlType(propOrder = {"codigo", "codigoRetencion","baseImponible", "porcentajeRetener",  "valorRetenido"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Retencion {
    private String codigo;
    private String codigoRetencion;
    private String baseImponible;
    private String porcentajeRetener;
    private String valorRetenido;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoRetencion() {
        return codigoRetencion;
    }

    public void setCodigoRetencion(String codigoRetencion) {
        this.codigoRetencion = codigoRetencion;
    }

    public String getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(String baseImponible) {
        this.baseImponible = baseImponible;
    }

    public String getPorcentajeRetener() {
        return porcentajeRetener;
    }

    public void setPorcentajeRetener(String porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }

    public String getValorRetenido() {
        return valorRetenido;
    }

    public void setValorRetenido(String valorRetenido) {
        this.valorRetenido = valorRetenido;
    }
}
