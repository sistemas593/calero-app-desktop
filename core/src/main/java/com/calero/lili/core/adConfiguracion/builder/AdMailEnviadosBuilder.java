package com.calero.lili.core.adConfiguracion.builder;

import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosResponseDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AdMailEnviadosBuilder {


    public AdMailEnviadosResponseDto builderResponse(AdMailEnviadosEntity model) {
        return AdMailEnviadosResponseDto.builder()
                .id(model.getId())
                .clave1(model.getClave1())
                .codigoDocumento(model.getCodigoDocumento())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .mailTo(model.getMailTo())
                .fecha(Objects.nonNull(model.getFecha()) ? DateUtils.toStringFechaEmision(model.getFecha()) : null)
                .total(model.getTotal())
                .build();
    }


}
