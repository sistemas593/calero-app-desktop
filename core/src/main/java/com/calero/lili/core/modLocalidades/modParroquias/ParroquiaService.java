package com.calero.lili.core.modLocalidades.modParroquias;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modCantones.CantonRepository;
import com.calero.lili.core.modLocalidades.modParroquias.builder.ParroquiaBuilder;
import com.calero.lili.core.modLocalidades.modParroquias.dto.FilterRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaResponseDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaResponseListDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ParroquiaService {

    private final ParroquiaRepository parroquiaRepository;
    private final ParroquiaBuilder parroquiaBuilder;
    private final CantonRepository cantonRepository;

    public ParroquiaResponseDto create(ParroquiaRequestDto request, String usuario) {


        parroquiaRepository.getForFindById(request.getCodigoParroquia())
                .ifPresent(c -> {
                    throw new GeneralException(MessageFormat.format("El codigo de la parroquia: {0} ya existe", request.getCodigoParroquia()));
                });

        CantonEntity cantonEntity = cantonRepository.getForFindById(request.getCodigoCanton())
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + request.getCodigoCanton()));


        ParroquiaEntity entity = parroquiaBuilder.builder(request);

        entity.setCanton(cantonEntity);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());

        return parroquiaBuilder.builderResponse(parroquiaRepository.save(entity));
    }

    public ParroquiaResponseDto update(String codigoParroquia, ParroquiaRequestDto request, String usuario) {


        ParroquiaEntity parroquia = parroquiaRepository.getForFindById(codigoParroquia)
                .orElseThrow(() -> new GeneralException("No se encontró la parroquia con el código: " + codigoParroquia));

        CantonEntity cantonEntity = null;
        if (Objects.nonNull(request.getCodigoCanton())) {

            cantonEntity = cantonRepository.getForFindById(request.getCodigoCanton())
                    .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + request.getCodigoCanton()));

        } else {
            cantonEntity = parroquia.getCanton();
        }

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

    public List<ParroquiaResponseListDto> findAll(FilterRequestDto filter) {

        String codigoCanton = filter.getCodigoCanton();

        if (Objects.isNull(codigoCanton) || codigoCanton.isEmpty()) {
            throw new GeneralException("El codigo de canton es obligatorio");
        }

        String filterContent = (filter.getFilter() == null || filter.getFilter().trim().isEmpty())
                ? ""
                : filter.getFilter().trim();

        return parroquiaRepository.getFindAll(codigoCanton, filterContent)
                .stream()
                .map(parroquiaBuilder::builderListResponse)
                .toList();
    }
}
