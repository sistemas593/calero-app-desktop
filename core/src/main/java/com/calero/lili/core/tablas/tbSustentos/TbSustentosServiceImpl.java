package com.calero.lili.core.tablas.tbSustentos;

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
public class TbSustentosServiceImpl {

    private final TbSustentosRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TbSustentosBuilder tbSustentosBuilder;

    public ResponseDto create(TbSustentosCreationRequestDto request) {
        Optional<TbSustentosEntity> existing = tbRepository.findById(request.getCodigoSustento());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigoSustento()));
        }

        return responseApiBuilder.builderResponse(tbRepository.save(tbSustentosBuilder
                .builderEntity(request)).getCodigoSustento());
    }

    public ResponseDto update(String id, TbSustentosCreationRequestDto request) {
        TbSustentosEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return responseApiBuilder.builderResponse(tbRepository.save(tbSustentosBuilder
                .builderUpdateEntity(request, entidad)).getCodigoSustento());
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbSustentosGetOneDto findById(String id) {
        TbSustentosEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return tbSustentosBuilder.builderResponse(entidad);
    }

    public PaginatedDto<TbSustentosGetListDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<TbSustentosEntity> page = tbRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<TbSustentosGetListDto>();
        paginatedDto.setContent(page.getContent().stream().map(tbSustentosBuilder::builderListResponse).collect(Collectors.toList()));

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
