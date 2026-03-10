package com.calero.lili.api.modAdminUsuarios.adGrupos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdGruposRequestDto {

    @NotEmpty(message = "El nombre del grupo requerido")
    @NotNull(message = "El nombre del grupo es requerido")
    private String nombre;
    private String descripcion;

    private List<Permisos> permisos;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Permisos {
        private Long idPermiso;
        private String permiso;
    }

}
