package com.calero.lili.api.modTerceros;

import com.calero.lili.api.modTerceros.builder.GeTerceroBuilder;
import com.calero.lili.api.modTerceros.dto.GeTerceroFilterDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroGetListDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroGetOneDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroRequestDto;
import com.calero.lili.api.modTerceros.projections.GeTerceroProjection;
import com.calero.lili.api.utils.validaciones.ValidarCampoAscii;
import com.calero.lili.api.utils.validaciones.ValidarIdentificacion;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeTercerosServiceImpl {

    private final GeTercerosRepository vtClientesRepository;
    private final ValidarIdentificacion validarIdentificacion;
    private final GeTerceroBuilder clienteBuilder;
    private final GeTercerosTipoServiceImpl geTercerosTipoService;



    public GeTerceroGetListDto create(Long idEmpresa, Long idData, GeTerceroRequestDto request, String usuario) {

        ValidarCampoAscii.validarStrings(request);

        Optional<GeTerceroProjection> vtClientesEntityExist = vtClientesRepository.findExistByNumeroIdentificacion(idData, request.getNumeroIdentificacion());
        if (vtClientesEntityExist.isPresent()) {
            System.out.println(request.getNumeroIdentificacion());
            throw new GeneralException(MessageFormat.format("El tercero con número de identificación {0} ya existe", request.getNumeroIdentificacion()));
        }

        try {
            if (request.getTipoIdentificacion() != null) {
                if (request.getTipoIdentificacion().equals(TipoIdentificacion.C)) {
                    validarIdentificacion.validarCedula(request.getNumeroIdentificacion());
                } else if (request.getTipoIdentificacion().equals(TipoIdentificacion.R)) {
                    validarIdentificacion.validarRuc(request.getNumeroIdentificacion());
                }
            }

        } catch (Exception e) {
            throw new GeneralException(MessageFormat.format(e.getMessage(), ""));
        }

        validarTransportista(request);
        validarTrabajador(request);
        GeTerceroEntity newEntity = clienteBuilder.builderEntity(request, idData);
        newEntity.setCreatedBy(usuario);
        newEntity.setCreatedDate(LocalDateTime.now());
        GeTerceroEntity entity = vtClientesRepository.save(newEntity);
        geTercerosTipoService.save(request, entity, idData, idEmpresa);
        return clienteBuilder.builderListResponse(entity);
    }

    public GeTerceroGetListDto update(Long idEmpresa, Long idData, UUID id, GeTerceroRequestDto request, String usuario) {

        ValidarCampoAscii.validarStrings(request);

        GeTerceroEntity actualizar = vtClientesRepository.findByIdCliente(idData, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Cliente con id {0} no existe", id)));

        validarTransportista(request);
        validarTrabajador(request);

        GeTerceroEntity update = clienteBuilder.builderUpdateEntity(request, actualizar);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        GeTerceroEntity actualizado = vtClientesRepository.save(update);
        geTercerosTipoService.update(request, actualizado, idEmpresa);
        return clienteBuilder.builderListResponse(actualizado);
    }

    public void delete(Long idData, UUID id, String usuario) {
        GeTerceroEntity entidad = vtClientesRepository.findByIdCliente(idData, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Tercero con id {0} no existe", id)));


        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        vtClientesRepository.save(entidad);
    }

    public GeTerceroGetOneDto findById(Long idData, UUID id, Long idEmpresa) {
        GeTerceroEntity entidad = vtClientesRepository.findByIdCliente(idData, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Cliente con id {0} no existe", id)));
        GeTerceroGetOneDto response = clienteBuilder.builderResponse(entidad);
        geTercerosTipoService.getResponseTercerosTipos(entidad, response, idEmpresa);
        return response;
    }

    public PaginatedDto<GeTerceroGetListDto> findAllPaginate(Long idData, GeTerceroFilterDto filters, Pageable pageable) {
        Page<GeTerceroEntity> page = vtClientesRepository.findAllPaginate(idData, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", filters.getTipoTercero(), pageable);

        PaginatedDto paginatedDto = new PaginatedDto<GeTerceroGetListDto>();

        List<GeTerceroGetListDto> dtoList = page.stream().map(clienteBuilder::builderListResponse).toList();

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


    private void validarTransportista(GeTerceroRequestDto request) {

        if (request.getTransportista().getEsTransportista()) {
            if (Objects.isNull(request.getTransportista().getPlaca()) || request.getTransportista().getPlaca().isEmpty()) {
                throw new GeneralException("Es requerida la placa del transportista");
            }
        }


        if (!request.getTransportista().getEsTransportista()) {
            if (Objects.nonNull(request.getTransportista().getPlaca())) {
                throw new GeneralException("No es requerida la placa del transportista");
            }
        }

    }

    private void validarTrabajador(GeTerceroRequestDto request) {
        if (request.getTrabajador().getEsTrabajador()) {
            if (Objects.isNull(request.getTrabajador().getInfoTrabajador())) {
                throw new GeneralException("La información del trabajador es requerida");
            } else {
                if (Objects.isNull(request.getTrabajador().getInfoTrabajador().getCodigoPais())
                        || request.getTrabajador().getInfoTrabajador().getCodigoPais().isEmpty()) {
                    throw new GeneralException("El codigo del pais es requerido");
                }
            }
        }
    }

}


