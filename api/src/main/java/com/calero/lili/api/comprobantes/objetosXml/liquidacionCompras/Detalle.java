package com.calero.lili.api.comprobantes.objetosXml.liquidacionCompras;

import com.calero.lili.api.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.api.comprobantes.objetosXml.factura.Impuesto;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"codigoPrincipal", "descripcion", "cantidad", "precioUnitario", "descuento", "precioTotalSinImpuesto", "detAdicional", "impuesto"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Detalle {
    private String codigoPrincipal;
    private String descripcion;
    private String cantidad;
    private String precioUnitario;
    private String descuento;
    private String precioTotalSinImpuesto;
    private List<DetAdicional> detAdicional;
    private List<Impuesto> impuesto;

    @XmlElementWrapper(name = "detallesAdicionales") // envoltorio
    public List<DetAdicional> getDetAdicional() {
        return detAdicional;
    }

    public void setDetAdicional(List<DetAdicional> detAdicional) {
        this.detAdicional = detAdicional;
    }
    
    @XmlElementWrapper(name = "impuestos") // envoltorio
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(List<Impuesto> impuesto) {
        this.impuesto = impuesto;
    }


    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
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

    public String getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(String precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getPrecioTotalSinImpuesto() {
        return precioTotalSinImpuesto;
    }

    public void setPrecioTotalSinImpuesto(String precioTotalSinImpuesto) {
        this.precioTotalSinImpuesto = precioTotalSinImpuesto;
    }
    
}
