package com.calero.lili.core.modRRHH.modRRHHTrabajadores;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.builder.TrabajadorBuilder;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.RequestTrabajadorDto;
import com.calero.lili.core.modRRHH.modRRHHTrabajadores.dto.ResponseTrabajadorDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl {

    private final TrabajadorRepository trabajadorRepository;
    private final TrabajadorBuilder trabajadorBuilder;

    public void create(RequestTrabajadorDto request, GeTerceroEntity tercero) {
        trabajadorRepository
                .save(trabajadorBuilder.builderEntity(request, tercero));
    }

    public void update(RequestTrabajadorDto request, GeTerceroEntity tercero) {
        TrabajadorEntity trabajadorEntity = trabajadorRepository.getForFindByIdTercero(tercero.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe información del trabajador"));

        trabajadorRepository
                .save(trabajadorBuilder.builderUpdateEntity(request, trabajadorEntity, tercero));
    }


    public ResponseTrabajadorDto findByIdTercero(UUID idTercero) {

        Optional<TrabajadorEntity> trabajadorEntity = trabajadorRepository.getForFindByIdTercero(idTercero);
        return trabajadorEntity.map(trabajadorBuilder::builderResponse).orElse(null);
    }


    public void delete(UUID idTercero) {

        TrabajadorEntity trabajadorEntity = trabajadorRepository.getForFindByIdTercero(idTercero)
                .orElseThrow(() -> new GeneralException("No existe información del trabajador"));

        trabajadorRepository.delete(trabajadorEntity);
    }

}
