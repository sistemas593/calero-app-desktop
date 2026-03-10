package com.calero.lili.api.modImpuestosProcesos.dto.impuestos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValoresTalonResumenDto {


    private Integer numeroRegistros;
    private BigDecimal suelSal;
    private BigDecimal sobSuelComRemu;
    private BigDecimal partUtil;
    private BigDecimal intGrabGen;
    private BigDecimal impRentEmpl;
    private BigDecimal decimTer;
    private BigDecimal decimCuar;
    private BigDecimal fondoReserva;
    private BigDecimal salarioDigno;
    private BigDecimal otrosIngRenGrav;
    private BigDecimal ingGravConEsteEmpl;
    private BigDecimal apoPerIess;
    private BigDecimal aporPerIessConOtrosEmpls;
    private BigDecimal deducVivienda;
    private BigDecimal deducSalud;
    private BigDecimal deducEducartcult;
    private BigDecimal deducAliement;
    private BigDecimal deducVestim;
    private BigDecimal deduccionTurismo;
    private BigDecimal exoDiscap;
    private BigDecimal exoTerEd;
    private BigDecimal basImp;
    private BigDecimal impRentCaus;
    private BigDecimal rebajaGastosPersonales;
    private BigDecimal impuestoRentaRebajaGastosPersonales;
    private BigDecimal valRetAsuOtrosEmpls;
    private BigDecimal valImpAsuEsteEmpl;
    private BigDecimal valRet;
    private BigDecimal otrosIngRenNoGrav;

}
