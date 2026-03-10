package com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.builder.TsBancosConciliacionesBuilder;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionListFilterDto;
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
public class TsBancosConciliacionesServiceImpl {

    private final TsBancosConciliacionesRepository bcBancosConciliacionesRepository;
    private final TsBancosConciliacionesBuilder tsBancosConciliacionesBuilder;
    private final AuditorAware<String> auditorAware;

    public BcBancoConciliacionCreationResponseDto create(Long idData, Long idEmpresa, BcBancoConciliacionCreationRequestDto request) {

        return tsBancosConciliacionesBuilder.builderResponse(bcBancosConciliacionesRepository
                .save(tsBancosConciliacionesBuilder.builderEntity(request, idData, idEmpresa)));
    }

    public BcBancoConciliacionCreationResponseDto update(Long idData, Long idEmpresa, UUID id, BcBancoConciliacionCreationRequestDto request) {


        TsBancosConciliacionesEntity entidad = bcBancosConciliacionesRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        TsBancosConciliacionesEntity update = tsBancosConciliacionesBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());

        return tsBancosConciliacionesBuilder.builderResponse(bcBancosConciliacionesRepository
                .save(update));

    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        TsBancosConciliacionesEntity entidad = bcBancosConciliacionesRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        bcBancosConciliacionesRepository.save(entidad);
    }

    public BcBancoConciliacionCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        return tsBancosConciliacionesBuilder.builderResponse(bcBancosConciliacionesRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<BcBancoConciliacionCreationResponseDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                                BcBancoConciliacionListFilterDto filters,
                                                                                Pageable pageable) {


        Page<TsBancosConciliacionesEntity> page = bcBancosConciliacionesRepository.findAllByIdDataAndIdEmpresa
                (idData, idEmpresa, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<BcBancoConciliacionCreationResponseDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(tsBancosConciliacionesBuilder::builderResponse)
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
