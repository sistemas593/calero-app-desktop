package com.calero.lili.core.comprobantes.objetosXml.notaDebito;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador", "razonSocialComprador",
        "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "codDocModificado",
        "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "impuesto",
        "valorTotal"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoNotaDebito {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String codDocModificado;
    private String numDocModificado;
    private String fechaEmisionDocSustento;
    private String tipoIdentificacionComprador;
    private String razonSocialComprador;
    private String identificacionComprador;
    private String totalSinImpuestos;
    private List<Impuesto> impuesto;
    private String valorTotal;


    @XmlElementWrapper(name = "impuestos") // envoltorio
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(List<Impuesto> impuesto) {
        this.impuesto = impuesto;
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

    public String getCodDocModificado() {
        return codDocModificado;
    }

    public void setCodDocModificado(String codDocModificado) {
        this.codDocModificado = codDocModificado;
    }

    public String getNumDocModificado() {
        return numDocModificado;
    }

    public void setNumDocModificado(String numDocModificado) {
        this.numDocModificado = numDocModificado;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
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


    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }


}