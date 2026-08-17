package com.calero.lili.core.modCompras.service;


import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.PagoLocalExterior;
import com.calero.lili.core.enums.SustentoCodigos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface CpImpuestoAtsProjection {

    UUID getIdImpuestos();

    SustentoCodigos getCodigoSustento();

    String getTipoIdProv();

    String getIdProv();

    DocumentoEnum getCodigoDocumento();

    LocalDate getFechaRegistro();

    String getSerie();

    String getSecuencial();

    LocalDate getFechaEmision();

    String getAutorizacion();

    BigDecimal getBaseNoGraIva();

    BigDecimal getBaseImponible();

    BigDecimal getBaseImpGrav();

    BigDecimal getBaseImpExe();

    BigDecimal getMontoIva();

    PagoLocalExterior getPagoLocExt();

    String getPagoExterior();

    String getFormasPago();

    UUID getIdRetencion();

    DocumentoEnum getModCodigoDocumento();

    String getModSerie();

    String getModSecuencial();

    LocalDate getModFechaEmision();

    String getModNumAutorizacion();




}
