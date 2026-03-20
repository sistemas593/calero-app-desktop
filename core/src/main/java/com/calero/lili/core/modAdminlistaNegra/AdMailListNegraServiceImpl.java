package com.calero.lili.core.modAdminlistaNegra;

import com.calero.lili.core.modAdminlistaNegra.dto.FilterMailBlackDto;
import com.calero.lili.core.modAdminlistaNegra.dto.MailBlackResponseDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdMailListNegraServiceImpl {

    private final AdMailsListaNegraRepository adMailsListaNegraRepository;
    private final AdMailsListaNegraBuilder adMailsListaNegraBuilder;


    public PaginatedDto<MailBlackResponseDto> getAll(FilterMailBlackDto filters, Pageable pageable) {

        Page<AdMailListaNegraEntity> page = adMailsListaNegraRepository.getFindAll(filters.getFechaDesde(),
                filters.getFechaHasta(), filters.getEmail(), pageable);

        List<MailBlackResponseDto> dtoList = page
                .stream()
                .map(adMailsListaNegraBuilder::builderResponse)
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

    }

    public MailBlackResponseDto findByEmail(FilterMailBlackDto filters) {
        return adMailsListaNegraBuilder.builderResponse(adMailsListaNegraRepository.findByEmail(filters.getEmail())
                .orElseThrow(() -> new GeneralException("No existe referencia a ese correo")));
    }

    public void delete(FilterMailBlackDto filters) {

        AdMailListaNegraEntity email = adMailsListaNegraRepository.findByEmail(filters.getEmail())
                .orElseThrow(() -> new GeneralException("No existe referencia a ese correo"));
        adMailsListaNegraRepository.delete(email);
    }

}
