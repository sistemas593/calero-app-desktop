package com.calero.lili.api.modTercerosProvedoresParametros;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosRepository;
import com.calero.lili.api.modTercerosProvedoresParametros.builder.CpProveedoresParametroBuilder;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationRequestDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationResponseDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroListFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CpProveedoresParametrosServiceImpl {

    private final CpProveedoresParametrosRepository cpProveedoresParametrosRepository;
    private final CpProveedoresParametroBuilder cpProveedoresParametroBuilder;
    private final GeTercerosRepository geTercerosRepository;

    public CpProveedorParametroCreationResponseDto create(Long idData, Long idEmpresa, CpProveedorParametroCreationRequestDto request) {


        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));

        CpProveedoresParametrosEntity entity = cpProveedoresParametroBuilder.builderEntity(request, idData, idEmpresa);
        entity.setProveedor(tercero);

        return cpProveedoresParametroBuilder.builderResponse(cpProveedoresParametrosRepository
                .save(entity));
    }

    public CpProveedorParametroCreationResponseDto update(Long idData, Long idEmpresa, UUID id, CpProveedorParametroCreationRequestDto request) {


        Optional<CpProveedoresParametrosEntity> entidad = cpProveedoresParametrosRepository.findByIdEntity(idData, idEmpresa, id);
        if (entidad.isEmpty()) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }

        GeTerceroEntity tercero = geTercerosRepository.findByIdCliente(idData, request.getIdTercero())
                .orElseThrow(() -> new GeneralException("No existe tercero"));


        CpProveedoresParametrosEntity entity = cpProveedoresParametroBuilder
                .builderUpdateEntity(request, entidad.get());
        entity.setProveedor(tercero);

        return cpProveedoresParametroBuilder.builderResponse(cpProveedoresParametrosRepository
                .save(entity));
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {
        cpProveedoresParametrosRepository.deleteByIdDataAndEmpresaAndIdParametro(idData, idEmpresa, id);
    }

    public CpProveedorParametroCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {
        return cpProveedoresParametroBuilder.builderResponse(cpProveedoresParametrosRepository
                .findByIdEntity(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id))));
    }


    public PaginatedDto<CpProveedorParametroCreationResponseDto> findAllPaginate(Long idData, Long idEmpresa, CpProveedorParametroListFilterDto filters, Pageable pageable) {
        Page<CpProveedoresParametrosEntity> page = cpProveedoresParametrosRepository.findAllPaginate(idData, idEmpresa, pageable);
        PaginatedDto paginatedDto = new PaginatedDto<CpProveedorParametroCreationResponseDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(cpProveedoresParametroBuilder::builderResponse)
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
