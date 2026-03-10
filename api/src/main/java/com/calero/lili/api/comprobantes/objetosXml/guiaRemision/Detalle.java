package com.calero.lili.api.comprobantes.objetosXml.guiaRemision;

import com.calero.lili.api.comprobantes.objetosXml.factura.DetAdicional;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"codigoInterno", "codigoAdicional", "descripcion", "cantidad", "detAdicional"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Detalle {
    private String codigoInterno;
    private String codigoAdicional;

    private String descripcion;
    private String cantidad;
    private List<DetAdicional> detAdicional;
//    private List<Impuesto> impuesto;

    @XmlElementWrapper(name = "detallesAdicionales") // envoltorio
    public List<DetAdicional> getDetAdicional() {
        return detAdicional;
    }

    public void setDetAdicional(List<DetAdicional> detAdicional) {
        this.detAdicional = detAdicional;
    }

//    @XmlElementWrapper(name = "impuestos") // envoltorio
//    public List<Impuesto> getImpuesto() {
//        return impuesto;
//    }
//
//    public void setImpuesto(List<Impuesto> impuesto) {
//        this.impuesto = impuesto;
//    }
    
    public String getCodigoInterno() {
        return codigoInterno;
    }
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getCodigoAdicional() {
        return codigoAdicional;
    }
    public void setCodigoAdicional(String codigoAdicional) {
        this.codigoAdicional = codigoAdicional;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    
}
