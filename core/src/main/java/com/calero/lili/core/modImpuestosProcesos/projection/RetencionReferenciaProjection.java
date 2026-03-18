package com.calero.lili.core.modImpuestosProcesos.projection;


import java.time.LocalDate;
import java.util.UUID;

public interface RetencionReferenciaProjection {

    UUID getIdReferencia();

    UUID getIdRetencion();

    String getSerie();

    String getSecuencial();

    String getNumeroIdentificacion();

    String getCodigoDocumento();

    String getImpuestosCodigos();

    LocalDate getFechaRegistro();

}
