package com.calero.lili.api.modLocalidades.modCantones;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modLocalidades.modCantones.builder.CantonBuilder;
import com.calero.lili.api.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseCantonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

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
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia);
        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        CantonEntity updated = cantonBuilder.builderUpdateEntity(request, cantonEntity);
        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());
        return cantonBuilder.builderResponse(cantonRepository.save(updated));
    }

    public void delete(String idProvincia, String usuario) {
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia);
        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        cantonEntity.setDeletedBy(usuario);
        cantonEntity.setDeletedDate(LocalDateTime.now());
        cantonEntity.setDelete(Boolean.TRUE);
        cantonRepository.save(cantonEntity);
    }

    public ResponseCantonDto findFirstById(String idProvincia) {
        CantonEntity cantonEntity = cantonRepository.getForFindById(idProvincia);
        if (Objects.isNull(cantonEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        return cantonBuilder.builderResponse(cantonEntity);
    }

    public PaginatedDto<ResponseCantonDto> findAllPaginate(String codProvincia, Pageable pageable) {

        Page<CantonEntity> page = cantonRepository.findAllPaginate(codProvincia, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<ResponseCantonDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(cantonBuilder::builderResponse)
                .collect(Collectors.toList()));

        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());
        paginatedDto.setPaginator(paginated);
        return paginatedDto;
    }


}
