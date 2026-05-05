package com.calero.lili.core.modLocalidades.modParroquias;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modCantones.CantonRepository;
import com.calero.lili.core.modLocalidades.modParroquias.builder.ParroquiaBuilder;
import com.calero.lili.core.modLocalidades.modParroquias.dto.FilterRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ParroquiaService {

    private final ParroquiaRepository parroquiaRepository;
    private final ParroquiaBuilder parroquiaBuilder;
    private final CantonRepository cantonRepository;

    public ParroquiaResponseDto create(ParroquiaRequestDto request, String usuario) {

        CantonEntity cantonEntity = cantonRepository.getForFindById(request.getCodigoCanton())
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + request.getCodigoCanton()));

        ParroquiaEntity entity = parroquiaBuilder.builder(request);

        entity.setCanton(cantonEntity);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());

        return parroquiaBuilder.builderResponse(parroquiaRepository.save(entity));
    }

    public ParroquiaResponseDto update(String codigoParroquia, ParroquiaRequestDto request, String usuario) {


        CantonEntity cantonEntity = cantonRepository.getForFindById(request.getCodigoCanton())
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + request.getCodigoCanton()));

        ParroquiaEntity parroquia = parroquiaRepository.getForFindById(codigoParroquia)
                .orElseThrow(() -> new GeneralException("No se encontró la parroquia con el código: " + codigoParroquia));

        ParroquiaEntity entity = parroquiaBuilder.builderUpdate(parroquia, request);

        entity.setCanton(cantonEntity);
        entity.setModifiedBy(usuario);
        entity.setModifiedDate(LocalDateTime.now());

        return parroquiaBuilder.builderResponse(parroquiaRepository.save(entity));
    }


    public ParroquiaResponseDto findById(String codigoParroquia) {
        return parroquiaBuilder.builderResponse(parroquiaRepository.getForFindById(codigoParroquia)
                .orElseThrow(() -> new GeneralException("No se encontró la parroquia con el código: " + codigoParroquia)));
    }

    public void delete(String codigoParroquia, String usuario) {
        ParroquiaEntity parroquia = parroquiaRepository.getForFindById(codigoParroquia)
                .orElseThrow(() -> new GeneralException("No se encontró la parroquia con el código: " + codigoParroquia));

        parroquia.setDelete(Boolean.TRUE);
        parroquia.setDeletedBy(usuario);
        parroquia.setDeletedDate(LocalDateTime.now());

        parroquiaRepository.save(parroquia);

    }

    public List<ParroquiaResponseDto> findAll(FilterRequestDto filter) {

        String codigoCanton = filter.getCodigoCanton();

        String filterContent = (filter.getFilter() == null || filter.getFilter().trim().isEmpty())
                ? ""
                : filter.getFilter().trim();

        return parroquiaRepository.getFindAll(codigoCanton, filterContent)
                .stream()
                .map(parroquiaBuilder::builderResponse)
                .toList();
    }
}
