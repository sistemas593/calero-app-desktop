package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.builder.TsEntidadBuilder;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadReportDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.dto.BcEntidadesListFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BcEntidadServiceImpl {

    private final BcEntidadesRepository bcEntidadesRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final TsEntidadBuilder tsEntidadBuilder;
    private final AuditorAware<String> auditorAware;

    public ResponseDto create(Long idData, Long idEmpresa, BcEntidadCreationRequestDto request) {

        TsEntidadEntity entidad = bcEntidadesRepository.save(tsEntidadBuilder
                .builderEntity(request, idData, idEmpresa));
        return responseApiBuilder.builderResponse(entidad.getIdEntidad().toString());
    }

    public ResponseDto update(Long idData, Long idEmpresa, UUID id, BcEntidadCreationRequestDto request) {

        TsEntidadEntity entidad = bcEntidadesRepository.findByIdDataAndIdEmpresaAndIdEntidad(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        TsEntidadEntity update = tsEntidadBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());

        bcEntidadesRepository.save(update);
        return responseApiBuilder.builderResponse(entidad.getIdEntidad().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        TsEntidadEntity entidad = bcEntidadesRepository.findByIdDataAndIdEmpresaAndIdEntidad(idData, idEmpresa, id);
        if (Objects.isNull(entidad)) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        bcEntidadesRepository.save(entidad);

    }

    public BcEntidadCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        TsEntidadEntity entidad = bcEntidadesRepository.findByIdDataAndIdEmpresaAndIdEntidad(idData, idEmpresa, id);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        return tsEntidadBuilder.builderResponse(entidad);
    }

    public PaginatedDto<BcEntidadReportDto> findAllPaginate(Long idData, Long idEmpresa, BcEntidadesListFilterDto filters, Pageable pageable) {

        Page<BcEntidadesProjection> page = bcEntidadesRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "",
                filters.getTipoEntidad(),
                pageable);

        PaginatedDto paginatedDto = new PaginatedDto<BcEntidadReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(tsEntidadBuilder::builderListResponse)
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
