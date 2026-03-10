package com.calero.lili.api.builder.retencion;

import com.calero.lili.api.modImpuestosAnexos.retencion.Empleado;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoBuilder {

    public Empleado builderEmpleado(RetencionFuenteXmlDto.InfoEmpleadoDto model) {
        return Empleado.builder()
                .benGalpg(model.getBenGalpg())
                .enfcatastro(model.getEnfcatastro())
                .numCargRebGastPers(model.getNumCargRebGastPers())
                .tipIdRet(model.getTipIdRet())
                .idRet(model.getIdRet())
                .apellidoTrab(model.getApellidoTrab())
                .nombreTrab(model.getNombreTrab())
                .estab(model.getEstab())
                .residenciaTrab(model.getResidenciaTrab())
                .paisResidencia(model.getPaisResidencia())
                .aplicaConvenio(model.getAplicaConvenio())
                .tipoTrabajDiscap(model.getTipoTrabajDiscap())
                .porcentajeDiscap(model.getPorcentajeDiscap())
                .tipIdDiscap(model.getTipIdDiscap())
                .idDiscap(model.getIdDiscap())
                .build();
    }

}
