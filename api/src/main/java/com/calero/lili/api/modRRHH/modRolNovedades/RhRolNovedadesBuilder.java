package com.calero.lili.api.modRRHH.modRolNovedades;

import com.calero.lili.api.modRRHH.modRRHHRublos.RubrosEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RhRolNovedadesBuilder {


    public RhRolNovedadesEntity builderRolNovedades(Long idData, Long idEmpresa,
                                                    RubrosEntity rubros, GeTerceroEntity tercero) {
        return RhRolNovedadesEntity.builder()
                .idNovedad(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .rubros(rubros)
                .tercero(tercero)
                .build();
    }


}
