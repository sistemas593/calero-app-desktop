package com.calero.lili.api.modRRHH.modRRHHRublos;

import com.calero.lili.api.modRRHH.modRRHHRublos.builder.RubroBuilder;
import com.calero.lili.api.modRRHH.modRRHHRublos.dto.RubroRequestDto;
import com.calero.lili.api.modRRHH.modRRHHRublos.dto.RubroResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RubroServiceImpl {


    private final RubrosRepository rubrosRepository;
    private final RubroBuilder rubroBuilder;


    public RubroResponseDto create(Long idData, Long idEmpresa, RubroRequestDto request, String usuario) {
        RubrosEntity newEntity = rubroBuilder.builderEntity(request, idEmpresa, idData);
        newEntity.setCreatedBy(usuario);
        newEntity.setCreatedDate(LocalDateTime.now());
        return rubroBuilder.builderResponse(rubrosRepository.save(newEntity));
    }

    public RubroResponseDto update(UUID id, Long idData, Long idEmpresa, RubroRequestDto request,String usuario) {

        RubrosEntity entidad = rubrosRepository.getForFindById(id, idEmpresa, idData)
                .orElseThrow(() -> new GeneralException("El rubro no existe"));

        RubrosEntity update = rubroBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return rubroBuilder.builderResponse(rubrosRepository.save(update));
    }

    public RubroResponseDto findById(UUID id, Long idData, Long idEmpresa) {
        return rubroBuilder.builderResponse(rubrosRepository.getForFindById(id, idEmpresa, idData)
                .orElseThrow(() -> new GeneralException("El rubro no existe")));
    }

    public void delete(UUID id, Long idData, Long idEmpresa, String usuario) {
        RubrosEntity entidad = rubrosRepository.getForFindById(id, idEmpresa, idData)
                .orElseThrow(() -> new GeneralException("El rubro no existe"));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        rubrosRepository.save(entidad);
    }

    public List<RubroResponseDto> getAll(Long idEmpresa, Long idData) {
        return rubrosRepository.findAllList(idEmpresa, idData)
                .stream()
                .map(rubroBuilder::builderResponse)
                .toList();
    }


}
