package com.calero.lili.core.builder.retencion;

import com.calero.lili.core.modImpuestosAnexos.retencion.RetRelDep;
import com.calero.lili.core.modImpuestosProcesos.dto.impuestos.RetencionFuenteXmlDto;
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
