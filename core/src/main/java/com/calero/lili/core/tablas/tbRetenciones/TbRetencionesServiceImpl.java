package com.calero.lili.core.tablas.tbRetenciones;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TbRetencionesServiceImpl {

    private final TbRetencionesRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TbRetencionesBuilder tbRetencionesBuilder;

    public ResponseDto create(TbRetencionesCreationRequestDto request) {
        Optional<TbRetencionEntity> existing = tbRepository.findById(request.getCodigo());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigo()));
        }

        return responseApiBuilder.builderResponse(tbRepository
                .save(tbRetencionesBuilder.builderEntity(request)).getCodigo());
    }

    public ResponseDto update(String id, TbRetencionesCreationRequestDto request) {
        TbRetencionEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        return responseApiBuilder.builderResponse(tbRepository
                .save(tbRetencionesBuilder.builderUpdateEntity(request, entidad)).getCodigo());
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbRetencionesGetOneDto findById(String id) {
        TbRetencionEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return tbRetencionesBuilder.builderResponse(entidad);
    }

    public PaginatedDto<TbRetencionesGetListDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<TbRetencionEntity> page = tbRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<TbRetencionesGetListDto>();
        paginatedDto.setContent(page.getContent().stream().map(tbRetencionesBuilder::builderListResponse).collect(Collectors.toList()));

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
