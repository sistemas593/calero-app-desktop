package com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.builder.TsComprobanteSecuenciaBuilder;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TsComprobanteSecuenciaServiceImpl {

    private final TsComprobanteSecuenciasRepository tsComprobanteSecuenciasRepository;
    private final TsComprobanteSecuenciaBuilder tsComprobanteSecuenciaBuilder;
    private final TsCajasRepository tsCajasRepository;

    public TsComprobanteSecuenciaResponseDto create(Long idData, Long idEmpresa,
                                                    TsComprobanteSecuenciaRequestDto request, String usuario) {

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCajas())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja con id {0}, no existe", request.getIdCajas())));

        Optional<TsComprobantesSecuenciasEntity> exists = tsComprobanteSecuenciasRepository
                .findByIdCajaAndTipoAndAnio(idData, idEmpresa, request.getIdCajas(), request.getTipo(), request.getAnio());

        if (exists.isPresent()) {
            throw new GeneralException(MessageFormat.format("La secuencia de comprobante para la caja {0}, tipo {1} y año {2}" +
                    ", ya existe", request.getIdCajas(), request.getTipo(), request.getAnio()));
        }

        TsComprobantesSecuenciasEntity comprobanteSecuencia = tsComprobanteSecuenciaBuilder.builderEntity(idData, idEmpresa, request);
        comprobanteSecuencia.setCajas(caja);
        comprobanteSecuencia.setCreatedBy(usuario);
        comprobanteSecuencia.setCreatedDate(LocalDateTime.now());

        return tsComprobanteSecuenciaBuilder.builderResponse(tsComprobanteSecuenciasRepository.save(comprobanteSecuencia));
    }

    public TsComprobanteSecuenciaResponseDto update(Long idData, Long idEmpresa, UUID idComprobanteSecuencia,
                                                    TsComprobanteSecuenciaRequestDto request, String usuario) {

        TsCajasEntity caja = tsCajasRepository.findById(idData, idEmpresa, request.getIdCajas())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja con id {0}, no existe", request.getIdCajas())));

        TsComprobantesSecuenciasEntity exists = tsComprobanteSecuenciasRepository
                .findById(idData, idEmpresa, idComprobanteSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La secuencia de comprobante con id {0}, no existe", idComprobanteSecuencia)));

        TsComprobantesSecuenciasEntity comprobanteSecuencia = tsComprobanteSecuenciaBuilder.builderUpdateEntity(request, exists);
        comprobanteSecuencia.setCajas(caja);
        comprobanteSecuencia.setModifiedBy(usuario);
        comprobanteSecuencia.setModifiedDate(LocalDateTime.now());

        return tsComprobanteSecuenciaBuilder.builderResponse(tsComprobanteSecuenciasRepository.save(comprobanteSecuencia));
    }

    public void delete(Long idData, Long idEmpresa, UUID idComprobanteSecuencia, String usuario) {

        TsComprobantesSecuenciasEntity comprobanteSecuencia = tsComprobanteSecuenciasRepository
                .findById(idData, idEmpresa, idComprobanteSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La secuencia de comprobante con id {0}, no existe", idComprobanteSecuencia)));

        comprobanteSecuencia.setDelete(Boolean.TRUE);
        comprobanteSecuencia.setDeletedBy(usuario);
        comprobanteSecuencia.setDeletedDate(LocalDateTime.now());

        tsComprobanteSecuenciasRepository.save(comprobanteSecuencia);
    }

    public TsComprobanteSecuenciaResponseDto findById(Long idData, Long idEmpresa, UUID idComprobanteSecuencia) {

        return tsComprobanteSecuenciaBuilder.builderResponse(tsComprobanteSecuenciasRepository
                .findById(idData, idEmpresa, idComprobanteSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La secuencia de comprobante con id {0}, no existe", idComprobanteSecuencia))));
    }

    public PaginatedDto<TsComprobanteSecuenciaResponseDto> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable) {

        Page<TsComprobantesSecuenciasEntity> page = tsComprobanteSecuenciasRepository
                .findAllPaginate(idData, idEmpresa, pageable);

        List<TsComprobanteSecuenciaResponseDto> dtoList = page.stream().map(tsComprobanteSecuenciaBuilder::builderResponse)
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

}
