package com.calero.lili.core.modAdDatasConfiguraciones;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdDatasConfiguraciones.builder.VtClienteConfiguracionBuilder;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StEmpresasListCreationResponseDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesCreationResponseDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesGetListDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesGetOneDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesListCreationRequestDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesListFilterDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.VtClientesConfiguracionesRequestDto;
import com.calero.lili.core.modAdModulos.AdModuloRepository;
import com.calero.lili.core.modAdModulos.AdModulosEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VtClientesConfiguracionesServiceImpl {

    private final VtClientesConfiguracionesRepository clientesConfiguracionesRepository;
    private final VtClienteConfiguracionBuilder vtClienteConfiguracionBuilder;
    private final ResponseApiBuilder responseApiBuilder;
    private final AdModuloRepository adModuloRepository;


    public ResponseDto create(VtClientesConfiguracionesRequestDto request, String usuario) {


        Optional<VtClientesConfiguracionesEntity> existing = clientesConfiguracionesRepository
                .findByClave(request.getClave());
        if (existing.isPresent()) {
            throw new GeneralException(MessageFormat.format("Clave {0} ya existe", request.getClave()));
        }

        Optional<VtClientesConfiguracionesEntity> existing2 = clientesConfiguracionesRepository.findByRuc(request.getRuc());
        if (existing2.isPresent()) {
            throw new GeneralException(MessageFormat.format("RUC {0} ya existe", request.getRuc()));
        }
        VtClientesConfiguracionesEntity configuracion = vtClienteConfiguracionBuilder.builderEntity(request);
        validarModulos(configuracion, request);
        configuracion.setCreatedBy(usuario);
        configuracion.setCreatedDate(LocalDateTime.now());
        clientesConfiguracionesRepository.save(configuracion);
        return responseApiBuilder.builderResponse(configuracion.getIdConfiguracion().toString());
    }

    public ResponseDto update(UUID id, VtClientesConfiguracionesRequestDto request, String usuario) {

        VtClientesConfiguracionesEntity entidad = clientesConfiguracionesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("id number {0} no exists", id)));

        VtClientesConfiguracionesEntity update = vtClienteConfiguracionBuilder
                .builderUpdateEntity(request, entidad);

        validarModulos(update, request);
        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        VtClientesConfiguracionesEntity actualizar = clientesConfiguracionesRepository.save(update);

        return responseApiBuilder.builderResponse(actualizar.getIdConfiguracion().toString());
    }

    public void delete(UUID id, String usuario) {

        VtClientesConfiguracionesEntity stEmpresasEntity = clientesConfiguracionesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        stEmpresasEntity.setDelete(Boolean.TRUE);
        stEmpresasEntity.setDeletedBy(usuario);
        stEmpresasEntity.setDeletedDate(LocalDateTime.now());

        clientesConfiguracionesRepository.save(stEmpresasEntity);
    }

    public VtClientesConfiguracionesGetOneDto findById(UUID id) {

        VtClientesConfiguracionesEntity entidad = clientesConfiguracionesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));
        return vtClienteConfiguracionBuilder.builderResponse(entidad);
    }

    public PaginatedDto<VtClientesConfiguracionesGetListDto> findAllPaginate(VtClientesConfiguracionesListFilterDto filters,
                                                                             Pageable pageable) {

        Page<VtClientesConfiguracionesEntity> page = null;

        if (Objects.nonNull(filters.getFechaVencimientoDesde()) && Objects.nonNull(filters.getFechaVencimientoHasta())) {

            page = clientesConfiguracionesRepository.findAllPaginateVencimiento(filters.getFilter(),
                    (filters.getFilter() != null) ? filters.getFilter() : "", filters.getFechaVencimientoDesde(),
                    filters.getFechaVencimientoHasta(), pageable);
        }
        if (Objects.nonNull(filters.getFechaBloqueoDesde()) && Objects.nonNull(filters.getFechaBloqueoHasta())) {
            page = clientesConfiguracionesRepository.findAllPaginateBloqueo(filters.getFilter(),
                    (filters.getFilter() != null) ? filters.getFilter() : "", filters.getFechaBloqueoDesde(),
                    filters.getFechaBloqueoHasta(), pageable);
        }

        if (page == null) {

            page = clientesConfiguracionesRepository.findAllPaginate(filters.getFilter(),
                    (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        }

        PaginatedDto paginatedDto = new PaginatedDto<VtClientesConfiguracionesGetListDto>();

        List<VtClientesConfiguracionesGetListDto> dtoList = page.stream()
                .map(vtClienteConfiguracionBuilder::builderListResponse)
                .toList();

        paginatedDto.setContent(dtoList);

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


    public StEmpresasListCreationResponseDto createUpdateList(VtClientesConfiguracionesListCreationRequestDto request, String usuario) {


        StEmpresasListCreationResponseDto errores = new StEmpresasListCreationResponseDto();
        List<VtClientesConfiguracionesCreationResponseDto> listaRespuestas = new ArrayList<>();

        request.getListaEmpresas().stream().forEach((requestDto) -> {

            String error = "";
            if (requestDto.getClave() == null || requestDto.getClave().isEmpty()) {
                error = "vacio";
                VtClientesConfiguracionesCreationResponseDto res = new VtClientesConfiguracionesCreationResponseDto();
                res.setClave("");
                res.setError("Clave no puede estar vacio");
                listaRespuestas.add(res);
            }
            if (requestDto.getFechaVencimiento() == null) {
                error = "vacio";
                VtClientesConfiguracionesCreationResponseDto res = new VtClientesConfiguracionesCreationResponseDto();
                res.setClave(requestDto.getClave());
                res.setError("Fecha vencimiento no puede estar vacio");
                listaRespuestas.add(res);
            } else {
                try {
                    String fechaVencimiento;
                    DateUtils.toLocalDate(requestDto.getFechaVencimiento());
                } catch (Exception e) {
                    error = "La fecha es incorrecta";
                    VtClientesConfiguracionesCreationResponseDto res = new VtClientesConfiguracionesCreationResponseDto();
                    res.setClave(requestDto.getClave());
                    res.setError(error);
                    listaRespuestas.add(res);
                }
            }
            if (error.isEmpty()) {
                String clave = requestDto.getClave();
                Optional<VtClientesConfiguracionesEntity> existing = clientesConfiguracionesRepository.findByClave(clave);
                if (existing.isEmpty()) {
                    VtClientesConfiguracionesEntity saved = vtClienteConfiguracionBuilder.builderListEntity(requestDto);
                    saved.setCreatedBy(usuario);
                    saved.setCreatedDate(LocalDateTime.now());
                    validarModulos(saved, requestDto);
                    clientesConfiguracionesRepository.save(saved);
                } else {
                    //ACTUALIZAR
                    VtClientesConfiguracionesEntity entidad = existing.orElseThrow();
                    VtClientesConfiguracionesEntity updated = vtClienteConfiguracionBuilder
                            .builderListUpdate(requestDto, entidad);
                    updated.setModifiedBy(usuario);
                    updated.setModifiedDate(LocalDateTime.now());
                    validarModulos(updated, requestDto);
                    clientesConfiguracionesRepository.save(updated);
                }
            }

        });

        errores.setErrores(listaRespuestas);
        return errores;

    }

    private void validarModulos(VtClientesConfiguracionesEntity entidad, VtClientesConfiguracionesRequestDto request) {

        if (Objects.isNull(request.getModuloList()) || request.getModuloList().isEmpty()) {
            entidad.setModulosList(null);
        } else {

            List<Long> listaIdModulos = request.getModuloList()
                    .stream().map(VtClientesConfiguracionesRequestDto.ModuloDto::getIdModulo)
                    .toList();

            List<AdModulosEntity> lista = adModuloRepository.findAllByIds(listaIdModulos);

            Set<Long> idsEncontrados = lista.stream()
                    .map(AdModulosEntity::getIdModulo)
                    .collect(Collectors.toSet());

            List<Long> idsNoEncontrados = request.getModuloList().stream()
                    .map(VtClientesConfiguracionesRequestDto.ModuloDto::getIdModulo)
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();

            if (!idsNoEncontrados.isEmpty()) {
                throw new GeneralException(
                        "No existen  módulos con los siguientes ids: " + idsNoEncontrados
                );
            }
            entidad.setModulosList(lista);
        }
    }
}
