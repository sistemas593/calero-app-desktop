package com.calero.lili.core.modContabilidad.modSecuenciales.projection;

import java.util.UUID;

public interface CnSecuenciasProjection {

    UUID getIdSecuencia();
    Integer getUltimoNumero();

}
