package com.calero.lili.core.modCargarExcelDP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorCargaDto {

    private int linea;
    private String identificacion;
    private String mensajeError;
}
