package com.calero.lili.api.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@XmlType(propOrder = {"codSustento", "tpIdProv", "idProv",
        "tipoComprobante", "parteRel", "fechaRegistro",
        "establecimiento", "puntoEmision", "secuencial",
        "fechaEmision", "autorizacion", "baseNoGraIva",
        "baseImponible", "baseImpGrav", "baseImpExe",
        "montoIce", "montoIva", "valRetBien10", "valRetServ20",
        "valorRetBienes", "valRetServ50", "valorRetServicios",
        "valRetServ100", "totbasesImpReemb", "pagoExterior", "formasDePago", "detalleAir",
        "estabRetencion1", "ptoEmiRetencion1", "secRetencion1", "autRetencion1", "fechaEmiRet1"})
@Builder
@Data
@AllArgsConstructor
public class DetalleCompras {
    private String codSustento;
    private String tpIdProv;
    private String idProv;
    private String tipoComprobante;
    private String parteRel;
    private String fechaRegistro;
    private String establecimiento;
    private String puntoEmision;
    private String secuencial;
    private String fechaEmision;
    private String autorizacion;
    private BigDecimal baseNoGraIva;
    private BigDecimal baseImponible;
    private BigDecimal baseImpGrav;
    private BigDecimal baseImpExe;
    private BigDecimal montoIce;
    private BigDecimal montoIva;
    private BigDecimal valRetBien10;
    private BigDecimal valRetServ20;
    private BigDecimal valorRetBienes;
    private BigDecimal valRetServ50;
    private BigDecimal valorRetServicios;
    private BigDecimal valRetServ100;
    private BigDecimal totbasesImpReemb;
    private PagoExterior pagoExterior;
    private List<DetalleAir> detalleAir;
    private String estabRetencion1;
    private String ptoEmiRetencion1;
    private String secRetencion1;
    private String autRetencion1;
    private String fechaEmiRet1;
    private Pago formasDePago;


    @XmlElement(name = "pagoExterior") // envoltorio
    public PagoExterior getPagoExterior() {
        return pagoExterior;
    }

    @XmlElementWrapper(name = "air")// envoltorio
    public List<DetalleAir> getDetalleAir() {
        return detalleAir;
    }

    @XmlElement(name = "formasDePago")
    public Pago getFormasDePago() {
        return formasDePago;
    }

    public DetalleCompras() {
    }

}
