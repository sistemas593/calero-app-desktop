package com.calero.lili.core.modTesoreria.modTesoreriaCajas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.builder.TsCajasBuilder;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasResponseDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.BcEntidadesRepository;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
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
public class TsCajasServiceImpl {


    private final TsCajasRepository tsCajasRepository;
    private final TsCajasBuilder tsCajasBuilder;
    private final BcEntidadesRepository bcEntidadesRepository;


    public TsCajasResponseDto create(Long idData, Long idEmpresa,
                                     TsCajasRequestDto request, String usuario) {

        Optional<TsCajasEntity> cajaExiste = tsCajasRepository
                .findByCodigoCaja(idData, idEmpresa, request.getCodigoCaja());

        if (cajaExiste.isPresent()) {
            throw new GeneralException(MessageFormat.format("La caja con código {0}, ya existe", request.getCodigoCaja()));
        }

        TsEntidadEntity entidadBanco = bcEntidadesRepository.findById(idData, idEmpresa, request.getIdEntidad())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La entidad bancaria con el id {0}, no existe ", request.getIdEntidad())));

        TsCajasEntity caja = tsCajasBuilder.builderEntity(idData, idEmpresa, request);
        caja.setEntidad(entidadBanco);
        caja.setCreatedBy(usuario);
        caja.setCreatedDate(LocalDateTime.now());


        return tsCajasBuilder.builderResponse(tsCajasRepository.save(caja));

    }


    public TsCajasResponseDto update(Long idData, Long idEmpresa, UUID idCaja,
                                     TsCajasRequestDto request, String usuario) {

        TsCajasEntity caja = tsCajasRepository
                .findById(idData, idEmpresa, idCaja)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La caja con id {0}, no existe", idEmpresa)));

        TsEntidadEntity entidadBanco = bcEntidadesRepository.findById(idData, idEmpresa, request.getIdEntidad())
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La entidad bancaria con el id {0}, no existe ", request.getIdEntidad())));

        TsCajasEntity update = tsCajasBuilder.builderUpdateEntity(request, caja);
        update.setEntidad(entidadBanco);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return tsCajasBuilder.builderResponse(tsCajasRepository.save(update));

    }


    public void delete(Long idData, Long idEmpresa, UUID idEntidad, String usuario) {
        TsCajasEntity caja = tsCajasRepository
                .findById(idData, idEmpresa, idEntidad)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La caja con id {0}, no existe", idEmpresa)));

        caja.setDelete(Boolean.TRUE);
        caja.setDeletedBy(usuario);
        caja.setDeletedDate(LocalDateTime.now());

        tsCajasRepository.save(caja);
    }


    public TsCajasResponseDto findById(Long idData, Long idEmpresa, UUID idEntidad) {
        return tsCajasBuilder.builderResponse(tsCajasRepository
                .findById(idData, idEmpresa, idEntidad)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("La caja con id {0}, no existe", idEmpresa))));
    }


    public PaginatedDto<TsCajasResponseDto> findAll(Long idData, Long idEmpresa, Pageable pageable) {
        Page<TsCajasEntity> page = tsCajasRepository.findAllPaginate(idData, idEmpresa, pageable);

        List<TsCajasResponseDto> dtoList = page.stream()
                .map(tsCajasBuilder::builderResponse)
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
