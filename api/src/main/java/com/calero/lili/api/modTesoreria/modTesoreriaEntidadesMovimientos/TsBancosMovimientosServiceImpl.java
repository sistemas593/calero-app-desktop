package com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.builder.TsBancosMovimentosBuilder;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoListFilterDto;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoReportDto;
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
public class TsBancosMovimientosServiceImpl {

    private final TsBancosMovimientosRepository bcBancosMovimientosRepository;
    private final TsBancosMovimentosBuilder tsBancosMovimentosBuilder;
    private final GeTercerosRepository geTercerosRepository;
    private final AuditorAware<String> auditorAware;


    public BcBancoMovimientoCreationResponseDto create(Long idData, Long idEmpresa, BcBancoMovimientoCreationRequestDto request) {

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        TsBancosMovimentosEntity bancosMovimentos = tsBancosMovimentosBuilder.builderEntity(request, idData, idEmpresa);
        bancosMovimentos.setVtClienteEntity(tercero);

        return tsBancosMovimentosBuilder.builderResponse(bcBancosMovimientosRepository
                .save(bancosMovimentos));
    }

    public BcBancoMovimientoCreationResponseDto update(Long idData, Long idEmpresa, UUID id, BcBancoMovimientoCreationRequestDto request) {

        TsBancosMovimentosEntity entidad = bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        TsBancosMovimentosEntity bancosMovimentos = tsBancosMovimentosBuilder.builderUpdateEntity(request, entidad);
        bancosMovimentos.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        bancosMovimentos.setModifiedDate(LocalDateTime.now());

        bancosMovimentos.setVtClienteEntity(tercero);


        return tsBancosMovimentosBuilder.builderResponse(bcBancosMovimientosRepository
                .save(bancosMovimentos));
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        TsBancosMovimentosEntity entidad = bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        bcBancosMovimientosRepository.save(entidad);

    }

    public BcBancoMovimientoCreationResponseDto findById(Long idData, Long idEmpresa, UUID id) {

        return tsBancosMovimentosBuilder.builderResponse(bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<BcBancoMovimientoCreationResponseDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                              BcBancoMovimientoListFilterDto filters,
                                                                              Pageable pageable) {

        Page<TsBancosMovimentosEntity> page = bcBancosMovimientosRepository
                .findAllByIdDataAndIdEmpresa(idData, idEmpresa, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<BcBancoMovimientoReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(tsBancosMovimentosBuilder::builderResponse)
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
