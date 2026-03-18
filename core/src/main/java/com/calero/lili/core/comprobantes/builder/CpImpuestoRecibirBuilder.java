package com.calero.lili.core.comprobantes.builder;

import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CpImpuestoRecibirBuilder {

    public CpImpuestosRecibirResponseDto builder(String nombre, String message, Boolean estado, String claveAcceso) {

        return CpImpuestosRecibirResponseDto.builder()
                .exitoso(estado ? "S" : "N")
                .archivo(nombre)
                .error(message)
                .claveAcceso(claveAcceso)
                .build();
    }


    public CpImpuestosRecibirListCreationResponseDto builderResponseList(List<CpImpuestosRecibirResponseDto> list) {
        return CpImpuestosRecibirListCreationResponseDto.builder()
                .resultados(list)
                .build();
    }
}
