package com.calero.lili.core.modImpuestosAnexos.retencion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"empleado", "suelSal", "sobSuelComRemu", "partUtil", "intGrabGen", "impRentEmpl", "decimTer", "decimCuar",
        "fondoReserva", "salarioDigno", "otrosIngRenGrav", "ingGravConEsteEmpl", "apoPerIess", "aporPerIessConOtrosEmpls",
        "deducVivienda", "deducSalud", "deducEducartcult", "deducAliement", "deducVestim", "deduccionTurismo", "exoDiscap",
        "exoTerEd", "basImp", "impRentCaus", "rebajaGastosPersonales", "impuestoRentaRebajaGastosPersonales",
        "valRetAsuOtrosEmpls", "valImpAsuEsteEmpl", "valRet"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatRetRelDep {

    private Empleado empleado;
    private String suelSal;
    private String sobSuelComRemu;
    private String partUtil;
    private String intGrabGen;
    private String impRentEmpl;
    private String decimTer;
    private String decimCuar;
    private String fondoReserva;
    private String salarioDigno;
    private String otrosIngRenGrav;
    private String ingGravConEsteEmpl;
    private String apoPerIess;
    private String aporPerIessConOtrosEmpls;
    private String deducVivienda;
    private String deducSalud;
    private String deducEducartcult;
    private String deducAliement;
    private String deducVestim;
    private String deduccionTurismo;
    private String exoDiscap;
    private String exoTerEd;
    private String basImp;
    private String impRentCaus;
    private String rebajaGastosPersonales;
    private String impuestoRentaRebajaGastosPersonales;
    private String valRetAsuOtrosEmpls;
    private String valImpAsuEsteEmpl;
    private String valRet;


}
