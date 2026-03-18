package com.calero.lili.core.comprobantes.objetosXml.notaCredito;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador",
        "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial",
        "obligadoContabilidad", "codDocModificado", "numDocModificado", "fechaEmisionDocSustento",
        "totalSinImpuestos", "valorModificacion",
        "moneda",
        "totalImpuesto",
        "motivo",
})

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoNotaCredito {
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
    private String valorModificacion;
    private List<TotalImpuesto> totalImpuesto;
    private String moneda;
    private String motivo;


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

    public String getValorModificacion() {
        return valorModificacion;
    }

    public void setValorModificacion(String valorModificacion) {
        this.valorModificacion = valorModificacion;
    }


    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
