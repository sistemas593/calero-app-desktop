package com.calero.lili.api.tablas.tbDocumentos;

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
public class TbDocumentosServiceImpl {

    private final TbDocumentosRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TbDocumentoBuilder tbDocumentoBuilder;

    public ResponseDto create(TbDocumentosCreationRequestDto request) {
        Optional<TbDocumentoEntity> existing = tbRepository.findById(request.getCodigoDocumento());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigoDocumento()));
        }

        return responseApiBuilder.builderResponse(tbRepository.save(tbDocumentoBuilder.builderEntity(request)).getCodigoDocumento());
    }

    public ResponseDto update(String id, TbDocumentosCreationRequestDto request) {
        TbDocumentoEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return responseApiBuilder.builderResponse(tbRepository.save(tbDocumentoBuilder
                .builderUpdateEntity(request, entidad)).getCodigoDocumento());
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbDocumentosGetOneDto findById(String id) {
        TbDocumentoEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return tbDocumentoBuilder.builderResponse(entidad);
    }

    public PaginatedDto<TbDocumentosGetListDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<TbDocumentoEntity> page = tbRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<TbDocumentosGetListDto>();
        paginatedDto.setContent(page.getContent().stream().map(tbDocumentoBuilder::builderListResponse).collect(Collectors.toList()));

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
