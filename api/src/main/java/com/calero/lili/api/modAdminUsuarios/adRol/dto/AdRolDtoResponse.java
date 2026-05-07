package com.calero.lili.api.modAdminUsuarios.adRol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdRolDtoResponse {

    private Long idRol;
    private String nombre;
}
