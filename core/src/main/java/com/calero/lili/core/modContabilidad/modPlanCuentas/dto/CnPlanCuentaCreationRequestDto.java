package com.calero.lili.core.modContabilidad.modPlanCuentas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CnPlanCuentaCreationRequestDto {

    @NotNull(message = "Es requerida el codigo de cuenta")
    private String codigoCuenta;

    @NotNull(message = "Es requerida el codigo de cuenta original")
    private String codigoCuentaOriginal;

    private UUID idCuentaPadre;

    private String cuenta;

    @NotNull(message = "Es requerida saber si es mayor")
    private Boolean mayor;
    private String tipoAuxiliar;

}
