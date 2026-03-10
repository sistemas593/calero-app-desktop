package com.calero.lili.api.modClientesTickets;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modClientesTickets.builder.ClienteTicketsBuilder;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsCreationRequestDto;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsGetDtoOne;
import com.calero.lili.api.modClientesTickets.dto.VtClientesTicketsGetListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VtClientesTicketsServiceImpl {

    private final VtClientesTicketsRepository vtClientesTicketsRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final ClienteTicketsBuilder clienteTicketsBuilder;
    private final AuditorAware<String> auditorAware;

    public ResponseDto create(Long idData, VtClientesTicketsCreationRequestDto request) {

        VtClienteTicketsEntity entity = vtClientesTicketsRepository.save(clienteTicketsBuilder
                .builderEntity(request, idData));
        return responseApiBuilder.builderResponse(entity.getIdTicket().toString());

    }

    public ResponseDto update(Long idData, UUID id, VtClientesTicketsCreationRequestDto request) {

        VtClienteTicketsEntity entidad = vtClientesTicketsRepository.findById(idData, id).
                orElseThrow(() -> new GeneralException(MessageFormat.format("idNovedad {0} no exists", id)));

        VtClienteTicketsEntity update =  clienteTicketsBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());

        vtClientesTicketsRepository.save(update);
        return responseApiBuilder.builderResponse(update.getIdTicket().toString());

    }

    public VtClientesTicketsGetDtoOne findByIdNovedad(Long idData, UUID idNovedad) {

        return clienteTicketsBuilder.builderResponse(vtClientesTicketsRepository.findById(idData, idNovedad).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData))));

    }

    public PaginatedDto<VtClientesTicketsGetListDto> findAllPaginate(Long idData, FilterDto filters, Pageable pageable) {


        Page<VtClienteTicketsEntity> page = vtClientesTicketsRepository.findAllPaginate(idData, pageable);
        PaginatedDto paginatedDto = new PaginatedDto<VtClientesTicketsGetListDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(clienteTicketsBuilder::builderListResponse)
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
