package com.calero.lili.api.adEmpresasPeriodo;

import com.calero.lili.api.adEmpresasPeriodo.builder.AdEmpresasPeriodoBuilder;
import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationRequestDto;
import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationResponseDto;
import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoListFilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdEmpresasPeriodosServiceImpl {

    private final AdEmpresasPeriodosRepository adEmpresasPeriodosRepository;
    private final AdEmpresasPeriodoBuilder adEmpresasPeriodoBuilder;

    public AdEmpresaPeriodoCreationResponseDto create(Long idData,
                                                      Long idEmpresa, AdEmpresaPeriodoCreationRequestDto request) {
        return adEmpresasPeriodoBuilder
                .builderResponse(adEmpresasPeriodoBuilder.builderEntity(request, idData, idEmpresa));
    }

    public AdEmpresaPeriodoCreationResponseDto update(Long idData, Long idEmpresa,
                                                      UUID idPeriodo, AdEmpresaPeriodoCreationRequestDto request) {

        AdEmpresaPeriodoEntity entidad = adEmpresasPeriodosRepository
                .findIdPeriodoAndIdDataAndIdEmpresa(idPeriodo, idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idPeriodo)));

        return adEmpresasPeriodoBuilder
                .builderResponse(adEmpresasPeriodoBuilder.builderUpdateEntity(request, entidad));
    }

    public void delete(Long idData, Long idEmpresa, UUID idPeriodo) {
        AdEmpresaPeriodoEntity entidad = adEmpresasPeriodosRepository
                .findIdPeriodoAndIdDataAndIdEmpresa(idPeriodo, idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idPeriodo)));

        adEmpresasPeriodosRepository.delete(entidad);
    }

    public AdEmpresaPeriodoCreationResponseDto findFirstById(UUID idPeriodo) {
        return adEmpresasPeriodoBuilder
                .builderResponse(adEmpresasPeriodosRepository.findIdPeriodo(idPeriodo)
                        .orElseThrow(() -> new GeneralException
                                (MessageFormat.format("No existe registro para el id : {0}", idPeriodo))));
    }

    public PaginatedDto<AdEmpresaPeriodoCreationResponseDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                             AdEmpresaPeriodoListFilterDto filters,
                                                                             Pageable pageable) {


        Page<AdEmpresaPeriodoEntity> page = adEmpresasPeriodosRepository.findAllPaginate(idData, idEmpresa, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaPeriodoCreationResponseDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adEmpresasPeriodoBuilder::builderResponse)
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
