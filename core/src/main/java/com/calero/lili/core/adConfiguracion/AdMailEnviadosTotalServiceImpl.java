package com.calero.lili.core.adConfiguracion;

import com.calero.lili.core.adConfiguracion.builder.AdMailBuilderTotalBuilder;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosTotalResponseDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosTotalesDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosTotalEntity;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdMailEnviadosTotalServiceImpl {


    private final AdMailsEnviadosTotalRepository adMailsEnviadosTotalRepository;
    private final AdMailBuilderTotalBuilder adMailBuilderTotalBuilder;

    /*public PaginatedDto<AdMailEnviadosTotalResponseDto> findAllPaginate(FilterMailEnviadosTotalesDto filter, Pageable pageable) {

        Page<AdMailEnviadosTotalEntity> page = adMailsEnviadosTotalRepository.findAllPaginate(filter.getClave1(),
                filter.getPeriodo(), pageable);

        List<AdMailEnviadosTotalResponseDto> dtoList = page.stream().map(adMailBuilderTotalBuilder::builderResponse)
                .toList();


        PaginatedDto paginatedDto = new PaginatedDto();
        paginatedDto.setContent(dtoList);

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

    }*/


}
