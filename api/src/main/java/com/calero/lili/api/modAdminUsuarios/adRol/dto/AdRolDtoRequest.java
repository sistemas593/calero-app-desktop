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
public class AdRolDtoRequest {

    private String nombre;

    private List<Grupo> grupos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Grupo {
        private Long idGrupo;
        private String nombre;
    }

}