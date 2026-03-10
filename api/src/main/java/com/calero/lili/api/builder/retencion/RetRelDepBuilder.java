package com.calero.lili.api.builder.retencion;

import com.calero.lili.api.modImpuestosAnexos.retencion.RetRelDep;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RetRelDepBuilder {

    private final DatRetRelDepBuilder retRelDepBuilder;

    public RetRelDep builderRetRelDep(RetencionFuenteXmlDto model) {
        return RetRelDep.builder()
                .datRetRelDepList(retRelDepBuilder.builderListRetRelDel(model.getDetalleValores()))
                .build();
    }
}
