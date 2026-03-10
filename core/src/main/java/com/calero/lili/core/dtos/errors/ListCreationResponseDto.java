package com.calero.lili.core.dtos.errors;

import lombok.Data;

import java.util.List;

@Data
public class ListCreationResponseDto {

    private String respuesta;

    private List<DetallesErrores> detallesErrores;

}
