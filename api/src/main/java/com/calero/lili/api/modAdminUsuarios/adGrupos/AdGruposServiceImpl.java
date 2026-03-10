package com.calero.lili.api.modAdminUsuarios.adGrupos;

import com.calero.lili.api.modAdminUsuarios.adGrupos.builder.AdGruposBuilder;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposRequestDto;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposResponseDto;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.GruposFilter;
import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosRepository;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioReportDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdGruposServiceImpl {

    private final AdGruposRepository adGruposRepository;
    private final AdGruposBuilder adGruposBuilder;
    private final AdPermisosRepository adPermisosRepository;
    private final AuditorAware<String> auditorAware;


    public AdGruposResponseDto create(AdGruposRequestDto request) {
        return adGruposBuilder.builderResponse(adGruposRepository
                .save(validarCreatePermisos(request)));
    }


    public AdGruposResponseDto update(Long idGrupoPermiso, AdGruposRequestDto request) {
        AdGruposEntity adGrupoPermiso = adGruposRepository.getFindId(idGrupoPermiso).orElseThrow(() ->
                new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso)));

        return adGruposBuilder.builderResponse(adGruposRepository
                .save(validarUpdatePermisos(request, adGrupoPermiso)));
    }

    public AdGruposResponseDto findById(Long idGrupoPermiso) {
        return adGruposBuilder.builderResponse(adGruposRepository.getFindId(idGrupoPermiso)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso))));
    }

    public void delete(Long idGrupoPermiso) {

        AdGruposEntity adGrupoPermiso = adGruposRepository.getFindId(idGrupoPermiso)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso)));


        adGrupoPermiso.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        adGrupoPermiso.setDeletedDate(LocalDateTime.now());
        adGrupoPermiso.setDelete(Boolean.TRUE);

        adGruposRepository.save(adGrupoPermiso);
    }

    public PaginatedDto<AdGruposResponseDto> findAll(GruposFilter filtro, Pageable pageable) {

        Page<AdGruposEntity> page = adGruposRepository.findAllPaginate(!filtro.getFilter().isEmpty() ?
                filtro.getFilter().toLowerCase() : filtro.getFilter(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdUsuarioReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adGruposBuilder::builderResponse)
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

    private AdGruposEntity validarCreatePermisos(AdGruposRequestDto request) {
        validarGrupoPermiso(request);
        List<AdPermisosEntity> listPermisos = new ArrayList<>();
        AdGruposEntity entidad = adGruposBuilder.builderEntity(request);
        if (!request.getPermisos().isEmpty()) {

            for (AdGruposRequestDto.Permisos item : request.getPermisos()) {

                AdPermisosEntity permisos = adPermisosRepository.getFindId(item.getIdPermiso())
                        .orElseThrow(() -> new GeneralException(MessageFormat
                                .format("El permiso con id {0} no existe", item.getIdPermiso())));
                listPermisos.add(permisos);
            }
            entidad.setPermisos(listPermisos);
        }
        return entidad;
    }


    private AdGruposEntity validarUpdatePermisos(AdGruposRequestDto request, AdGruposEntity item) {

        List<AdPermisosEntity> listPermisos = adPermisosRepository
                .findAllById(request.getPermisos()
                        .stream()
                        .map(AdGruposRequestDto.Permisos::getIdPermiso)
                        .toList());

        item.getPermisos().clear();
        AdGruposEntity entidad = adGruposBuilder.builderUpdate(request, item);
        entidad.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setModifiedDate(LocalDateTime.now());
        entidad.setPermisos(listPermisos);
        return entidad;
    }


    private void validarGrupoPermiso(AdGruposRequestDto request) {
        Optional<AdGruposEntity> exists = adGruposRepository.getFindNombre(request.getNombre());
        if (exists.isPresent()) {
            throw new GeneralException(MessageFormat
                    .format("El grupo permiso con nombre {0} ya existe", request.getNombre()));
        }
    }

}
