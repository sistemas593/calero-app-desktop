package com.calero.lili.core.modAdminEmpresasSucursales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEmpresaSucursalGetOneDto {

    private UUID idSucursal;

    private String sucursal;
    private String nombreSucursal;
    private Boolean bloqueado;

}
