package com.calero.lili.api.modAdminUsuarios;

import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.AdRolEntity;
import com.calero.lili.api.modAdminUsuarios.adRol.AdUsuarioRolRepository;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioCreationResponseDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioListFilterDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioPermisosDtoResponse;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioReportDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioRequestDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminDatas.AdDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdUsuarioServiceImpl {

    @Autowired
    private AdUsuarioRepository adUsuarioRepository;

    @Autowired
    private AdUsuarioRolRepository roleRepository;

    // ACTIVAR CONJUNTAMENTE CON create
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdDataRepository adDataRepository;


    @Transactional
    // ESTA INCORRECTO ESTOY RECIBIENTO DIRECTAMENTE LA ENTIDAD, CAMBIAR Y RECIBIR DTO
    public AdUsuarioCreationResponseDto create(AdUsuarioRequestDto request, String usuario) {

        AdUsuarioEntity adUsuarioExist = adUsuarioRepository.findByUsername(request.getUsername());
        if (adUsuarioExist != null) {
            throw new GeneralException(MessageFormat.format("El username {0} ya existe", request.getUsername()));
        }
        AdUsuarioEntity mailExist = adUsuarioRepository.findByEmail(request.getEmail());
        if (mailExist != null) {
            throw new GeneralException(MessageFormat.format("El email {0} ya existe", request.getEmail()));
        }

        adDataRepository.findByIdData(request.getIdData())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("IdData {0} no existe", request.getIdData())));

        AdUsuarioEntity adUsuario = new AdUsuarioEntity();

        adUsuario = toEntity(request, adUsuario);
        adUsuario.setUsername(request.getUsername());
        adUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        adUsuario.setCreatedBy(usuario);
        adUsuario.setCreatedDate(LocalDateTime.now());

//        List<AdRolEntity> rolesNuevosEntity = request
//                .getRoles()
//                .stream()
//                .map(detail -> toRolEntity(detail))
//                .collect(Collectors.toList());
//        if (rolesNuevosEntity.isEmpty()){
//            throw new GeneralException(MessageFormat.format("No existen roles para el usuario", ""));
//        }

//        List<AdRolEntity> rolesEntityActual = adUsuario.getRoles();
//        if (rolesEntityActual!=null){
//            rolesEntityActual.clear();
//        }
//
//        for(AdRolEntity rolesNuevos : rolesNuevosEntity){
//            rolesEntityActual.add(rolesNuevos);
//        }

//bien
//        List<AdRolEntity> rolesEntity = new ArrayList<>();
//
//        List<AdUsuarioRequestDto.Roles> rolesDto = request.getRoles();
//        for(AdUsuarioRequestDto.Roles rol : rolesDto){
//            AdRolEntity rolEntity = roleRepository.findById(rol.getIdRol())
//                    .orElseThrow(()-> new GeneralException(MessageFormat.format("IdRol {0} no existe", rol.getIdRol())));
//            rolEntity.setId(rol.getIdRol());
//            rolEntity.setName(rol.getRol());
//            rolesEntity.add(rolEntity);
//        }
//        adUsuario.setRoles(rolesEntity);

        adUsuarioRepository.save(adUsuario);

        AdUsuarioCreationResponseDto responseDto = new AdUsuarioCreationResponseDto();
        responseDto.setIdUsuario(adUsuario.getIdUsuario());
        responseDto.setUsername(request.getUsername());
        return responseDto;
    }

//    public AdRolEntity toRolEntity(AdUsuarioRequestDto.Roles roles){
//        AdRolEntity rol = new AdRolEntity();
//        rol.setId(roles.getIdRol());
//        rol.setName(rol.getName());
//        return rol;
//    }

    public AdUsuarioCreationResponseDto update(Long id, AdUsuarioRequestDto request, String usuario) {
        AdUsuarioEntity entidad = adUsuarioRepository.findByIdUsuario(id);
        if (entidad != null) {
            AdUsuarioEntity entity = toEntity(request, entidad);
            entity.setModifiedBy(usuario);
            entity.setModifiedDate(LocalDateTime.now());
            try {
                entidad = adUsuarioRepository.save(entity);

            } catch (Exception e) {
                throw new GeneralException(MessageFormat.format("Error al guardar", id));
            }


        } else {
            throw new GeneralException(MessageFormat.format("Id {0} no existe", id));
        }
        AdUsuarioCreationResponseDto responseDto = new AdUsuarioCreationResponseDto();
        responseDto.setIdUsuario(entidad.getIdUsuario());
        responseDto.setUsername(entidad.getUsername());
        return responseDto;
    }

    public AdUsuarioReportDto findByIdUsuario(Long idUsuario) {
        AdUsuarioEntity entidad = adUsuarioRepository.findById(idUsuario).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idUsuario)));
        //        if (!entidad.getIdData().equals(idData)){
