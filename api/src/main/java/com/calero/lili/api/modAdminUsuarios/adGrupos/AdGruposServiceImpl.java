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


    public AdGruposResponseDto create(Long idData, Long idEmpresa, AdGruposRequestDto request, String usuario) {
        return adGruposBuilder.builderResponse(adGruposRepository
                .save(validarCreatePermisos(idData, idEmpresa, request, usuario)));
    }


    public AdGruposResponseDto update(Long idData, Long idEmpresa, Long idGrupoPermiso, AdGruposRequestDto request, String usuario) {
        AdGruposEntity adGrupoPermiso = adGruposRepository.getFindId(idData, idEmpresa, idGrupoPermiso).orElseThrow(() ->
                new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso)));

        return adGruposBuilder.builderResponse(adGruposRepository
                .save(validarUpdatePermisos(request, adGrupoPermiso, usuario)));
    }

    public AdGruposResponseDto findById(Long idData, Long idEmpresa, Long idGrupoPermiso) {
        return adGruposBuilder.builderResponse(adGruposRepository.getFindId(idData, idEmpresa, idGrupoPermiso)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso))));
    }

    public void delete(Long idData, Long idEmpresa, Long idGrupoPermiso, String usuario) {

        AdGruposEntity adGrupoPermiso = adGruposRepository.getFindId(idData, idEmpresa, idGrupoPermiso)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El grupo permiso con id {0} no existe", idGrupoPermiso)));


        adGrupoPermiso.setDeletedBy(usuario);
        adGrupoPermiso.setDeletedDate(LocalDateTime.now());
        adGrupoPermiso.setDelete(Boolean.TRUE);

        adGruposRepository.save(adGrupoPermiso);
    }

    public PaginatedDto<AdGruposResponseDto> findAll(Long idData, Long idEmpresa, GruposFilter filtro, Pageable pageable) {

        Page<AdGruposEntity> page = adGruposRepository.findAllPaginate(idData, idEmpresa, !filtro.getFilter().isEmpty() ?
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

    private AdGruposEntity validarCreatePermisos(Long idData, Long idEmpresa, AdGruposRequestDto request, String usuario) {
        validarGrupoPermiso(request);
        List<AdPermisosEntity> listPermisos = new ArrayList<>();
        AdGruposEntity entidad = adGruposBuilder.builderEntity(idData, idEmpresa, request);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
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


    private AdGruposEntity validarUpdatePermisos(AdGruposRequestDto request, AdGruposEntity item, String usuario) {

        List<AdPermisosEntity> listPermisos = adPermisosRepository
                .findAllById(request.getPermisos()
                        .stream()
                        .map(AdGruposRequestDto.Permisos::getIdPermiso)
                        .toList());

        item.getPermisos().clear();
        AdGruposEntity entidad = adGruposBuilder.builderUpdate(request, item);
        entidad.setModifiedBy(usuario);
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
