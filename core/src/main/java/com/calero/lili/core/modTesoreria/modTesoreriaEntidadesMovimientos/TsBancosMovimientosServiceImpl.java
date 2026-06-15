package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder.TsComprobanteBuilder;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteResponseDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoListFilterDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoReportDto;
import lombok.RequiredArgsConstructor;
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

    private final TsComprobanteRepository bcBancosMovimientosRepository;
    private final TsComprobanteBuilder tsComprobanteBuilder;
    private final GeTercerosRepository geTercerosRepository;


    public TsComprobanteResponseDto create(Long idData, Long idEmpresa, TsComprobanteCreationRequestDto request, String usuario) {

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        TsComprobantesEntity bancosMovimentos = tsComprobanteBuilder.builderEntity(request, idData, idEmpresa);
        bancosMovimentos.setTercero(tercero);
        bancosMovimentos.setCreatedBy(usuario);
        bancosMovimentos.setCreatedDate(LocalDateTime.now());

        return tsComprobanteBuilder.builderResponse(bcBancosMovimientosRepository
                .save(bancosMovimentos));
    }

    public TsComprobanteResponseDto update(Long idData, Long idEmpresa, UUID id, TsComprobanteCreationRequestDto request, String usuario) {

        TsComprobantesEntity entidad = bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        TsComprobantesEntity bancosMovimentos = tsComprobanteBuilder.builderUpdateEntity(request, entidad);
        bancosMovimentos.setModifiedBy(usuario);
        bancosMovimentos.setModifiedDate(LocalDateTime.now());

        bancosMovimentos.setTercero(tercero);


        return tsComprobanteBuilder.builderResponse(bcBancosMovimientosRepository
                .save(bancosMovimentos));
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        TsComprobantesEntity entidad = bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());
        entidad.setDelete(Boolean.TRUE);

        bcBancosMovimientosRepository.save(entidad);

    }

    public TsComprobanteResponseDto findById(Long idData, Long idEmpresa, UUID id) {

        return tsComprobanteBuilder.builderResponse(bcBancosMovimientosRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<TsComprobanteResponseDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                  BcBancoMovimientoListFilterDto filters,
                                                                  Pageable pageable) {

        Page<TsComprobantesEntity> page = bcBancosMovimientosRepository
                .findAllByIdDataAndIdEmpresa(idData, idEmpresa, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<BcBancoMovimientoReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(tsComprobanteBuilder::builderResponse)
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
