package com.calero.lili.api.modImpuestosProcesos.dto.impuestos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetencionFuenteXmlDto {

    private String razonSocial;
    private String numRuc;
    private String anio;
    private String fechaCarga;
    private String numeroRegistros;
    private String tipoEmpleador;
    private String enteSegSocial;

    private List<DetalleValores> detalleValores;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleValores {

        private InfoEmpleadoDto infoEmpleado;
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
        private String sisSalNet;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InfoEmpleadoDto {

        private String benGalpg;
        private String enfcatastro;
        private String numCargRebGastPers;
        private String tipIdRet;
        private String idRet;
        private String apellidoTrab;
        private String nombreTrab;
        private String estab;
        private String residenciaTrab;
        private String paisResidencia;
        private String aplicaConvenio;
        private String tipoTrabajDiscap;
        private String porcentajeDiscap;
        private String tipIdDiscap;
        private String idDiscap;

    }


}
