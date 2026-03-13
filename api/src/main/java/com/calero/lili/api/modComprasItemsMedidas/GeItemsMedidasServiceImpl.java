package com.calero.lili.api.modComprasItemsMedidas;

import com.calero.lili.api.modComprasItemsMedidas.builder.GetItemsMedidasBuilder;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaCreationRequestDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaListFilterDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaReportDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class GeItemsMedidasServiceImpl {

    private final GeItemsMedidasRepository geItemsMedidasRepository;
    private final GetItemsMedidasBuilder getItemsMedidasBuilder;


    public GeItemMedidaCreationResponseDto create(Long idData, GeItemMedidaCreationRequestDto request, String usuario) {

        GeItemsMedidasEntity entity = getItemsMedidasBuilder.builderEntity(request, idData);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        GeItemsMedidasEntity createdDto = geItemsMedidasRepository.save(entity);
        return getItemsMedidasBuilder.builderResponse(createdDto);
    }

    public GeItemMedidaCreationResponseDto update(Long idData, UUID id, GeItemMedidaCreationRequestDto request, String usuario) {

        GeItemsMedidasEntity entidad = geItemsMedidasRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        GeItemsMedidasEntity update = getItemsMedidasBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        geItemsMedidasRepository.save(update);
        return getItemsMedidasBuilder.builderResponse(update);
    }

    public void delete(Long idData, UUID id, String usuario) {
        GeItemsMedidasEntity entidad = geItemsMedidasRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsMedidasRepository.save(entidad);
    }

    public GeItemMedidaCreationResponseDto findFirstById(Long idData, UUID id) {

        GeItemsMedidasEntity entidad = geItemsMedidasRepository.findById(idData, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return getItemsMedidasBuilder.builderResponse(entidad);
    }

    public PaginatedDto<GeItemMedidaReportDto> findAllPaginate(Long idData, GeItemMedidaListFilterDto filters, Pageable pageable) {

        Page<GeItemsMedidasEntity> page = geItemsMedidasRepository.findAllPaginate(idData, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<GeItemMedidaReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(getItemsMedidasBuilder::builderListDto)
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
