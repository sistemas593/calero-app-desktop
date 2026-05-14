package com.calero.lili.core.modAdModulos;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdModulos.builder.AdModuloBuilder;
import com.calero.lili.core.modAdModulos.dto.AdModuloRequestDto;
import com.calero.lili.core.modAdModulos.dto.AdModuloResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AdModuloServiceImpl {

    private final AdModuloRepository adModuloRepository;
    private final AdModuloBuilder adModuloBuilder;

    public AdModuloResponseDto create(AdModuloRequestDto request, String usuario) {
        AdModulosEntity entidad = adModuloBuilder.builderEntity(request);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        return adModuloBuilder.builderResponse(adModuloRepository.save(entidad));
    }

    public AdModuloResponseDto update(Long idModulo, AdModuloRequestDto request, String usuario) {

        AdModulosEntity modulo = adModuloRepository.getFindById(idModulo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Modulo con id {0} no existe ", idModulo)));

        AdModulosEntity entidad = adModuloBuilder.builderUpdateEntity(request, modulo);
        entidad.setModifiedBy(usuario);
        entidad.setModifiedDate(LocalDateTime.now());
        return adModuloBuilder.builderResponse(adModuloRepository.save(entidad));
    }

    public AdModuloResponseDto findById(Long idModulo) {
        return adModuloBuilder.builderResponse(adModuloRepository.getFindById(idModulo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Modulo con id {0} no existe ", idModulo))));
    }

    public void delete(Long idModulo, String usuario) {

        AdModulosEntity modulo = adModuloRepository.getFindById(idModulo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Modulo con id {0} no existe ", idModulo)));

        modulo.setDelete(Boolean.TRUE);
        modulo.setDeletedBy(usuario);
        modulo.setDeletedDate(LocalDateTime.now());

        adModuloRepository.save(modulo);
    }

    public List<AdModuloResponseDto> findAll() {
        return adModuloRepository.findAll()
                .stream()
                .map(adModuloBuilder::builderResponse)
                .toList();
    }


}
