package com.calero.lili.api.dtos.deRecibidos;

import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CpImpuestosRecibirListCreationResponseDto {

    private List<CpImpuestosRecibirResponseDto> resultados;

}
