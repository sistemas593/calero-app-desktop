package com.calero.lili.core.modRRHH.modRRHHParametros.builder;

import com.calero.lili.core.modRRHH.modRRHHParametros.RhRolParametrosEntity;
import com.calero.lili.core.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
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
