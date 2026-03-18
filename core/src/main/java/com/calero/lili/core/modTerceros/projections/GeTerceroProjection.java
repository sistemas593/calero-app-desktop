package com.calero.lili.core.modTerceros.projections;

import java.util.UUID;

public interface GeTerceroProjection {

    UUID getIdTercero();

    String getTipoIdentificacion();
    String getNumeroIdentificacion();

    String getTercero();

    String getWeb();

    String getObservaciones();

    String getTipoCliente();

    String getCiudad();
    String getDireccion();
    String getTelefonos();
    String getContacto();
    String getEmail();

}
