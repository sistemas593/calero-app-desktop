package com.calero.lili.api.modAdminUsuarios.adRol;

import com.calero.lili.api.modAdminUsuarios.adRol.builder.AdRolBuilder;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoRequest;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoResponse;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.RolFilterDto;
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
public class AdRolServiceImpl {

    private final AdUsuarioRolRepository adUsuarioRolRepository;
    private final AdRolBuilder adRolBuilder;


    public AdRolDtoResponse create(Long idData, AdRolDtoRequest request, String usuario) {
        return adRolBuilder.builderResponse(adUsuarioRolRepository
                .save(validarCreatePermisos(idData, request, usuario)));
    }

    public AdRolDtoResponse update(Long idData, Long idRol, AdRolDtoRequest request, String usuario) {

        AdRolEntity adRolEntity = adUsuarioRolRepository.getFindId(idData, idRol).orElseThrow(() -> new GeneralException(MessageFormat
                .format("El rol con id {0} no existe", idRol)));

        return adRolBuilder.builderResponse(adUsuarioRolRepository
                .save(validarUpdatePermisos(request, adRolEntity, usuario)));
    }

    public AdRolDtoResponse findById(Long idData, Long idRol) {
        return adRolBuilder.builderResponse(adUsuarioRolRepository.getFindId(idData, idRol)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("El rol con id {0} no existe", idRol))));
    }

    public void delete(Long idData, Long idRol, String usuario) {

        AdRolEntity adRolEntity = adUsuarioRolRepository.getFindId(idData, idRol).orElseThrow(() -> new GeneralException(MessageFormat
                .format("El rol con id {0} no existe", idRol)));

        adRolEntity.setDeletedBy(usuario);
        adRolEntity.setDeletedDate(LocalDateTime.now());
        adRolEntity.setDelete(Boolean.TRUE);

        adUsuarioRolRepository.save(adRolEntity);
    }

    public PaginatedDto<AdRolDtoResponse> findAll(Long idData, RolFilterDto filtro, Pageable pageable) {

        Page<AdRolEntity> page = adUsuarioRolRepository.findAllPaginate(idData, !filtro.getFilter().isEmpty()
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

    private AdRolEntity validarCreatePermisos(Long idData, AdRolDtoRequest request, String usuario) {
        validarRol(request);
        AdRolEntity entidad = adRolBuilder.builderEntity(idData, request);
        entidad.setCreatedBy(usuario);
        entidad.setCreatedDate(LocalDateTime.now());
        return entidad;
    }

    private AdRolEntity validarUpdatePermisos(AdRolDtoRequest request, AdRolEntity item, String usuario) {
        AdRolEntity entidad = adRolBuilder.builderUpdate(request, item);
        entidad.setModifiedBy(usuario);
        entidad.setModifiedDate(LocalDateTime.now());
        return entidad;
    }


}
