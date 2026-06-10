package com.calero.lili.core.modVentas.reporteCredito.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DinarapPlazoOperacionEnum {

    // TODO EL CODIGO I020 NO EXISTE EN LA IMAGEN DE LOS CODIGOS QUE NOS PROPORCIONARON

    I40(40, "I040"),
    I109(190, "0109"),
    I75(75, "I075"),
    I30(30, "I030"),
    Y795(40, "Y795"),
    I10(10, "I010"),
    I60(60, "I060"),
    I50(50, "I050"),
    I45(45, "I045"),
    I5(5, "I005"),
    I15(15, "I015"),
    I90(90, "I090"),
    I70(70, "I070"),
    I105(105, "I105"),
    I0(0, "I000"),
    Z23(23, "Z023"),
    E60(60, "E060"),
    I20(20, "I020");


    private final Integer diasCreditos;
    private final String codigo;

    public static Integer getDiasCredito(String codigo) {

        for (DinarapPlazoOperacionEnum tipo : DinarapPlazoOperacionEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo.diasCreditos;
            }
        }
        throw new IllegalArgumentException("El código de término del pago no existe : " + codigo);
    }

}
