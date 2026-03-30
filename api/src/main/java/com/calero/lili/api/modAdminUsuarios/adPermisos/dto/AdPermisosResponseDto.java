package com.calero.lili.api.modAdminUsuarios.adPermisos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdPermisosResponseDto {

    private Long idPermiso;
    private String nombre;
    private String permiso;

}
