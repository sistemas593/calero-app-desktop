package com.calero.lili.api.tablas.tbFormasPagoSri;

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
public class TbFormasPagoSriServiceImpl {

    private final TbFormasPagoSriRepository tbRepository;
    private final ResponseApiBuilder responseApiBuilder;

    public ResponseDto create(TbFormaPagoSriCreationRequestDto request) {
        Optional<TbFormaPagoSriEntity> existing = tbRepository.findById(request.getCodigoFormaPagoSri());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Documento {0} ya existe", request.getCodigoFormaPagoSri()));
        }
        TbFormaPagoSriEntity entidad = new TbFormaPagoSriEntity();
        entidad.setCodigoFormaPagoSri(request.getCodigoFormaPagoSri());
        return toEntity(entidad, request);
    }

    public ResponseDto update(String id, TbFormaPagoSriCreationRequestDto request) {
        TbFormaPagoSriEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return toEntity(entidad, request);
    }

    public void delete(String codigoDocumento) {
        tbRepository.deleteById(codigoDocumento);
    }

    public TbFormaPagoSriGetOneDto findById(String id) {
        TbFormaPagoSriEntity entidad = tbRepository.findById(id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento {0} no exists", id)));
        return toDto(entidad);
    }

    public PaginatedDto<TbFormaPagoSriGetListDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<TbFormaPagoSriEntity> page = tbRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<TbFormaPagoSriGetListDto>();
        paginatedDto.setContent(page.getContent().stream().map(this::toDto).collect(Collectors.toList()));

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

    private ResponseDto toEntity(TbFormaPagoSriEntity entidad, TbFormaPagoSriCreationRequestDto request) {
        entidad.setFormaPagoSri(request.getFormaPagoSri());
        tbRepository.save(entidad);
        return responseApiBuilder.builderResponse(entidad.getCodigoFormaPagoSri().toString());
    }

    private TbFormaPagoSriGetOneDto toDto(TbFormaPagoSriEntity entity) {
        TbFormaPagoSriGetOneDto dto = new TbFormaPagoSriGetOneDto();
        dto.setCodigoFormaPagoSri(entity.getCodigoFormaPagoSri());
        dto.setFormaPagoSri(entity.getFormaPagoSri());
        return dto;
    }

}
