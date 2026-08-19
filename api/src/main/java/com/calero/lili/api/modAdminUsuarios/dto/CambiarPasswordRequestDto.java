package com.calero.lili.api.modAdminUsuarios.dto;

import lombok.Data;

@Data
public class CambiarPasswordRequestDto {

    private String passwordActual;
    private String passwordNueva;

}
