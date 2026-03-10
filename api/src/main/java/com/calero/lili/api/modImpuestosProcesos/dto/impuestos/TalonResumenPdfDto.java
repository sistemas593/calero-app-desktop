package com.calero.lili.api.modImpuestosProcesos.dto.impuestos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TalonResumenPdfDto {

    private String razonSocial;
    private String numRuc;
    private String anio;
    private String fechaCarga;
    private String numeroRegistros;
    private String numIdentificacionContador;
    private String numIdentificacionRepLegal;
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
    private String otrosIngRenNoGrav;

}
