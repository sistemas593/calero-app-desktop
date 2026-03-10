package com.calero.lili.api.modImpuestosAnexos.retencion;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"benGalpg", "enfcatastro", "numCargRebGastPers", "tipIdRet",
        "idRet", "apellidoTrab", "nombreTrab", "estab", "residenciaTrab", "paisResidencia",
        "aplicaConvenio", "tipoTrabajDiscap", "porcentajeDiscap", "tipIdDiscap", "idDiscap"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Empleado {

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
