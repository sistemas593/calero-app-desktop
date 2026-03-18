package com.calero.lili.core.modClientesConfiguraciones.dto;

import lombok.Data;

import java.util.List;

@Data
public class StEmpresasListCreationResponseDto {

    private List<VtClientesConfiguracionesCreationResponseDto> errores;

}
