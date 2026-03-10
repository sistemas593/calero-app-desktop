package com.calero.lili.core.modAdminEmpresasSucursales;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSucursales.builder.AdEmpresasSucursalesBuilder;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalCreationRequestDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalGetListDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalGetOneDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalListFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
public class AdEmpresasSucursalesServiceImpl {

    private final AdEmpresasSucursalesRepository adEmpresasSucursalesRepository;
    private final AdEmpresasSucursalesBuilder adEmpresasSucursalesBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final AuditorAware<String> auditorAware;

    public ResponseDto create(Long idData, Long idEmpresa, AdEmpresaSucursalCreationRequestDto request) {

        AdEmpresasSucursalesEntity createdDto = adEmpresasSucursalesRepository.save(adEmpresasSucursalesBuilder
                .builderCreateEntity(request, idData, idEmpresa));

        return responseApiBuilder.builderResponse(createdDto.getSucursal());
    }

    public ResponseDto update(Long idData, Long idEmpresa, UUID idSucursal, AdEmpresaSucursalCreationRequestDto request) {

        AdEmpresasSucursalesEntity entidad = adEmpresasSucursalesRepository.findById(idData, idEmpresa, idSucursal)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("id {0} no existe", idSucursal)));

        AdEmpresasSucursalesEntity update = adEmpresasSucursalesBuilder.builderUpdateEntity(request, entidad);

       /* update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());*/

        adEmpresasSucursalesRepository.save(update);

        return responseApiBuilder.builderResponse(entidad.getSucursal());

    }

    public void delete(Long idData, Long idEmpresa, UUID idSucursal) {

        AdEmpresasSucursalesEntity sucursal = adEmpresasSucursalesRepository.findById(idData, idEmpresa, idSucursal)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("id {0} no existe", idSucursal)));

       /* sucursal.setDelete(Boolean.TRUE);
        sucursal.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        sucursal.setDeletedDate(LocalDateTime.now());*/

        adEmpresasSucursalesRepository.save(sucursal);

    }

    public AdEmpresaSucursalGetOneDto findById(Long idData, Long idEmpresa, UUID idSucursal) {

        Optional<AdEmpresasSucursalesEntity> entidad = adEmpresasSucursalesRepository.findById(idData, idEmpresa, idSucursal);
        if (entidad == null) {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", idSucursal));
        }
        return adEmpresasSucursalesBuilder.builderDto(entidad.get());
    }


    public PaginatedDto<AdEmpresaSucursalGetListDto> findAllPaginate(Long idData, Long idEmpresa, AdEmpresaSucursalListFilterDto filters, Pageable pageable) {

        Page<AdEmpresasSucursalesEntity> page = adEmpresasSucursalesRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<AdEmpresaSucursalGetListDto>();

        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adEmpresasSucursalesBuilder::builderListDto)
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
