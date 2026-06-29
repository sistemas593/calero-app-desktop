package com.calero.lili.core.modContabilidad.modSecuenciales;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modContabilidad.modSecuenciales.builder.CnSecuenciasBuilder;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasRequestDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasResponseDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.projection.CnSecuenciasProjection;
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
public class CnSecuenciasServiceImpl {


    private final CnSecuenciasRepository cnSecuenciasRepository;
    private final CnSecuenciasBuilder cnSecuenciasBuilder;

    public CnSecuenciasResponseDto create(Long idData, Long idEmpresa,
                                          CnSecuenciasRequestDto request, String usuario) {

        Optional<CnSecuenciasProjection> exits = cnSecuenciasRepository.findByAnioMesSucursal(idData, idEmpresa,
                request.getSucursal(), request.getAnio(), request.getMes());

        if (exits.isPresent()) {
            throw new GeneralException(MessageFormat.format("El secuencial con sucursal: {0}, año: {1}, mes: {2} ya existe", request.getSucursal(), request.getAnio(), request.getMes()));
        }

        CnSecuenciasEntity secuencias = cnSecuenciasBuilder.builderEntity(idData, idEmpresa, request);
        secuencias.setCreatedBy(usuario);
        secuencias.setCreatedDate(LocalDateTime.now());
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.save(secuencias));
    }


    public CnSecuenciasResponseDto update(Long idData, Long idEmpresa, UUID idSecuencia,
                                          CnSecuenciasRequestDto request, String usuario) {

        CnSecuenciasEntity secuencias = cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0} "
                        , idSecuencia)));

        Optional<CnSecuenciasProjection> exits = cnSecuenciasRepository.findByAnioMesSucursal(idData, idEmpresa,
                request.getSucursal(), request.getAnio(), request.getMes());

        if (exits.isPresent()) {

            if (!exits.get().getIdSecuencia().equals(idSecuencia)) {
                throw new GeneralException(MessageFormat.format("El secuencial con sucursal: {0}, año: {1}," +
                        " mes: {2} ya existe", request.getSucursal(), request.getAnio(), request.getMes()));
            }

        }

        CnSecuenciasEntity update = cnSecuenciasBuilder.builderUpdateEntity(secuencias, request);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.save(update));
    }

    public void delete(Long idData, Long idEmpresa, UUID idSecuencia, String usuario) {

        CnSecuenciasEntity secuencias = cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0}"
                        , idSecuencia)));

        secuencias.setDelete(Boolean.TRUE);
        secuencias.setDeletedBy(usuario);
        secuencias.setDeletedDate(LocalDateTime.now());
        cnSecuenciasRepository.save(secuencias);
    }


    public CnSecuenciasResponseDto findById(Long idData, Long idEmpresa, UUID idSecuencia) {
        return cnSecuenciasBuilder.builderResponse(cnSecuenciasRepository.findById(idData, idEmpresa, idSecuencia)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe secuencia con id {0}"
                        , idSecuencia))));
    }


    public PaginatedDto<CnSecuenciasResponseDto> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable) {
        Page<CnSecuenciasEntity> page = cnSecuenciasRepository.getFindAllPaginate(idData, idEmpresa, pageable);

        List<CnSecuenciasResponseDto> dtoList = page.stream().map(cnSecuenciasBuilder::builderResponse).toList();

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
