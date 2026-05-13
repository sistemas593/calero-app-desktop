package com.calero.lili.core.modLocalidades.modProvincias;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modLocalidades.modProvincias.builder.ProvinciaBuilder;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ProvinceListFiltersDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinciaServiceImpl {

    private final ProvinciaRepository provinciaRepository;
    private final ProvinciaBuilder provinciaBuilder;

    public ResponseProvinciaDto create(RequestProvinciaDto request, String usuario) {
        ProvinciaEntity newEntity = provinciaBuilder.builderEntity(request);
        newEntity.setCreatedBy(usuario);
        newEntity.setCreatedDate(LocalDateTime.now());
        return provinciaBuilder.builderResponse(provinciaRepository.save(newEntity));
    }

    public ResponseProvinciaDto update(String idProvincia, RequestProvinciaDto request, String usuario) {
        ProvinciaEntity provinciaEntity = provinciaRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia)));

        ProvinciaEntity updated = provinciaBuilder.builderUpdateEntity(request, provinciaEntity);
        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());
        return provinciaBuilder.builderResponse(provinciaRepository.save(updated));
    }

    public void delete(String idProvincia, String usuario) {

        ProvinciaEntity provinciaEntity = provinciaRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia)));

        provinciaEntity.setDeletedBy(usuario);
        provinciaEntity.setDeletedDate(LocalDateTime.now());
        provinciaEntity.setDelete(Boolean.TRUE);
        provinciaRepository.save(provinciaEntity);
    }

    public ResponseProvinciaDto findFirstById(String idProvincia) {

        return provinciaBuilder.builderResponse(provinciaRepository.getForFindById(idProvincia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia))));

    }

    public List<ResponseProvinciaDto> findAll(ProvinceListFiltersDto filter) {

        String filterContent = (filter.getFilter() == null || filter.getFilter().trim().isEmpty())
                ? ""
                : filter.getFilter().trim();

        List<ProvinciaEntity> provinciaEntities = provinciaRepository.getFindAll(filterContent);
        return provinciaEntities.stream()
                .map(provinciaBuilder::builderResponse)
                .collect(Collectors.toList());
    }

}
