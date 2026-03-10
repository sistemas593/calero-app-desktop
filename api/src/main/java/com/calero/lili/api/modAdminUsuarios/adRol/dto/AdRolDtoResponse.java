package com.calero.lili.api.modAdminUsuarios.adRol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdRolDtoResponse {

    private Long idRol;
    private String nombre;

    private List<Grupos> grupos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Grupos {
        private Long idGrupo;
        private String grupo;
    }

}
