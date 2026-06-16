package com.calero.lili.core.modVentas.reporteCredito.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DinarapPlazoOperacionEnum {

    // TODO EL CODIGO I020 NO EXISTE EN LA IMAGEN DE LOS CODIGOS QUE NOS PROPORCIONARON

    Z24(0, "Z024"),
    Z44(5, "Z404"),
    Z1(0, "Z001"),
    Z23(8, "Z023"),
    US1(10, "US01"),
    Z26(10, "Z026"),
    Z248(15, "Z248"),
    Z262(20, "Z262"),
    Z2(30, "Z002"),
    Y795(40, "Y795"),
    Z43(45, "Z403"),
    I19(50, "0109"),
    Z3(60, "Z003"),
    Z42(75, "Z402"),
    Z4(90, "Z004"),
    Y938(60, "Y938"),
    Y94(90, "Y940"),
    Z18(60, "Z018"),
    B9(90, "B090"),
    B6(60, "B060"),
    I54(65, "0054"),
    I0(0, "I000"),
    I5(5, "I005"),
    I10(10, "I010"),
    I15(15, "I015"),
    I30(30, "I030"),
    I40(40, "I040"),
    I45(45, "I045"),
    I50(50, "I050"),
    I60(60, "I060"),
    I75(75, "I075"),
    I90(90, "I090"),
    I105(105, "I105"),
    I120(120, "I120"),
    I135(135, "I135"),
    I150(150, "I150"),
    I165(165, "I165"),
    I180(180, "I180"),
    I210(210, "I210"),
    I240(240, "I240"),
    E060(60, "E060"),
    I070(70, "I070"),
    D10A(10, "D10A"),
    B30(30, "B030"),
    Y950(30, "Y950"),
    E30(30, "E030"),
    ES25(60, "ES25"),
    L60(60, "L060"),
    E90(90, "E090"),
    I020(20,"I020");


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
