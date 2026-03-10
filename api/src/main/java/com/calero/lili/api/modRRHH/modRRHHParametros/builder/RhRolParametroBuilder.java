package com.calero.lili.api.modRRHH.modRRHHParametros.builder;

import com.calero.lili.api.modRRHH.modRRHHParametros.RhRolParametrosEntity;
import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RhRolParametroBuilder {


    public RhRolParametrosEntity builderNewParametro(GeTerceroEntity tercero, RubrosEntity rubro) {
        return RhRolParametrosEntity.builder()
                .idParametro(UUID.randomUUID())
                .rubros(rubro)
                .tercero(tercero)
                .build();
    }

}
