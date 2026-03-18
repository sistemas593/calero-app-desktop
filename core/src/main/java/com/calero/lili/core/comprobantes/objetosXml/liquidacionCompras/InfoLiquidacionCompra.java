package com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad",
        "tipoIdentificacionProveedor", "razonSocialProveedor", "identificacionProveedor",
        "direccionProveedor", "totalSinImpuestos", "totalDescuento",
        "codDocReembolso","totalComprobantesReembolso","totalBaseImponibleReembolso","totalImpuestoReembolso",
        "totalImpuesto", "importeTotal", "moneda", "pago",})

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoLiquidacionCompra {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String tipoIdentificacionProveedor;
    private String razonSocialProveedor;
    private String identificacionProveedor;
    private String direccionProveedor;
    private String totalSinImpuestos;
    private String totalDescuento;
    private List<TotalImpuesto> totalImpuesto;
    private String importeTotal;
    private String moneda;
    private List<Pago> pago;
    private String codDocReembolso;
    private String totalComprobantesReembolso;
    private String totalBaseImponibleReembolso;
    private String totalImpuestoReembolso;


    @XmlElementWrapper(name = "pagos") // envoltorio
    public List<Pago> getPago() {
        return pago;
    }

    public void setPago(List<Pago> pago) {
        this.pago = pago;
    }


    @XmlElementWrapper(name = "totalConImpuestos") // envoltorio
    public List<TotalImpuesto> getTotalImpuesto() {
        return totalImpuesto;
    }

    public void setTotalImpuesto(List<TotalImpuesto> totalImpuesto) {
        this.totalImpuesto = totalImpuesto;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getDirEstablecimiento() {
        return dirEstablecimiento;
    }

    public void setDirEstablecimiento(String dirEstablecimiento) {
        this.dirEstablecimiento = dirEstablecimiento;
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = contribuyenteEspecial;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getTipoIdentificacionProveedor() {
        return tipoIdentificacionProveedor;
    }

    public void setTipoIdentificacionProveedor(String tipoIdentificacionProveedor) {
        this.tipoIdentificacionProveedor = tipoIdentificacionProveedor;
    }

    public String getRazonSocialProveedor() {
        return razonSocialProveedor;
    }

    public void setRazonSocialProveedor(String razonSocialProveedor) {
        this.razonSocialProveedor = razonSocialProveedor;
    }

    public String getIdentificacionProveedor() {
        return identificacionProveedor;
    }

    public void setIdentificacionProveedor(String identificacionProveedor) {
        this.identificacionProveedor = identificacionProveedor;
    }

    public String getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(String totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }

    public String getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(String totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public String getDireccionProveedor() {
        return direccionProveedor;
    }

    public void setDireccionProveedor(String direccionProveedor) {
        this.direccionProveedor = direccionProveedor;
    }

    public String getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(String importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCodDocReembolso() {
        return codDocReembolso;
    }

    public void setCodDocReembolso(String codDocReembolso) {
        this.codDocReembolso = codDocReembolso;
    }

    public String getTotalComprobantesReembolso() {
        return totalComprobantesReembolso;
    }

    public void setTotalComprobantesReembolso(String totalComprobantesReembolso) {
        this.totalComprobantesReembolso = totalComprobantesReembolso;
    }

    public String getTotalBaseImponibleReembolso() {
        return totalBaseImponibleReembolso;
    }

    public void setTotalBaseImponibleReembolso(String totalBaseImponibleReembolso) {
        this.totalBaseImponibleReembolso = totalBaseImponibleReembolso;
    }

    public String getTotalImpuestoReembolso() {
        return totalImpuestoReembolso;
    }

    public void setTotalImpuestoReembolso(String totalImpuestoReembolso) {
        this.totalImpuestoReembolso = totalImpuestoReembolso;
    }



}
