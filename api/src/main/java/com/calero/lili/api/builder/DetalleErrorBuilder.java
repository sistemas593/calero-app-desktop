package com.calero.lili.api.builder;


import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import org.springframework.stereotype.Component;

@Component
public class DetalleErrorBuilder {

    public DetalleError builderDetalleError(int linea, EnumError enumError){
            return DetalleError.builder()
                    .linea(linea)
                    .type(enumError)
                    .build();
    }
}
