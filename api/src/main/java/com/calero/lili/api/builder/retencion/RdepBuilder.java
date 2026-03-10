package com.calero.lili.api.builder.retencion;

import com.calero.lili.api.modImpuestosAnexos.retencion.Rdep;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RdepBuilder {

    private final RetRelDepBuilder retRelDepBuilder;

    public Rdep builderRdep(RetencionFuenteXmlDto model) {
        return Rdep.builder()
                .numRuc(model.getNumRuc())
                .anio(model.getAnio())
                .tipoEmpleador(model.getTipoEmpleador())
                .enteSegSocial(model.getEnteSegSocial())
                .retRelDep(retRelDepBuilder.builderRetRelDep(model))
                .build();
    }

}