//            throw new GeneralException(MessageFormat.format("Id {1} no exists", idData));
//        }
        return toDto(entidad);
    }

    public PaginatedDto<AdUsuarioReportDto> findAllPaginate(AdUsuarioListFilterDto filters, Pageable pageable) {
        Page<AdUsuarioEntity> page = adUsuarioRepository.findAllPaginate(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<AdUsuarioReportDto>();
        paginatedDto.setContent(page.getContent()
                .stream()
                .map(this::toDto)
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

    private AdUsuarioEntity toEntity(AdUsuarioRequestDto request, AdUsuarioEntity entidad) {
        entidad.setIdArea(request.getIdArea());
        entidad.setIdData(request.getIdData());

        entidad.setEmail(request.getEmail());
        entidad.setNivel(request.getNivel());
        //entidad.setInsertionDate(LocalDateTime.now());

        List<AdRolEntity> rolesEntity = new ArrayList<>();

        List<AdUsuarioRequestDto.Roles> rolesDto = request.getRoles();
        if (rolesDto == null) {
            //throw new GeneralException(MessageFormat.format("No existen roles para el usuario", ""));
            AdRolEntity rolEntity = roleRepository.findForName("ROLE_USER")
                    .orElseThrow(() -> new GeneralException(MessageFormat.format("ROLE_USER no existe", "")));
            //rolEntity.setId(Long.valueOf(1));
            rolesEntity.add(rolEntity);
        } else {
            for (AdUsuarioRequestDto.Roles rol : rolesDto) {
                AdRolEntity rolEntity = roleRepository.findById(rol.getIdRol())
                        .orElseThrow(() -> new GeneralException(MessageFormat.format("IdRol {0} no existe", rol.getIdRol())));
//            rolesEntity.add(rolEntity);
//                AdRolEntity rolEntity = new AdRolEntity();
//                rolEntity.setId(rol.getIdRol());
                rolesEntity.add(rolEntity);
            }
        }

        entidad.setRoles(rolesEntity);

        return entidad;
    }

    private AdUsuarioReportDto toDto(AdUsuarioEntity entity) {
        AdUsuarioReportDto dto = new AdUsuarioReportDto();
        dto.setIdArea(entity.getIdArea());
        dto.setIdData(entity.getIdData());
        dto.setIdUsuario(entity.getIdUsuario());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setNivel(entity.getNivel());

        List<AdUsuarioReportDto.Roles> rolesDto = new ArrayList<>();

        List<AdRolEntity> rolesEntity = entity.getRoles();

        for (AdRolEntity rol : rolesEntity) {
            AdUsuarioReportDto.Roles rolDto = new AdUsuarioReportDto.Roles();
            rolDto.setIdRol(rol.getIdRol());
            rolDto.setRol(rol.getNombre());
            rolesDto.add(rolDto);
        }

        rolesDto.sort(Comparator.comparing(AdUsuarioReportDto.Roles::getIdRol));
        dto.setRoles(rolesDto);
        return dto;
    }


    public AdUsuarioPermisosDtoResponse getRolPermisosUsuario(Long idUsuario) {
        AdUsuarioEntity entidad = adUsuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", idUsuario)));

        List<String> permisos = entidad.getRoles().stream()
                .flatMap(rol -> rol.getGrupos().stream())
                .flatMap(grupo -> grupo.getPermisos().stream())
                .map(AdPermisosEntity::getPermiso)
                .distinct()
                .toList();

        return AdUsuarioPermisosDtoResponse.builder()
                .roles(entidad.getRoles().stream().map(this::getRolUsuario).toList())
                .permisos(permisos)
                .build();


    }

    private AdUsuarioPermisosDtoResponse.Roles getRolUsuario(AdRolEntity rol) {
        return AdUsuarioPermisosDtoResponse.Roles.builder()
                .rol(rol.getNombre())
                .build();
    }

}
