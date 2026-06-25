package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTerceros.GeTercerosRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.TsComprobanteSecuenciasRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.builder.TsComprobanteBuilder;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.TsComprobanteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TsComprobanteServiceImpl {

    private final TsComprobanteRepository tsComprobanteRepository;
    private final TsComprobanteBuilder tsComprobanteBuilder;
    private final GeTercerosRepository geTercerosRepository;
    private final TsCajasRepository tsCajasRepository;
    private final TsComprobanteSecuenciasRepository tsComprobanteSecuenciasRepository;


    @Transactional
    public TsComprobanteResponseDto create(Long idData, Long idEmpresa, TsComprobanteCreationRequestDto request, String usuario) {

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El tercero con id {0} no existe ", request.getIdTercero())));

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCaja())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La caja con id {0}, no existe", request.getIdCaja())));

        Integer ultimoNumero = tsComprobanteSecuenciasRepository.actualizarUltimoNumeroSecuencia(idData, idEmpresa,
                request.getIdCaja(), request.getTipoComprobante().name(), request.getAnio());

        TsComprobantesEntity bancosMovimentos = tsComprobanteBuilder.builderEntity(request, idData, idEmpresa);

        if (Objects.isNull(ultimoNumero)) {
            throw new GeneralException("El secuencial para el número de comprobante no existe");
        }

        bancosMovimentos.setNumeroComprobante("00000001");
        bancosMovimentos.setTercero(tercero);
        bancosMovimentos.setCaja(caja);
        bancosMovimentos.setCreatedBy(usuario);
        bancosMovimentos.setCreatedDate(LocalDateTime.now());

        return tsComprobanteBuilder.builderResponse(tsComprobanteRepository.save(bancosMovimentos));

    }

    public TsComprobanteResponseDto update(Long idData, Long idEmpresa, UUID id,
                                           TsComprobanteCreationRequestDto request, String usuario) {

        TsComprobantesEntity entidad = tsComprobanteRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Comprobante con id {0} no existe", id)));

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El tercero con id {0} no existe ", request.getIdTercero())));

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCaja())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La caja con id {0}, no existe", request.getIdCaja())));

        TsComprobantesEntity bancosMovimentos = tsComprobanteBuilder.builderUpdateEntity(request, entidad);
        bancosMovimentos.setNumeroComprobante(entidad.getNumeroComprobante());
        bancosMovimentos.setModifiedBy(usuario);
        bancosMovimentos.setModifiedDate(LocalDateTime.now());
        bancosMovimentos.setTercero(tercero);
        bancosMovimentos.setCaja(caja);

        return tsComprobanteBuilder.builderResponse(tsComprobanteRepository.save(bancosMovimentos));
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        TsComprobantesEntity entidad = tsComprobanteRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Comprobante con id {0} no existe", id)));

        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());
        entidad.setDelete(Boolean.TRUE);

        tsComprobanteRepository.save(entidad);

    }

    public TsComprobanteResponseDto findById(Long idData, Long idEmpresa, UUID id) {

        return tsComprobanteBuilder.builderResponse(tsComprobanteRepository.findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<TsComprobanteResponseDto> findAllPaginate(Long idData, Long idEmpresa,
                                                                  Pageable pageable) {

        Page<TsComprobantesEntity> page = tsComprobanteRepository
                .findAllByIdDataAndIdEmpresa(idData, idEmpresa, pageable);

        PaginatedDto paginatedDto = new PaginatedDto<TsComprobanteResponseDto>();
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
