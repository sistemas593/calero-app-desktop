package com.calero.lili.core.modLocalidades.modParroquias.builder;

import com.calero.lili.core.modLocalidades.modCantones.builder.CantonBuilder;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ParroquiaBuilder {

    private final CantonBuilder cantonBuilder;

    public ParroquiaEntity builder(ParroquiaRequestDto model) {
        return ParroquiaEntity.builder()
                .codigoParroquia(model.getCodigoParroquia())
                .parroquia(model.getParroquia())
                .build();
    }

    public ParroquiaEntity builderUpdate(ParroquiaEntity entidad, ParroquiaRequestDto model) {
        return ParroquiaEntity.builder()
                .codigoParroquia(entidad.getCodigoParroquia())
                .parroquia(model.getParroquia())
                .build();
    }


    public ParroquiaResponseDto builderResponse(ParroquiaEntity model) {
        return ParroquiaResponseDto.builder()
                .codigoParroquia(model.getCodigoParroquia())
                .parroquia(model.getParroquia())
                .canton(cantonBuilder.builderResponse(model.getCanton()))
                .build();
    }


}
