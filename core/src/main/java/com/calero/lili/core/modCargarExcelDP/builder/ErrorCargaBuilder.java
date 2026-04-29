package com.calero.lili.core.modCargarExcelDP.builder;

import com.calero.lili.core.modCargarExcelDP.dto.ErrorCargaDto;
import org.springframework.stereotype.Component;

@Component
public class ErrorCargaBuilder {

    public ErrorCargaDto builderError(Integer linea, String identificacion, String error) {

        return ErrorCargaDto.builder()
                .linea(linea)
                .identificacion(identificacion)
                .mensajeError(error)
                .build();
    }

}
