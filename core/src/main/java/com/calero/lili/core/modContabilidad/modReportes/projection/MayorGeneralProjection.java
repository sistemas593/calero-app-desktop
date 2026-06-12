package com.calero.lili.core.modContabilidad.modReportes.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface MayorGeneralProjection {

    UUID getIdAsiento();

    LocalDate getFechaAsiento();

    String getCuenta();

    String getTipoAsiento();

    String getNumeroAsiento();

    String getTipoDocumento();

    String getNumeroDocumento();

    String getConcepto();

    BigDecimal getDebe();

    BigDecimal getHaber();

    BigDecimal getSaldoAcumulado();

    String getCodigoCentroCostos();

    String getCentroCostos();

    UUID getIdTercero();

    String getNumeroIdentificacion();

    String getTercero();

    UUID getIdItem();

    String getDescripcion();

    String getCodigoPrincipal();
}
