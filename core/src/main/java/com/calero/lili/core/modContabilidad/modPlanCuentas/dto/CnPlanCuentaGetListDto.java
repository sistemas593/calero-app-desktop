package com.calero.lili.core.modContabilidad.modPlanCuentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CnPlanCuentaGetListDto {

    private UUID idCuenta;
    private UUID idCuentaPadre;
    private String codigoCuenta;
    private String codigoCuentaOriginal;
    private String cuenta;
    private Boolean mayor;
    private Integer nivel;
    private String tipoAuxiliar;
}
