package com.calero.lili.core.modTerceros.dto;

import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private SexoEnum sexo;
    private OrigenIngresosEnum origenIngresos;
    private EstadoCivilEnum estadoCivil;

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
