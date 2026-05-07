package com.calero.lili.api.modAdminUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AdUsuarioRequestDto {
    private String idArea;
    private Long idData;
    private Long idUsuario;
    private String username;
    private String email;
    private String password;
    private boolean admin;
    private int nivel;

    private List<Roles> roles;
    private List<Grupos> grupos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Roles {
        private Long idRol;
        private String rol;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Grupos {
        private Long idGrupo;
        private String grupo;
    }

}
