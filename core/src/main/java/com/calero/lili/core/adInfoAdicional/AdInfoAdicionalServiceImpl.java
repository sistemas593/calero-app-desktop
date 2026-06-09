package com.calero.lili.core.adInfoAdicional;

import com.calero.lili.core.adInfoAdicional.builder.AdInfoAdicionalBuilder;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalRequestDto;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdInfoAdicionalServiceImpl {

    private final AdInfoAdicionalBuilder adInfoAdicionalBuilder;
    private final AdInfoAdicionalRepository adInfoAdicionalRepository;


    public AdInfoAdicionalResponseDto create(Long idData, Long idEmpresa,
                                             AdInfoAdicionalRequestDto request, String usuario) {
        AdInfoAdicionalEntity entity = adInfoAdicionalBuilder.builderEntity(idData, idEmpresa, request);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.save(entity));
    }


    public AdInfoAdicionalResponseDto update(Long idData, Long idEmpresa, UUID idInfoAdicional,
                                             AdInfoAdicionalRequestDto request, String usuario) {

        AdInfoAdicionalEntity entidad = adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional)));

        AdInfoAdicionalEntity update = adInfoAdicionalBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.save(update));
    }


    public AdInfoAdicionalResponseDto findById(Long idData, Long idEmpresa, UUID idInfoAdicional) {

        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional))));
    }


    public List<AdInfoAdicionalResponseDto> findAll(Long idData, Long idEmpresa) {
        return adInfoAdicionalRepository.findAll(idData, idEmpresa)
                .stream()
                .map(adInfoAdicionalBuilder::builderResponse)
                .toList();
    }

    public void delete(Long idData, Long idEmpresa, UUID idInfoAdicional, String usuario) {

        AdInfoAdicionalEntity entidad = adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        adInfoAdicionalRepository.save(entidad);
    }


}
