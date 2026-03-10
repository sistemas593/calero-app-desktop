package com.calero.lili.core.modAdminEmpresasSucursales.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdEmpresaSucursalGetListDto {

    private UUID idSucursal;
    private String sucursal;
    private String nombreSucursal;
    private Boolean bloqueado;

}
