package com.calero.lili.core.modVentasZonas;

import com.calero.lili.core.modVentasZonas.builder.VtZonaBuilder;
import com.calero.lili.core.modVentasZonas.dto.VtZonaCreationRequestDto;
import com.calero.lili.core.modVentasZonas.dto.VtZonaGetListDto;
import com.calero.lili.core.modVentasZonas.dto.VtZonaGetOneDto;
import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VtZonasServiceImpl {

    private final VtZonasRepository vtZonasRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final VtZonaBuilder vtZonaBuilder;
    private final ResponseApiBuilder responseApiBuilder;


    public ResponseDto create(Long idData, Long idEmpresa, VtZonaCreationRequestDto request, String usuario) {

        Optional<AdEmpresaEntity> existing = adEmpresasRepository.findById(idData, idEmpresa);
        if (existing.isEmpty()) {
            throw new GeneralException(MessageFormat.format("Data: {0} / Empresa {1} no existe", idData, idEmpresa));
        }
        VtZonaEntity entidad = vtZonaBuilder.builderEntity(request, idData, idEmpresa);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        VtZonaEntity saved = vtZonasRepository.save(entidad);
        return responseApiBuilder.builderResponse(saved.getIdZona().toString());


    }

    public ResponseDto update(Long idData, Long idEmpresa, UUID id, VtZonaCreationRequestDto request, String usuario) {

        VtZonaEntity entidad = vtZonasRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));


        VtZonaEntity actualizado = vtZonaBuilder.builderUpdateEntity(request, entidad);
        actualizado.setModifiedBy(usuario);
        actualizado.setModifiedDate(LocalDateTime.now());
        vtZonasRepository.save(actualizado);
        return responseApiBuilder.builderResponse(actualizado.getIdZona().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {
        VtZonaEntity entidad = vtZonasRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        vtZonasRepository.save(entidad);
    }

    public VtZonaGetOneDto findById(Long idData, Long idEmpresa, UUID id) {

        VtZonaEntity entidad = vtZonasRepository.findById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("id {0} no existe", id)));

        return vtZonaBuilder.builderResponse(entidad);
    }

    public PaginatedDto<VtZonaGetListDto> findAllPaginate(Long idData, Long idEmpresa, FilterDto filters, Pageable pageable) {

        Page<VtZonaEntity> page = vtZonasRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);

        PaginatedDto paginatedDto = new PaginatedDto<VtZonaGetListDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(vtZonaBuilder::builderListResponse)
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
