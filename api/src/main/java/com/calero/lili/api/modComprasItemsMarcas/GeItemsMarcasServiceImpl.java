package com.calero.lili.api.modComprasItemsMarcas;

import com.calero.lili.api.modComprasItemsMarcas.builder.GetItemsMarcasBuilder;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMarcasReportDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMedidaListFilterDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemsMarcasCreationRequestDto;
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

public class GeItemsMarcasServiceImpl {

    private final GeItemsMarcasRepository geItemsMarcasRepository;
    private final GetItemsMarcasBuilder getItemsMarcasBuilder;


    public GeItemMedidaCreationResponseDto create(Long idData, GeItemsMarcasCreationRequestDto request, String usuario) {

        GeItemsMarcasEntity entity = getItemsMarcasBuilder.builderEntity(request, idData);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        GeItemsMarcasEntity createdDto = geItemsMarcasRepository.save(entity);
        return getItemsMarcasBuilder.builderResponse(createdDto);
    }

    public GeItemMedidaCreationResponseDto update(Long idData, UUID id, GeItemsMarcasCreationRequestDto request, String usuario) {

        GeItemsMarcasEntity entidad = geItemsMarcasRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        GeItemsMarcasEntity update = getItemsMarcasBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        geItemsMarcasRepository.save(update);
        return getItemsMarcasBuilder.builderResponse(update);
    }

    public void delete(Long idData, UUID id, String usuario) {
        GeItemsMarcasEntity entidad = geItemsMarcasRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsMarcasRepository.save(entidad);

    }

    public GeItemMedidaCreationResponseDto findFirstById(Long idData, UUID id) {

        GeItemsMarcasEntity entidad = geItemsMarcasRepository.findById(idData, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return getItemsMarcasBuilder.builderResponse(entidad);
    }

    public PaginatedDto<GeItemMarcasReportDto> findAllPaginate(Long idData, GeItemMedidaListFilterDto filters, Pageable pageable) {


        Page<GeItemsMarcasEntity> page = geItemsMarcasRepository.findAllPaginate(idData, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<GeItemMedidaReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(getItemsMarcasBuilder::builderListDto)
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
