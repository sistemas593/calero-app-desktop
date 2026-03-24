package com.calero.lili.api.modAdminUsuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdUsuarioPermisosDtoResponse {

    private List<Roles> roles;
    private List<String> permisos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Roles {
        private String rol;
    }



}
