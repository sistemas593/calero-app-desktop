package com.calero.lili.api.comprobantes.objetosXml.guiaRemision;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;

@XmlType(propOrder = {"dirEstablecimiento", "dirPartida",
        "razonSocialTransportista",
        "tipoIdentificacionTransportista",
        "rucTransportista",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "fechaIniTransporte",
        "fechaFinTransporte",
        "placa",
})
// ,"pago"

//"codDocModificado", "numDocModificado", "fechaEmisionDocSustento","valorModificacion"
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoGuiaRemision {
    private String dirEstablecimiento;
    private String dirPartida;
    private String contribuyenteEspecial;
    private String razonSocialTransportista;
    private String tipoIdentificacionTransportista;
    private String rucTransportista;
    ;
    private String obligadoContabilidad;
    private String fechaIniTransporte;
    private String fechaFinTransporte;
    private String placa;

//    private String codDocModificado;
//    private String numDocModificado;
//    private String valorModificacion;
//    private String fechaEmisionDocSustento;


    public String getDirEstablecimiento() {
        return dirEstablecimiento;
    }

    public void setDirEstablecimiento(String dirEstablecimiento) {
        this.dirEstablecimiento = dirEstablecimiento;
    }

    public String getDirPartida() {
        return dirPartida;
    }

    public void setDirPartida(String dirPartida) {
        this.dirPartida = dirPartida;
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = contribuyenteEspecial;
    }

    public String getRazonSocialTransportista() {
        return razonSocialTransportista;
    }

    public void setRazonSocialTransportista(String razonSocialTransportista) {
        this.razonSocialTransportista = razonSocialTransportista;
    }


    public String getTipoIdentificacionTransportista() {
        return tipoIdentificacionTransportista;
    }

    public void setTipoIdentificacionTransportista(String tipoIdentificacionTransportista) {
        this.tipoIdentificacionTransportista = tipoIdentificacionTransportista;
    }

    public String getRucTransportista() {
        return rucTransportista;
    }

    public void setRucTransportista(String rucTransportista) {
        this.rucTransportista = rucTransportista;
    }

    public String getObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(String obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getFechaIniTransporte() {
        return fechaIniTransporte;
    }

    public void setFechaIniTransporte(String fechaIniTransporte) {
        this.fechaIniTransporte = fechaIniTransporte;
    }

    public String getFechaFinTransporte() {
        return fechaFinTransporte;
    }

    public void setFechaFinTransporte(String fechaFinTransporte) {
        this.fechaFinTransporte = fechaFinTransporte;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

//    public String getCodDocModificado() {
//        return codDocModificado;
//    }
//
//    public void setCodDocModificado(String codDocModificado) {
//        this.codDocModificado = codDocModificado;
//    }
//
//    public String getNumDocModificado() {
//        return numDocModificado;
//    }
//
//    public void setNumDocModificado(String numDocModificado) {
//        this.numDocModificado = numDocModificado;
//    }

//    public String getFechaEmisionDocSustento() {
//        return fechaEmisionDocSustento;
//    }
//
//    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
//        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
//    }

//    public String getValorModificacion() {
//        return valorModificacion;
//    }
//
//    public void setValorModificacion(String valorModificacion) {
//        this.valorModificacion = valorModificacion;
//    }

}
