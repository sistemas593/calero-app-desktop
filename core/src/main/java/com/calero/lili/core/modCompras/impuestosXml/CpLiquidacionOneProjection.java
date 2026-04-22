package com.calero.lili.core.modCompras.impuestosXml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public interface CpLiquidacionOneProjection {

    String getIdData();

    void setIdData(Long idData);

    String getIdEmpresa();

    void setIdEmpresa(Long idEmpresa);

    String getIdSucursal();

    void setIdSucursal(String idSucursal);

    UUID getIdLiquidacion();

    void setIdLiquidacion(UUID idLiquidacion);

    UUID getIdTercero();

    void setIdTercero(UUID idTercero);

    String getTipoDocumento();

    void setTipoDocumento(String tipoDocumento);

    String getTipoIdentificacion();

    void setTipoIdentificacion(String tipoIdentificacion);

    String getNumeroIdentificacion();

    void setNumeroIdentificacion(String numeroIdentificacion);

    String getTipoProveedor();

    void setTipoProveedor(String tipoProveedor);

    Boolean getRelacionado();

    void setRelacionado(Boolean relacionado);

    LocalDate getFechaEmision();

    void setFechaEmision(LocalDate fechaEmision);

    String getFechaAutorizacion();

    void setFechaAutorizacion(LocalDate fechaAutorizacion);

    LocalDate getFechaRegistro();

    void setFechaRegistro(LocalDate fechaRegistro);

    String getCodigoDocumento();

    void setCodigoDocumento(String codigoDocumento);

    String getNumeroAutorizacion();

    void setNumeroAutorizacion(String numeroAutorizacion);

    String getSerie();

    void setSerie(String serie);

    String getSecuencial();

    void setSecuencial(String secuencial);

    LocalDate getFechaVencimiento();

    void setFechaVencimiento(LocalDate fechaVencimiento);

    String getConcepto();

    void setConcepto(String concepto);

    BigDecimal getOtros();

    void setOtros(BigDecimal otros);

    BigDecimal getCompensacionSolidaria();

    void setCompensacionSolidaria(BigDecimal compensacionSolidaria);

    String getCodigoSustento();

    void setCodigoSustento(String codigoSustento);

    String getDevolucionIva();

    void setDevolucionIva(String devolucionIva);

    String getReferencia();

    void setReferencia(String referencia);

    String getLiquidarNc();

    void setLiquidarNc(String liquidarNc);

    String getPagoLocExt();

    void setPagoLocExt(String pagoLocExt);

    String getTipoRegi();

    void setTipoRegi(String tipoRegi);

    String getPaisEfecPagoGen();

    void setPaisEfecPagoGen(String paisEfecPagoGen);

    String getPaisEfecPagoParFis();

    void setPaisEfecPagoParFis(String paisEfecPagoParFis);

    String getDenopagoRegFis();

    void setDenopagoRegFis(String denopagoRegFis);

    String getPaisEfecPago();

    void setPaisEfecPago(String paisEfecPago);

    Boolean getAplicConvDobTrib();

    void setAplicConvDobTrib(Boolean aplicConvDobTrib);

    Boolean getPagExtSujRetRorLeg();

    void setPagExtSujRetRorLeg(Boolean pagExtSujRetRorLeg);

    String getFormasPagoSri();

    void setFormasPagoSri(String formasPagoSri);

    Boolean getAnulada();

    void setAnulada(Boolean anulada);

    Long getIdRetencion();

    void setIdRetencion(Long idRetencion);

    BigDecimal getSubtotal();

    void setSubtotal(BigDecimal subtotal);

    BigDecimal getTotalDescuento();

    void setTotalDescuento(BigDecimal totalDescuento);

    BigDecimal getBaseCero();

    void setBaseCero(BigDecimal baseCero);

    BigDecimal getBaseNoObjeto();

    void setBaseNoObjeto(BigDecimal baseNoObjeto);

    BigDecimal getBaseExenta();

    void setBaseExenta(BigDecimal baseExenta);

    BigDecimal getBaseGravada();

    void setBaseGravada(BigDecimal baseGravada);

    BigDecimal getPorcentajeIva();

    void setPorcentajeIva(BigDecimal porcentajeIva);

    BigDecimal getValorIva();

    void setValorIva(BigDecimal valorIva);

    BigDecimal getBaseGravada5();

    void setBaseGravada5(BigDecimal baseGravada5);

    BigDecimal getValorIva5();

    void setValorIva5(BigDecimal valorIva5);

    BigDecimal getBaseGravada8();

    void setBaseGravada8(BigDecimal baseGravada8);

    BigDecimal getValorIva8();

    void setValorIva8(BigDecimal valorIva8);

    BigDecimal getTotal();

    void setTotal(BigDecimal total);

    String getDestino();

    void setDestino(BigDecimal destino);

    String getComprobante();

    String getEstadoDocumento();

    void setEstadoDocumento(String estadoDocumento);

    String getCodDocReembolso();

}

