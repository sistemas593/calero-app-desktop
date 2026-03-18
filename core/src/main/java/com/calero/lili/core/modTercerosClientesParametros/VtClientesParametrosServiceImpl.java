package com.calero.lili.core.modTercerosClientesParametros;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTercerosClientesParametros.builder.ClienteParametrosBuilder;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroCreationRequestDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroCreationResponseDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroListFilterDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VtClientesParametrosServiceImpl {

    private final VtClientesParametrosRepository vtClientesParametrosRepository;
    private final ClienteParametrosBuilder clienteParametrosBuilder;


    public VtClienteParametroCreationResponseDto create(Long idData, Long idEmpresa,
                                                        VtClienteParametroCreationRequestDto request) {

        return clienteParametrosBuilder
                .builderResponse(vtClientesParametrosRepository.save(clienteParametrosBuilder
                        .builderEntity(request, idData, idEmpresa)));
    }

    public VtClienteParametroCreationResponseDto update(Long idData, Long idEmpresa,
                                                        UUID id, VtClienteParametroCreationRequestDto request) {

        VtClienteParametroEntity entidad = vtClientesParametrosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        return clienteParametrosBuilder
                .builderResponse(vtClientesParametrosRepository.save(clienteParametrosBuilder
                        .builderUpdateEntity(request, entidad)));

    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        vtClientesParametrosRepository.deleteByIdDataAndIdParametro(idData, idEmpresa, id);
    }

    public VtClienteParametroCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        return clienteParametrosBuilder.builderResponse(vtClientesParametrosRepository
                .findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<VtClienteParametroReportDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                     VtClienteParametroListFilterDto filters, Pageable pageable) {

        Page<VtClienteParametroEntity> page = vtClientesParametrosRepository.findAllPaginate(idData, idEmpresa, pageable);
        PaginatedDto paginatedDto = new PaginatedDto<VtClienteParametroReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(clienteParametrosBuilder::builderListResponse)
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
        paginated.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginated);

        return paginatedDto;
    }
}
