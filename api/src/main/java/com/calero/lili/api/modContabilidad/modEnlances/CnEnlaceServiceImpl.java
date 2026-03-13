package com.calero.lili.api.modContabilidad.modEnlances;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modContabilidad.modEnlances.builder.CnEnlaceBuilder;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceRequestDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceResponseDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.EnlaceFilterDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetListDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CnEnlaceServiceImpl {


    private final CnEnlacesGeneralesRepository cnEnlacesGeneralesRepository;
    private final CnEnlaceBuilder cnEnlaceBuilder;


    public CnEnlaceResponseDto create(CnEnlaceRequestDto request, String usuario) {
        CnEnlacesGeneralesEntity entity = cnEnlaceBuilder.builderEntity(request);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return cnEnlaceBuilder.builderResponse(cnEnlacesGeneralesRepository.save(entity));
    }

    public CnEnlaceResponseDto update(UUID idEnlace, CnEnlaceRequestDto request, String usuario) {

        CnEnlacesGeneralesEntity enlace = cnEnlacesGeneralesRepository.findByIdEnlace(idEnlace)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El enlace con id {0} no existe", idEnlace)));

        CnEnlacesGeneralesEntity updated = cnEnlaceBuilder.builderUpdate(request, enlace);

        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());

        return cnEnlaceBuilder.builderResponse(cnEnlacesGeneralesRepository.save(updated));

    }

    public void delete(UUID idEnlace, String usuario) {

        CnEnlacesGeneralesEntity enlace = cnEnlacesGeneralesRepository.findByIdEnlace(idEnlace)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El enlace con id {0} no existe", idEnlace)));

        enlace.setDeletedBy(usuario);
        enlace.setDeletedDate(LocalDateTime.now());
        enlace.setDelete(Boolean.TRUE);

        cnEnlacesGeneralesRepository.save(enlace);
    }


    public CnEnlaceResponseDto findById(UUID idEnlace) {
        return cnEnlaceBuilder.builderResponse(cnEnlacesGeneralesRepository.findByIdEnlace(idEnlace)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El enlace con id {0} no existe", idEnlace))));
    }

    public PaginatedDto<CnEnlaceResponseDto> findAllPaginate(EnlaceFilterDto filters, Pageable pageable) {

        Page<CnEnlacesGeneralesEntity> page = cnEnlacesGeneralesRepository.findAllPaginate(filters.getFilter(), pageable);
        PaginatedDto paginatedDto = new PaginatedDto<CnPlanCuentaGetListDto>();

        List<CnEnlaceResponseDto> dtoList = page.stream().map(cnEnlaceBuilder::builderResponse).toList();
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
