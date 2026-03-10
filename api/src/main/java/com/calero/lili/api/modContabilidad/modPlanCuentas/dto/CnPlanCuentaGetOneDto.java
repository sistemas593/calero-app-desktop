package com.calero.lili.api.modContabilidad.modPlanCuentas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class CnPlanCuentaGetOneDto {

    private UUID idCuenta;
    private UUID idCuentaPadre;
    private String codigoCuenta;
    private String codigoCuentaOriginal;
    private String cuenta;
    private Boolean mayor;
    private Integer nivel;
    private String tipoAuxiliar;
}
