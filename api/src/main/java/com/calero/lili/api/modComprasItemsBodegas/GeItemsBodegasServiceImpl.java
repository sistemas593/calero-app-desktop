package com.calero.lili.api.modComprasItemsBodegas;

import com.calero.lili.api.modComprasItemsBodegas.builder.GetItemsBodegasBuilder;
import com.calero.lili.api.modComprasItemsBodegas.dto.GeItemBodegaCreationRequestDto;
import com.calero.lili.api.modComprasItemsBodegas.dto.GeItemBodegaCreationResponseDto;
import com.calero.lili.api.modComprasItemsBodegas.dto.GeItemBodegaListFilterDto;
import com.calero.lili.api.modComprasItemsBodegas.dto.GeItemBodegaReportDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeItemsBodegasServiceImpl {

    private final GeItemsBodegasRepository geItemsMedidasRepository;
    private final GetItemsBodegasBuilder getItemsBodegasBuilder;


    public GeItemBodegaCreationResponseDto create(Long idData, Long idEmpresa,
                                                  GeItemBodegaCreationRequestDto request, String usuario) {

        IvBodegaEntity entidad = getItemsBodegasBuilder.builderEntity(request, idData, idEmpresa);
        entidad.setIdBodega(UUID.randomUUID());
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        return getItemsBodegasBuilder.builderDto(geItemsMedidasRepository.save(entidad));
    }

    public GeItemBodegaCreationResponseDto update(Long idData, Long idEmpresa, UUID id,
                                                  GeItemBodegaCreationRequestDto request, String usuario) {

        IvBodegaEntity entidad = geItemsMedidasRepository.findById(idData, idEmpresa, id);
        if (entidad != null) {
            IvBodegaEntity update = getItemsBodegasBuilder.builderUpdateEntity(request, entidad);
            update.setModifiedBy(usuario);
            update.setModifiedDate(LocalDateTime.now());
            geItemsMedidasRepository.save(update);
            return getItemsBodegasBuilder.builderDto(update);
        } else {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        IvBodegaEntity entidad = geItemsMedidasRepository.findById(idData, idEmpresa, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsMedidasRepository.save(entidad);

    }

    public GeItemBodegaCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {


        IvBodegaEntity entidad = geItemsMedidasRepository.findById(idData, idEmpresa, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return getItemsBodegasBuilder.builderDto(entidad);
    }

    public PaginatedDto<GeItemBodegaReportDto> findAllPaginate(Long idData, Long idEmpresa, GeItemBodegaListFilterDto filters, Pageable pageable) {


        Page<IvBodegaEntity> page = geItemsMedidasRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<GeItemBodegaReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(getItemsBodegasBuilder::builderReportDto)
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
