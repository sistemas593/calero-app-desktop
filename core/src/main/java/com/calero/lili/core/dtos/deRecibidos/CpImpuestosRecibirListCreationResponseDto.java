package com.calero.lili.core.dtos.deRecibidos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CpImpuestosRecibirListCreationResponseDto {

    private List<CpImpuestosRecibirResponseDto> resultados;

}
