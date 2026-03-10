package com.calero.lili.api.modCompras.modComprasRetenciones.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface OneProjection {

    UUID getIdRetencion();
    String getSerie();
    String getSecuencial();
    String getNumeroAutorizacion();

    LocalDate getFechaEmision();

    String getInformacionAdicional();

    String getFormasPagoSri();
    String getExportacion();

    UUID getIdTercero();
    String getTerceroDatos();

    String getTerceroNombre();
    String getTipoIdentificacion();
    String getNumeroIdentificacion();
    String getEmail();

}
