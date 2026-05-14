package com.calero.lili.core.modTerceros.dto;

import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import com.calero.lili.core.enums.TipoClienteProveedor;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.ResponseTrabajadorDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeTerceroGetOneDto {

    private UUID idTercero;

    private String tercero;

    private String tipoIdentificacion;

    private String numeroIdentificacion;

    private String web;

    private String observaciones;

    private TipoClienteProveedor tipoClienteProveedor;

    public String ciudad;
    public String direccion;
    public String telefonos;
    public String contacto;
    public String email;

    private TipoTercerosClienteDto cliente;
    private TipoTercerosProveedorDto proveedor;
    private TipoTercerosTransportistaDto transportista;
    private TipoTercerosTrabajadorDto trabajador;


    private SexoEnum sexo;
    private OrigenIngresosEnum origenIngresos;
    private EstadoCivilEnum estadoCivil;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TipoTercerosClienteDto {
        private Boolean esCliente;
        private UUID idGrupoCliente;
        private String grupo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TipoTercerosProveedorDto {
        private Boolean esProveedor;
        private UUID idGrupoProveedor;
        private String grupo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TipoTercerosTransportistaDto {
        private Boolean esTransportista;
        private String placa;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TipoTercerosTrabajadorDto {
        private Boolean esTrabajador;
        private ResponseTrabajadorDto infoTrabajador;

    }

    private String codigoParroquia;
    private String parroquia;
    private String codigoCanton;
    private String canton;
    private String codigoProvincia;
    private String provincia;
    private String codigoPais;
    private String pais;

}
