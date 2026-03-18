package com.calero.lili.core.modComprasItemsCategorias;

import com.calero.lili.core.modComprasItemsCategorias.builder.GeItemsCategoriaBuilder;
import com.calero.lili.core.modComprasItemsCategorias.dto.GeItemCategoriaCreationResponseDto;
import com.calero.lili.core.modComprasItemsCategorias.dto.GeItemCategoriaReportDto;
import com.calero.lili.core.modComprasItemsCategorias.dto.GeItemMedidaListFilterDto;
import com.calero.lili.core.modComprasItemsCategorias.dto.GeItemsCategoriaCreationRequestDto;
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto;
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
public class GeItemsCategoriaServiceImpl {

    private final GeItemsCategoriaRepository geItemsCategoriaRepository;
    private final GeItemsCategoriaBuilder geItemsCategoriaBuilder;



    public GeItemCategoriaCreationResponseDto create(Long idData, GeItemsCategoriaCreationRequestDto request, String usuario) {
        GeItemsCategoriaEntity categoria = geItemsCategoriaBuilder.builderEntity(request, idData);
        categoria.setCreatedBy(usuario);
        categoria.setCreatedDate(LocalDateTime.now());
        geItemsCategoriaRepository.save(categoria);
        return geItemsCategoriaBuilder.builderResponse(categoria);
    }

    public GeItemCategoriaCreationResponseDto update(Long idData, UUID id,
                                                     GeItemsCategoriaCreationRequestDto request, String usuario) {


        GeItemsCategoriaEntity entidad = geItemsCategoriaRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        GeItemsCategoriaEntity update = geItemsCategoriaBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        geItemsCategoriaRepository.save(update);
        return geItemsCategoriaBuilder.builderResponse(update);
    }

    public void delete(Long idData, UUID id,String usuario) {

        GeItemsCategoriaEntity entidad = geItemsCategoriaRepository.findById(idData, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        geItemsCategoriaRepository.save(entidad);

    }

    public GeItemCategoriaCreationResponseDto findFirstById(Long idData, UUID id) {


        GeItemsCategoriaEntity entidad = geItemsCategoriaRepository.findById(idData, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return geItemsCategoriaBuilder.builderResponse(entidad);
    }

    public PaginatedDto<GeItemCategoriaReportDto> findAllPaginate(Long idData, GeItemMedidaListFilterDto filters, Pageable pageable) {

        Page<GeItemsCategoriaEntity> page = geItemsCategoriaRepository.findAllPaginate(idData, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<GeItemMedidaReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(geItemsCategoriaBuilder::builderListDto)
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
