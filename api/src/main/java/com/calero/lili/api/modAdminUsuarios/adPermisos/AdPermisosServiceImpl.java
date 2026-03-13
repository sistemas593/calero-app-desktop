package com.calero.lili.api.modAdminUsuarios.adPermisos;

import com.calero.lili.api.modAdminUsuarios.adPermisos.builder.AdPermisosBuilder;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosRequestDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosResponseDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.PermisoFilterDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioReportDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdPermisosServiceImpl {


    private final AdPermisosRepository adPermisosRepository;
    private final AdPermisosBuilder adPermisosBuilder;


    public AdPermisosResponseDto create(AdPermisosRequestDto model, String usuario) {

        validarPermisoIdentificador(model);
        AdPermisosEntity permiso = adPermisosBuilder.builderEntity(model);
        permiso.setCreatedBy(usuario);
        permiso.setCreatedDate(LocalDateTime.now());
        return adPermisosBuilder.builderResponse(adPermisosRepository.save(permiso));
    }

    public AdPermisosResponseDto update(Long idPermiso, AdPermisosRequestDto model, String usuario) {

        AdPermisosEntity exists = adPermisosRepository.getFindId(idPermiso).orElseThrow(() -> new GeneralException
                (MessageFormat.format("El  permiso con id {0} no existe", idPermiso)));

        AdPermisosEntity update = adPermisosBuilder.builderUpdate(model, exists);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        return adPermisosBuilder.builderResponse(adPermisosRepository.save(update));
    }

    public AdPermisosResponseDto findById(Long idPermiso) {
        return adPermisosBuilder.builderResponse(adPermisosRepository.getFindId(idPermiso)
                .orElseThrow(() -> new GeneralException
                        (MessageFormat.format("El  permiso con id {0} no existe", idPermiso))));
    }

    public void delete(Long idPermiso, String usuario) {

        AdPermisosEntity exists = adPermisosRepository.getFindId(idPermiso).orElseThrow(() -> new GeneralException
                (MessageFormat.format("El  permiso con id {0} no existe", idPermiso)));

        exists.setDeletedBy(usuario);
        exists.setDeletedDate(LocalDateTime.now());
        exists.setDelete(Boolean.TRUE);

        adPermisosRepository.save(exists);
    }

    public PaginatedDto<AdPermisosResponseDto> findAll(PermisoFilterDto filtro, Pageable pageable) {

        Page<AdPermisosEntity> page = adPermisosRepository.findAllPaginate(!filtro.getFilter().isEmpty()
                ? filtro.getFilter().toLowerCase()
                : filtro.getFilter(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdUsuarioReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adPermisosBuilder::builderResponse)
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


    private void validarPermisoIdentificador(AdPermisosRequestDto model) {

        Optional<AdPermisosEntity> exists = adPermisosRepository.getFindPermiso(model.getPermiso());
        if (exists.isPresent()) {
            throw new GeneralException(MessageFormat
                    .format("El  permiso con nombre {0} ya existe", model.getPermiso()));
        }
    }

}
