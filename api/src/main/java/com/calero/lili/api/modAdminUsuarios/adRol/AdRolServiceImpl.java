package com.calero.lili.api.modAdminUsuarios.adRol;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposEntity;
import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposRepository;
import com.calero.lili.api.modAdminUsuarios.adRol.builder.AdRolBuilder;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoRequest;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoResponse;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.RolFilterDto;
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
public class AdRolServiceImpl {

    private final AdUsuarioRolRepository adUsuarioRolRepository;
    private final AdRolBuilder adRolBuilder;
    private final AdGruposRepository adGruposRepository;
    private final AuditorAware<String> auditorAware;


    public AdRolDtoResponse create(AdRolDtoRequest request) {
        return adRolBuilder.builderResponse(adUsuarioRolRepository
                .save(validarCreatePermisos(request)));
    }

    public AdRolDtoResponse update(Long idRol, AdRolDtoRequest request) {

        AdRolEntity adRolEntity = adUsuarioRolRepository.getFindId(idRol).orElseThrow(() -> new GeneralException(MessageFormat
                .format("El rol con id {0} no existe", idRol)));

        return adRolBuilder.builderResponse(adUsuarioRolRepository
                .save(validarUpdatePermisos(request, adRolEntity)));
    }

    public AdRolDtoResponse findById(Long idRol) {
        return adRolBuilder.builderResponse(adUsuarioRolRepository.getFindId(idRol)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El rol con id {0} no existe", idRol))));
    }

    public void delete(Long idRol) {

        AdRolEntity adRolEntity = adUsuarioRolRepository.getFindId(idRol).orElseThrow(() -> new GeneralException(MessageFormat
                .format("El rol con id {0} no existe", idRol)));

        adRolEntity.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        adRolEntity.setDeletedDate(LocalDateTime.now());
        adRolEntity.setDelete(Boolean.TRUE);

        adUsuarioRolRepository.save(adRolEntity);
    }

    public PaginatedDto<AdRolDtoResponse> findAll(RolFilterDto filtro, Pageable pageable) {

        Page<AdRolEntity> page = adUsuarioRolRepository.findAllPaginate(!filtro.getFilter().isEmpty()
                ? filtro.getFilter().toLowerCase()
                : filtro.getFilter(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<AdUsuarioReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(adRolBuilder::builderResponse)
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


    private void validarRol(AdRolDtoRequest request) {
        Optional<AdRolEntity> exists = adUsuarioRolRepository.findForName(request.getNombre());
        if (exists.isPresent()) {
            throw new GeneralException(MessageFormat
                    .format("El rol con nombre {0} ya existe", request.getNombre()));
        }
    }

    private AdRolEntity validarCreatePermisos(AdRolDtoRequest request) {
        validarRol(request);
        List<AdGruposEntity> listPermisos = new ArrayList<>();
        AdRolEntity entidad = adRolBuilder.builderEntity(request);
        if (!request.getGrupos().isEmpty()) {

            for (AdRolDtoRequest.Grupo item : request.getGrupos()) {

                AdGruposEntity permisos = adGruposRepository.getFindId(item.getIdGrupo())
                        .orElseThrow(() -> new GeneralException(MessageFormat
                                .format("El grupo de permiso con id {0} no existe", item.getIdGrupo())));
                listPermisos.add(permisos);
            }
            entidad.setGrupos(listPermisos);
        }
        return entidad;
    }

    private AdRolEntity validarUpdatePermisos(AdRolDtoRequest request, AdRolEntity item) {

        List<AdGruposEntity> listPermisos = adGruposRepository
                .findAllById(request.getGrupos()
                        .stream()
                        .map(AdRolDtoRequest.Grupo::getIdGrupo)
                        .toList());

        item.getGrupos().clear();
        AdRolEntity entidad = adRolBuilder.builderUpdate(request, item);
        entidad.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setModifiedDate(LocalDateTime.now());
        entidad.setGrupos(listPermisos);
        return entidad;
    }


}
