package com.calero.lili.core.modTerceros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GeTerceroGetListDto {

    private UUID idTercero;

    private String tercero;

    private String tipoIdentificacion;

    private String numeroIdentificacion;

    private String web;

    private String observaciones;

    private UUID idGrupo;

    private String tipoClienteProveedor;

    public String ciudad;
    public String direccion;
    public String telefonos;
    public String contacto;
    public String email;

    public String placa; // setear la placa

    private List<TipoTercerosDto> tipoTerceros;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TipoTercerosDto {
        private UUID idTipoTercero;
        private String tipoTercero;
    }

}
