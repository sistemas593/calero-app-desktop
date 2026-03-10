package com.calero.lili.api.adDatasUsuarios.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdDataUsuarioCreationResponseDto {

    private UUID idRegistro;
    private Long idData;
    private Long idUsuario;

}
