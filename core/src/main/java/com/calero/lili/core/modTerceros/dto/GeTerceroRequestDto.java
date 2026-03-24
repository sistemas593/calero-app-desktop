package com.calero.lili.core.modTerceros.dto;

import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.RequestTrabajadorDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeTerceroRequestDto {

    @NotEmpty(message = "No existe el nombre")
    private String tercero;

    private TipoIdentificacion tipoIdentificacion;

    private String numeroIdentificacion;

    private String web;

    private String observaciones;

    private String tipoClienteProveedor;
    private String relacionado;

    public String ciudad;
    public String direccion;
    public String telefonos;
    public String contacto;
    public String email;


    @NotNull(message = "Es requerido la información del cliente")
    private TipoTercerosClienteDto cliente;

    @NotNull(message = "Es requerido la información del proveedor")
    private TipoTercerosProveedorDto proveedor;

    @NotNull(message = "Es requerido la información del transportista")
    private TipoTercerosTransportistaDto transportista;

    @NotNull(message = "Es requerido la información del transportista")
    private TipoTerceroTrabajador trabajador;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TipoTercerosClienteDto {
        @NotEmpty(message = "Es requerido el parametro para saber si es cliente")
        private Boolean esCliente;
        private UUID idGrupoCliente;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TipoTercerosProveedorDto {
        @NotEmpty(message = "Es requerido el parametro para saber si es proveedor")
        private Boolean esProveedor;
        private UUID idGrupoProveedor;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TipoTercerosTransportistaDto {
        @NotEmpty(message = "Es requerido el parametro para saber si es proveedor")
        private Boolean esTransportista;
        private String placa;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TipoTerceroTrabajador {
        @NotEmpty(message = "Es requerido el parametro para saber si es proveedor")
        private Boolean esTrabajador;
        private RequestTrabajadorDto infoTrabajador;

    }

}
