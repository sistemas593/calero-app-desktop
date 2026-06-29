package com.calero.lili.core.adConfiguracion;

import com.calero.lili.core.adConfiguracion.builder.AdMailEnviadosBuilder;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosResponseDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdMailEnviadosServiceImpl {


    private final AdMailsEnviadosRepository adMailsEnviadosRepository;
    private final AdMailEnviadosBuilder adMailEnviadosBuilder;


    public PaginatedDto<AdMailEnviadosResponseDto> findAllPaginate(FilterMailEnviadosDto filter, Pageable pageable) {

        Page<AdMailEnviadosEntity> page = adMailsEnviadosRepository.findAllPaginate(filter.getClave1(), filter.getCodigoDocumento(),
                filter.getSerie(), filter.getSecuencial(), filter.getCorreo(),
                filter.getFechaInicial(), filter.getFechaFinal(), pageable);


        List<AdMailEnviadosResponseDto> dtoList = page.stream().map(adMailEnviadosBuilder::builderResponse).toList();

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

    }


}
