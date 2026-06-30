package com.calero.lili.core.adInfoAdicional;

import com.calero.lili.core.adInfoAdicional.builder.AdInfoAdicionalBuilder;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalRequestDto;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalResponseDto;
import com.calero.lili.core.adInfoAdicional.projection.OneAdInfoProjection;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
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
public class AdInfoAdicionalServiceImpl {

    private final AdInfoAdicionalBuilder adInfoAdicionalBuilder;
    private final AdInfoAdicionalRepository adInfoAdicionalRepository;


    public AdInfoAdicionalResponseDto create(Long idData, Long idEmpresa,
                                             AdInfoAdicionalRequestDto request, String usuario) {

        Optional<OneAdInfoProjection> entidad = adInfoAdicionalRepository.findByTipoDocumento(idData, idEmpresa, request.getDocumento().name());

        if (entidad.isPresent()) {
            throw new GeneralException(MessageFormat.format("La información adicional del tipo" +
                    " documento {0}, ya existe", request.getDocumento().name()));
        }

        AdInfoAdicionalEntity entity = adInfoAdicionalBuilder.builderEntity(idData, idEmpresa, request);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.save(entity));
    }


    public AdInfoAdicionalResponseDto update(Long idData, Long idEmpresa, UUID idInfoAdicional,
                                             AdInfoAdicionalRequestDto request, String usuario) {

        AdInfoAdicionalEntity entidad = adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional)));

        if (!entidad.getDocumento().equals(request.getDocumento())) {

            Optional<OneAdInfoProjection> entity = adInfoAdicionalRepository.findByTipoDocumento(idData, idEmpresa, request.getDocumento().name());
            if (entity.isPresent()) {
                throw new GeneralException(MessageFormat.format("La información adicional del tipo" +
                        " documento {0}, ya existe", request.getDocumento().name()));
            }
        }

        AdInfoAdicionalEntity update = adInfoAdicionalBuilder.builderUpdateEntity(request, entidad);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());
        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.save(update));
    }


    public AdInfoAdicionalResponseDto findById(Long idData, Long idEmpresa, UUID idInfoAdicional) {

        return adInfoAdicionalBuilder.builderResponse(adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional))));
    }


    public PaginatedDto<AdInfoAdicionalResponseDto> findAll(Long idData, Long idEmpresa, Pageable pageable) {
        Page<AdInfoAdicionalEntity> page = adInfoAdicionalRepository.findAll(idData, idEmpresa, pageable);

        List<AdInfoAdicionalResponseDto> dtoList = page.stream()
                .map(adInfoAdicionalBuilder::builderResponse)
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

    public void delete(Long idData, Long idEmpresa, UUID idInfoAdicional, String usuario) {

        AdInfoAdicionalEntity entidad = adInfoAdicionalRepository.findByIdInfoAdicional(idData, idEmpresa, idInfoAdicional)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La información adicional con id {0}, no existe", idInfoAdicional)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        adInfoAdicionalRepository.save(entidad);
    }


}
