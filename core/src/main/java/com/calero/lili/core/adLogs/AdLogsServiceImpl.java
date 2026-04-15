package com.calero.lili.core.adLogs;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import com.calero.lili.core.dtos.FilterFechasDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdLogsServiceImpl {

    private final AdLogsBuilder adLogsBuilder;
    private final AdLogsRepository adLogsRepository;

    public void saveLog(AdLogsRequestDto model, String mensajes, String tipo) {
        adLogsRepository.save(adLogsBuilder.builderLog(model, mensajes, tipo));
    }


    public PaginatedDto<AdLogsDtoResponse> findAllPageable(Long idData, Long idEmpresa, FilterFechasDto filters, Pageable pageable) {

        Page<AdLogsEntity> page = adLogsRepository.findAllByFecha(idData, idEmpresa,
                filters.getFechaDesde(), filters.getFechaHasta(), filters.getTipo(), pageable);

        List<AdLogsDtoResponse> list = page.getContent().stream()
                .map(adLogsBuilder::builderResponse).toList();

        PaginatedDto paginatedDto = new PaginatedDto();
        paginatedDto.setContent(list);

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
