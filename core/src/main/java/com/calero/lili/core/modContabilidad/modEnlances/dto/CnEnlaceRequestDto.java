package com.calero.lili.core.modContabilidad.modEnlances.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class CnEnlaceRequestDto {

    private UUID idCuenta;
    private String detalle;
    private String codigo;

}
