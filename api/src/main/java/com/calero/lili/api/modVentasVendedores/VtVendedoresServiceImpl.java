package com.calero.lili.api.modVentasVendedores;

import com.calero.lili.api.modVentasVendedores.builder.VtVendedorBuilder;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationRequestDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationResponseDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorReportDto;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
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
public class VtVendedoresServiceImpl {

    private final VtVendedoresRepository vtVendedoresRepository;
    private final VtVendedorBuilder vtVendedorBuilder;

    public VtVendedorCreationResponseDto create(Long idData, Long idEmpresa, VtVendedorCreationRequestDto request, String usuario) {

        VtVendedorEntity entity = vtVendedorBuilder.builderEntity(request, idData, idEmpresa);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        return vtVendedorBuilder.builderResponse(vtVendedoresRepository.save(entity));
    }

    public VtVendedorCreationResponseDto update(Long idData, Long idEmpresa, UUID id,
                                                VtVendedorCreationRequestDto request, String usuario) {

        VtVendedorEntity entidad = vtVendedoresRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        VtVendedorEntity update = vtVendedorBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return vtVendedorBuilder.builderResponse(vtVendedoresRepository.save(update));
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        VtVendedorEntity entidad = vtVendedoresRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        vtVendedoresRepository.save(entidad);
    }

    public VtVendedorCreationResponseDto findById(Long idData, Long idEmpresa, UUID id) {

        VtVendedorEntity entidad = vtVendedoresRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("id {0} no existe", id)));
        return vtVendedorBuilder.builderResponse(entidad);
    }

    public PaginatedDto<VtVendedorReportDto> findAllPaginate(Long idData, Long idEmpresa, FilterDto filters, Pageable pageable) {

        Page<VtVendedorEntity> page = vtVendedoresRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<VtVendedorReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(vtVendedorBuilder::builderListResponse)
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
