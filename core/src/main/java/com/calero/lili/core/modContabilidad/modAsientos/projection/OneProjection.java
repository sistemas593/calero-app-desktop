package com.calero.lili.core.modContabilidad.modAsientos.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface OneProjection {

    UUID getIdVenta();
    String getTipoVenta();
    String getSerie();
    String getSecuencial();
    String getNumeroAutorizacion();

    LocalDate getFechaEmision();

    BigDecimal getSubtotal();
    BigDecimal getTotalDescuento();

    BigDecimal getBaseCero();
    BigDecimal getBaseExenta();
    BigDecimal getBaseNoObjeto();
    BigDecimal getBaseGravada();
    BigDecimal getPorcentajeIva();
    BigDecimal getValorIva();

    BigDecimal getBaseGravada5();
    BigDecimal getValorIva5();

    BigDecimal getBaseGravada8();
    BigDecimal getValorIva8();

    String getInformacionAdicional();

    String getFormasPagoSri();
    String getExportacion();

    UUID getIdTercero();
    String getTerceroDatos();

    String getTerceroNombre();
    String getTipoIdentificacion();
    String getNumeroIdentificacion();
    String getEmail();
    String getTipoCliente();
    String getRelacionado();



}
