package com.calero.lili.core.tablas.tbRetencionesCodigos;

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
public class TbRetencionesCodigosServiceImpl {

    private final TbRetencionesCodigosRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TbRetencionesCodigosBuilder tbRetencionesCodigosBuilder;

    public ResponseDto create(TbRetencionesCodigosCreationRequestDto request) {
        Optional<TbRetencionesCodigosEntity> existing = tbRepository.findById(request.getCodigoRetencion());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigoRetencion()));
        }

        return responseApiBuilder.builderResponse(tbRepository.save(tbRetencionesCodigosBuilder
                .builderEntity(request)).getCodigoRetencion());
    }

    public ResponseDto update(String id, TbRetencionesCodigosCreationRequestDto request) {
        TbRetencionesCodigosEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        return responseApiBuilder.builderResponse(tbRepository.save(tbRetencionesCodigosBuilder
                .builderUpdateEntity(request, entidad)).getCodigoRetencion());
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbRetencionesCodigosGetOneDto findById(String id) {
        TbRetencionesCodigosEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));

        return tbRetencionesCodigosBuilder.builderResponse(entidad);
    }

    public PaginatedDto<TbRetencionesCodigosGetListDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<TbRetencionesCodigosEntity> page = tbRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<TbRetencionesCodigosGetListDto>();
        paginatedDto.setContent(page.getContent().stream().map(tbRetencionesCodigosBuilder::builderListResponse).collect(Collectors.toList()));

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
