package com.calero.lili.core.modCompras.service;

import java.time.LocalDate;
import java.util.UUID;

public interface CpRetencionesProjection {


    UUID getIdRetencion();

    String getSerie();

    String getSecuencial();

    String getAutorizacion();

    LocalDate getFechaRetencion();
}
