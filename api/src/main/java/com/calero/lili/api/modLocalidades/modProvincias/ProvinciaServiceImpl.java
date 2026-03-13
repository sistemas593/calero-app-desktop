package com.calero.lili.api.modLocalidades.modProvincias;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modLocalidades.modProvincias.builder.ProvinciaBuilder;
import com.calero.lili.api.modLocalidades.modProvincias.dto.ProvinceListFiltersDto;
import com.calero.lili.api.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.api.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
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
        ProvinciaEntity provinciaEntity = provinciaRepository.getForFindById(idProvincia);
        if (Objects.isNull(provinciaEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        ProvinciaEntity updated = provinciaBuilder.builderUpdateEntity(request, provinciaEntity);
        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());
        return provinciaBuilder.builderResponse(provinciaRepository.save(updated));
    }

    public void delete(String idProvincia, String usuario) {
        ProvinciaEntity provinciaEntity = provinciaRepository.getForFindById(idProvincia);
        if (Objects.isNull(provinciaEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        provinciaEntity.setDeletedBy(usuario);
        provinciaEntity.setDeletedDate(LocalDateTime.now());
        provinciaEntity.setDelete(Boolean.TRUE);
        provinciaRepository.save(provinciaEntity);
    }

    public ResponseProvinciaDto findFirstById(String idProvincia) {
        ProvinciaEntity provinciaEntity = provinciaRepository.getForFindById(idProvincia);
        if (Objects.isNull(provinciaEntity)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idProvincia));
        }
        return provinciaBuilder.builderResponse(provinciaEntity);
    }

    public PaginatedDto<ResponseProvinciaDto> findAllPaginate(ProvinceListFiltersDto filters, Pageable pageable) {

        Page<ProvinciaEntity> page = provinciaRepository.findAllPaginate(Objects.nonNull(filters.getFilter())
                ? filters.getFilter() : "", pageable);

        PaginatedDto paginatedDto = new PaginatedDto<ResponseProvinciaDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(provinciaBuilder::builderResponse)
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
