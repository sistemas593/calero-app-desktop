package com.calero.lili.core.modLocalidades.modCantones;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modLocalidades.modCantones.builder.CantonBuilder;
import com.calero.lili.core.modLocalidades.modCantones.dto.CantonListFiltersDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CantonServiceImpl {

    private final CantonRepository cantonRepository;
    private final CantonBuilder cantonBuilder;

    public ResponseCantonDto create(RequestCantonDto request, String usuario) {
        CantonEntity newEntity = cantonBuilder.builderEntity(request);
        newEntity.setCreatedBy(usuario);
        newEntity.setCreatedDate(LocalDateTime.now());
        return cantonBuilder.builderResponse(cantonRepository.save(newEntity));
    }

    public ResponseCantonDto update(String idProvincia, RequestCantonDto request, String usuario) {
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + idProvincia));


        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        CantonEntity updated = cantonBuilder.builderUpdateEntity(request, cantonEntity);
        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());
        return cantonBuilder.builderResponse(cantonRepository.save(updated));
    }

    public void delete(String idProvincia, String usuario) {
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + idProvincia));

        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        cantonEntity.setDeletedBy(usuario);
        cantonEntity.setDeletedDate(LocalDateTime.now());
        cantonEntity.setDelete(Boolean.TRUE);
        cantonRepository.save(cantonEntity);
    }

    public ResponseCantonDto findFirstById(String idProvincia) {
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException("No se encontró el cantón con el código: " + idProvincia));

        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        return cantonBuilder.builderResponse(cantonEntity);
    }

    public List<ResponseCantonListDto> findAll(CantonListFiltersDto filter) {

        String codigoProvincia = filter.getCodigoProvincia();

        if (Objects.isNull(codigoProvincia) || codigoProvincia.isEmpty()) {
            throw new GeneralException("El codigo de provincia es obligatorio");
        }

        String filterContent = (filter.getFilter() == null || filter.getFilter().trim().isEmpty())
                ? ""
                : filter.getFilter().trim();

        return cantonRepository.getFindAll(codigoProvincia, filterContent)
                .stream()
                .map(cantonBuilder::builderListResponse)
                .toList();

    }


}
