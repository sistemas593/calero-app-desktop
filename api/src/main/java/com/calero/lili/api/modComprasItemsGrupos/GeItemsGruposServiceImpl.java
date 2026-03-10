package com.calero.lili.api.modComprasItemsGrupos;

import com.calero.lili.api.modComprasItemsGrupos.builder.GetItemGrupoBuilder;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoCreationRequestDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetListDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetOneDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoListFilterDto;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeItemsGruposServiceImpl {

    private final GeItemsGruposRepository geItemsGruposRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final GetItemGrupoBuilder getItemGrupoBuilder;
    private final AuditorAware<String> auditorAware;

    public ResponseDto create(Long idData, Long idEmpresa, GeItemGrupoCreationRequestDto request) {

        GeItemGrupoEntity entidad = geItemsGruposRepository
                .save(getItemGrupoBuilder.builderEntity(request, idData, idEmpresa));

        return responseApiBuilder.builderResponse(entidad.getIdGrupo().toString());

    }

    public ResponseDto update(Long idData, Long idEmpresa, UUID id, GeItemGrupoCreationRequestDto request) {

        GeItemGrupoEntity entidad = geItemsGruposRepository.findByIdGrupo(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        GeItemGrupoEntity entity = getItemGrupoBuilder.builderUpdateEntity(request, entidad);
        entity.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entity.setModifiedDate(LocalDateTime.now());
        geItemsGruposRepository.save(entity);
        return responseApiBuilder.builderResponse(entity.getIdGrupo().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        GeItemGrupoEntity entidad = geItemsGruposRepository.findByIdGrupo(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());
        geItemsGruposRepository.save(entidad);

    }

    public GeItemGrupoGetOneDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        GeItemGrupoEntity entidad = geItemsGruposRepository.findByIdGrupo(idData, idEmpresa, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return getItemGrupoBuilder.builderDto(entidad);
    }

    public PaginatedDto<GeItemGrupoGetListDto> findAllPaginate(Long idData, Long idEmpresa, GeItemGrupoListFilterDto filters, Pageable pageable) {

        Page<GeItemGrupoEntity> page = geItemsGruposRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);

        List<GeItemGrupoGetListDto> dtoList = page.stream().map(getItemGrupoBuilder::builderListDto).toList();

        PaginatedDto paginatedDto = new PaginatedDto<GeItemGrupoGetListDto>();
        paginatedDto.setContent(dtoList);

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

//    private GeItemGrupoGetListDto toDtoList(GeItemGrupoEntity projection) {
//        GeItemGrupoGetListDto dto = new GeItemGrupoGetListDto();
//        dto.setIdGrupo(projection.getIdGrupo());
//        dto.setGrupo(projection.getGrupo());
//        return dto;
//    }

//    private GeItemGrupoEntity toEntity(GeItemGrupoCreationRequestDto request, GeItemGrupoEntity entidad) {
//        entidad.setGrupo(request.getGrupo());
//        entidad.setIdCuentaInventario(request.getIdCuentaInventario());
//        entidad.setIdCuentaIngreso(request.getIdCuentaIngreso());
//        entidad.setIdCuentaCosto(request.getIdCuentaCosto());
//        entidad.setIdCuentaDescuento(request.getIdCuentaDescuento());
//        entidad.setIdCuentaDevolucion(request.getIdCuentaDevolucion());
//        return entidad;
//    }
//    private GeItemGrupoGetOneDto toDto(GeItemGrupoEntity entity) {
//        GeItemGrupoGetOneDto dto = new GeItemGrupoGetOneDto();
//        dto.setIdGrupo(entity.getIdGrupo());
//        dto.setGrupo(entity.getGrupo());
//        return dto;
//    }
}
