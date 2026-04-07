package com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion;

import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"codSustento", "codDocSustento", "numDocSustento", "fechaEmisionDocSustento", "fechaRegistroContable", "numAutDocSustento", "pagoLocExt", "tipoRegi", "paisEfecPago",
        "aplicConvDobTrib", "pagExtSujRetNorLeg", "pagoRegFis", "totalComprobantesReembolso", "totalBaseImponibleReembolso", "totalImpuestoReembolso", "totalSinImpuestos", "importeTotal", "impuestoDocSustento", "retencion", "pago"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocSustento {
    private String codSustento;
    private String codDocSustento;
    private String numDocSustento;
    private String fechaEmisionDocSustento;
    private String fechaRegistroContable;
    private String numAutDocSustento;
    private String pagoLocExt;
    private String tipoRegi;
    private String paisEfecPago;
    private String aplicConvDobTrib;
    private String pagExtSujRetNorLeg;
    private String pagoRegFis;

    private String totalComprobantesReembolso;
    private String totalBaseImponibleReembolso;
    private String totalImpuestoReembolso;
    private String totalSinImpuestos;
    private String importeTotal;
    private List<ImpuestoDocSustento> impuestoDocSustento;
    private List<Retencion> retencion;
    private List<Pago> pago;


    @XmlElementWrapper(name = "impuestosDocSustento") // envoltorio
    public List<ImpuestoDocSustento> getImpuestoDocSustento() {
        return impuestoDocSustento;
    }

    public void setImpuestoDocSustento(List<ImpuestoDocSustento> impuestoDocSustento) {
        this.impuestoDocSustento = impuestoDocSustento;
    }

    @XmlElementWrapper(name = "retenciones") // envoltorio
    public List<Retencion> getRetencion() {
        return retencion;
    }

    public void setRetencion(List<Retencion> retencion) {
        this.retencion = retencion;
    }

    public String getCodSustento() {
        return codSustento;
    }

    public void setCodSustento(String codSustento) {
        this.codSustento = codSustento;
    }

    public String getCodDocSustento() {
        return codDocSustento;
    }

    public void setCodDocSustento(String codDocSustento) {
        this.codDocSustento = codDocSustento;
    }

    public String getNumDocSustento() {
        return numDocSustento;
    }

    public void setNumDocSustento(String numDocSustento) {
        this.numDocSustento = numDocSustento;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getFechaRegistroContable() {
        return fechaRegistroContable;
    }

    public void setFechaRegistroContable(String fechaRegistroContable) {
        this.fechaRegistroContable = fechaRegistroContable;
    }

    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    public void setNumAutDocSustento(String numAutDocSustento) {
        this.numAutDocSustento = numAutDocSustento;
    }

    public String getPagoLocExt() {
        return pagoLocExt;
    }

    public void setPagoLocExt(String pagoLocExt) {
        this.pagoLocExt = pagoLocExt;
    }

    public String getPaisEfecPago() {
        return paisEfecPago;
    }

    public void setPaisEfecPago(String paisEfecPago) {
        this.paisEfecPago = paisEfecPago;
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

    public String getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(String totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }

    public String getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(String importeTotal) {
        this.importeTotal = importeTotal;
    }

    @XmlElementWrapper(name = "pagos") // envoltorio
    public List<Pago> getPago() {
        return pago;
    }

    public void setPago(List<Pago> pago) {
        this.pago = pago;
    }
}
