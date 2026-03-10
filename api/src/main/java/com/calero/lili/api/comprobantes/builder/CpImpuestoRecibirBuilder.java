package com.calero.lili.api.comprobantes.builder;

import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
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
