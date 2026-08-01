package com.calero.lili.core.modCompras.service;

import java.time.LocalDate;

public interface CpRetencionesProjection {

    String getSerie();

    String getSecuencial();

    String getAutorizacion();

    LocalDate getFechaRetencion();
}
