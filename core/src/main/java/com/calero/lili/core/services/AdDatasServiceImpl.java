package com.calero.lili.core.services;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.GeneralException;
import com.calero.lili.core.builders.AdDataBuilder;
import com.calero.lili.core.dtos.AdDatasCreationRequestDto;
import com.calero.lili.core.dtos.AdDatasDto;
import com.calero.lili.core.entities.AdDataEntity;
import com.calero.lili.core.repositories.AdDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdDatasServiceImpl {

    private final AdDataRepository adDataRepository;
    private final AdDataBuilder adDataBuilder;

    public void create(AdDatasCreationRequestDto request) {
        adDataRepository.save(adDataBuilder.builderEntity(request));
    }

    public void update(Long idData, AdDatasCreationRequestDto request) {
        AdDataEntity entidad = adDataRepository.findByIdData(idData).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData)));
        adDataRepository.save(adDataBuilder.builderUpdateEntity(request, entidad));
    }

    public AdDatasDto findByIdData(Long idData) {
        AdDataEntity entidad = adDataRepository.findById(idData).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Data {0} no exists", idData)));
        return adDataBuilder.builderResponse(entidad);
    }

    public PaginatedDto<AdDatasDto> findAllPaginate(FilterDto filters, Pageable pageable) {
        Page<AdDataEntity> page = adDataRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<AdDatasDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adDataBuilder::builderResponse)
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
