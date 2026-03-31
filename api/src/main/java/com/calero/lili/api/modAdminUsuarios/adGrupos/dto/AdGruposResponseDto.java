package com.calero.lili.api.modAdminUsuarios.adGrupos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdGruposResponseDto {

    private Long idGrupoPermiso;
    private String nombre;
    private List<Permisos> permisos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Permisos {
        private Long idPermiso;
        private String nombre;
        private String permiso;

    }


}
