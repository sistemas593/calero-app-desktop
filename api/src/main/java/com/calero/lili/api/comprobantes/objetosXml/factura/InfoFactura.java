package com.calero.lili.api.comprobantes.objetosXml.factura;

import com.calero.lili.api.comprobantes.objetosXml.TotalImpuesto;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "contribuyenteEspecial",
        "obligadoContabilidad", "comercioExterior", "incoTermFactura",
        "lugarIncoTerm", "paisOrigen", "puertoEmbarque",
        "puertoDestino", "paisDestino", "paisAdquisicion",
        "tipoIdentificacionComprador", "guiaRemision", "razonSocialComprador",
        "identificacionComprador", "direccionComprador", "totalSinImpuestos",
        "incoTermTotalSinImpuestos", "totalDescuento", "codDocReembolso",
        "totalComprobantesReembolso","totalBaseImponibleReembolso", "totalImpuestoReembolso",
        "totalImpuesto", "propina", "fleteInternacional",
        "seguroInternacional", "gastosAduaneros", "gastosTransporteOtros",
        "importeTotal", "moneda", "pago"})
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InfoFactura {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String comercioExterior;
    private String incoTermFactura;
    private String lugarIncoTerm;
    private String paisOrigen;
    private String puertoEmbarque;
    private String puertoDestino;
    private String paisDestino;
    private String paisAdquisicion;
    private String tipoIdentificacionComprador;
    private String direccionComprador;
    private String guiaRemision;
    private String razonSocialComprador;
    private String identificacionComprador;
    private String totalSinImpuestos;
    private String incoTermTotalSinImpuestos;
    private String totalDescuento;
    private String codDocReembolso;
    private String totalComprobantesReembolso;
    private String totalBaseImponibleReembolso;
    private String totalImpuestoReembolso;
    private List<TotalImpuesto> totalImpuesto;
    private String propina;
    private String fleteInternacional;
    private String seguroInternacional;
    private String gastosAduaneros;
    private String gastosTransporteOtros;
    private String importeTotal;
    private String moneda;

    private List<Pago> pago;

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

    public String getComercioExterior() {
        return comercioExterior;
    }

    public void setComercioExterior(String comercioExterior) {
        this.comercioExterior = comercioExterior;
    }

    public String getIncoTermFactura() {
        return incoTermFactura;
    }

    public void setIncoTermFactura(String incoTermFactura) {
        this.incoTermFactura = incoTermFactura;
    }

    public String getLugarIncoTerm() {
        return lugarIncoTerm;
    }

    public void setLugarIncoTerm(String lugarIncoTerm) {
        this.lugarIncoTerm = lugarIncoTerm;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public String getPuertoEmbarque() {
        return puertoEmbarque;
    }

    public void setPuertoEmbarque(String puertoEmbarque) {
        this.puertoEmbarque = puertoEmbarque;
    }

    public String getPuertoDestino() {
        return puertoDestino;
    }

    public void setPuertoDestino(String puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public String getPaisDestino() {
        return paisDestino;
    }

    public void setPaisDestino(String paisDestino) {
        this.paisDestino = paisDestino;
    }

    public String getPaisAdquisicion() {
        return paisAdquisicion;
    }

    public void setPaisAdquisicion(String paisAdquisicion) {
        this.paisAdquisicion = paisAdquisicion;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    public String getDireccionComprador() {
        return direccionComprador;
    }

    public void setDireccionComprador(String direccionComprador) {
        this.direccionComprador = direccionComprador;
    }

    public String getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(String guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
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

    public String getPropina() {
        return propina;
    }

    public void setPropina(String propina) {
        this.propina = propina;
    }

    public String getFleteInternacional() {
        return fleteInternacional;
    }

    public void setFleteInternacional(String fleteInternacional) {
        this.fleteInternacional = fleteInternacional;
    }

    public String getSeguroInternacional() {
        return seguroInternacional;
    }

    public void setSeguroInternacional(String seguroInternacional) {
        this.seguroInternacional = seguroInternacional;
    }

    public String getGastosAduaneros() {
        return gastosAduaneros;
    }

    public void setGastosAduaneros(String gastosAduaneros) {
        this.gastosAduaneros = gastosAduaneros;
    }

    public String getGastosTransporteOtros() {
        return gastosTransporteOtros;
    }

    public void setGastosTransporteOtros(String gastosTransporteOtros) {
        this.gastosTransporteOtros = gastosTransporteOtros;
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

    public String getIncoTermTotalSinImpuestos() {
        return incoTermTotalSinImpuestos;
    }

    public void setIncoTermTotalSinImpuestos(String incoTermTotalSinImpuestos) {
        this.incoTermTotalSinImpuestos = incoTermTotalSinImpuestos;
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
