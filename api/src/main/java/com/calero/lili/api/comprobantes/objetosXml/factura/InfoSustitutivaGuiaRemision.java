package com.calero.lili.api.comprobantes.objetosXml.factura;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlType(propOrder = {"dirPartida","dirDestinatario","fechaIniTransporte","fechaFinTransporte","razonSocialTransportista","tipoIdentificacionTransportista","rucTransportista","placa", "destino"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InfoSustitutivaGuiaRemision {
    private String dirPartida;
    private String dirDestinatario;
    private String fechaIniTransporte;
    private String fechaFinTransporte;
    private String razonSocialTransportista;
    private String tipoIdentificacionTransportista;
    private String rucTransportista;
    private String placa;

    private List<Destino> destino;

    public String getDirPartida() {
        return dirPartida;
    }

    public void setDirPartida(String dirPartida) {
        this.dirPartida = dirPartida;
    }

    public String getDirDestinatario() {
        return dirDestinatario;
    }

    public void setDirDestinatario(String dirDestinatario) {
        this.dirDestinatario = dirDestinatario;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    @XmlElementWrapper(name = "destinos") // envoltorio
    public List<Destino> getDestino() {
        return destino;
    }
    public void setDestino(List<Destino> destino) {
        this.destino = destino;
    }
}
