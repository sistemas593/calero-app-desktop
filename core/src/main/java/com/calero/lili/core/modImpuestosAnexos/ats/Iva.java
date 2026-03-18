package com.calero.lili.core.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement // DEFINE RAIZ DEL DOC XML
@XmlType(propOrder = {"tipoIDInformante", "idInformante", "razonSocial", "anio", "mes", "numEstabRuc", "totalVentas", "codigoOperativo", "detalleCompras"})
@Builder
@Data
@AllArgsConstructor
public class Iva {

    public Iva() {}

    private String TipoIDInformante;

    private String IdInformante;

    private String razonSocial;

    private String Anio;

    private String Mes;

    private String numEstabRuc;

    private BigDecimal totalVentas;

    private String codigoOperativo;

    private List<DetalleCompras> detalleCompras;

    @XmlElementWrapper(name = "compras")
    @XmlElement(name = "detalleCompras")
    public List<DetalleCompras> getDetalleCompras() {
        return detalleCompras;
    }


    @XmlElement(name = "TipoIDInformante")
    public String getTipoIDInformante() {
        return TipoIDInformante;
    }

    @XmlElement(name = "IdInformante")
    public String getIdInformante() {
        return IdInformante;
    }

    @XmlElement(name = "razonSocial")
    public String getRazonSocial() {
        return razonSocial;
    }

    @XmlElement(name = "Anio")
    public String getAnio() {
        return Anio;
    }

    @XmlElement(name = "Mes")
    public String getMes() {
        return Mes;
    }

    @XmlElement(name = "numEstabRuc")
    public String getNumEstabRuc() {
        return numEstabRuc;
    }

    @XmlElement(name = "totalVentas")
    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    @XmlElement(name = "codigoOperativo")
    public String getCodigoOperativo() {
        return codigoOperativo;
    }
}
