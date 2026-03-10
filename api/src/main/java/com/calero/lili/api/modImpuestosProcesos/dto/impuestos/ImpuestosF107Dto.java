package com.calero.lili.api.modImpuestosProcesos.dto.impuestos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class ImpuestosF107Dto {
    private String ano;
    private String ruc;
    private String razonSocial;

    private String rucContador;
    private String benGalpg;
    private String enfcatastro;
    private int numCargRebGastPers;
    private String tipIdRet;
    private String idRet;
    private String apellidoTrab;
    private String nombreTrab;
    private String estab;
    private String residenciaTrab;
    private String paisResidencia;
    private String aplicaConvenio;
    private String tipoTrabajDiscap;
    private int porcentajeDiscap;
    private String tipIdDiscap;
    private String idDiscap;

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
    private int sisSalNet;
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
