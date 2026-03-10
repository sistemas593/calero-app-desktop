package com.calero.lili.api.modContabilidad.modCentroCostos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modContabilidad.modCentroCostos.builder.CentroCostosBuilder;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoRequest;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoResponse;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetListDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@AllArgsConstructor
public class CnCentroCostosServiceImpl {


    private final CnCentroCostosRepository cnCentroCostosRepository;
    private final CentroCostosBuilder centroCostosBuilder;
    private final AuditorAware<String> auditorAware;

    public CentroCostosDtoResponse create(Long idData, Long idEmpresa, CentroCostosDtoRequest model) {

        Optional<CnCentroCostosEntity> entidad = cnCentroCostosRepository.findByCentroCostosCodigoOriginal(idData,
                idEmpresa,
                model.getCodigoCentroCostosOriginal());

        if (entidad.isPresent()) {
            throw new GeneralException(MessageFormat.format("El centro de costos con codigo de cuenta: {0} ya se encuentra registrado"
                    , model.getCodigoCentroCostosOriginal()));
        }


        CnCentroCostosEntity cnCentroCostosEntity = centroCostosBuilder.builderEntity(idData, idEmpresa, model);
        cnCentroCostosEntity.setNivel(getNivel(model.getCodigoCentroCostosOriginal(), model.getMayor()));
        return centroCostosBuilder.builderResponse(cnCentroCostosRepository.save(cnCentroCostosEntity));
    }


    public CentroCostosDtoResponse update(Long idData, Long idEmpresa, UUID id, CentroCostosDtoRequest model) {

        CnCentroCostosEntity entidad = cnCentroCostosRepository
                .findByIdCentroCostos(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("El centro de costo no existe id {0}", id)));


        CnCentroCostosEntity cnCentroCostosEntity = centroCostosBuilder.builderUpdateEntity(model, entidad);
        cnCentroCostosEntity.setNivel(getNivel(model.getCodigoCentroCostosOriginal(), model.getMayor()));

        cnCentroCostosEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        cnCentroCostosEntity.setModifiedDate(LocalDateTime.now());

        return centroCostosBuilder.builderResponse(cnCentroCostosRepository.save(cnCentroCostosEntity));
    }


    public CentroCostosDtoResponse findById(Long idData, Long idEmpresa, UUID id) {
        CnCentroCostosEntity entity = cnCentroCostosRepository
                .findByIdCentroCostos(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("El centro de costo no existe id {0}", id)));

        CentroCostosDtoResponse response = centroCostosBuilder.builderResponse(entity);

        if (Objects.nonNull(entity.getIdCodigoCentroCostosPadre())) {
            CnCentroCostosEntity padre = cnCentroCostosRepository
                    .findByIdCentroCostos(idData, idEmpresa, entity.getIdCodigoCentroCostosPadre())
                    .orElseThrow(() -> new GeneralException(
                            MessageFormat.format("El centro de costo no existe id {0}", entity.getIdCodigoCentroCostosPadre())));

            response.setNombreCodigoCentroPadre(padre.getCentroCostos());
        }

        return response;
    }


    public void delete(Long idData, Long idEmpresa, UUID id) {
        CnCentroCostosEntity entidad = cnCentroCostosRepository
                .findByIdCentroCostos(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("El centro de costo no existe id {0}", id)));


        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        cnCentroCostosRepository.save(entidad);
    }


    public List<CentroCostosDtoResponse> findAll(Long idData, Long idEmpresa) {
        return cnCentroCostosRepository.findAll(idData, idEmpresa)
                .stream()
                .map(centroCostosBuilder::builderResponse)
                .toList();

    }

    public PaginatedDto<CentroCostosDtoResponse> findAllPaginate(Long idData, Long idEmpresa,
                                                                 CnPlanCuentaListFilterDto filters, Pageable pageable) {


        Page<CnCentroCostosEntity> page = cnCentroCostosRepository.findAllPaginate(idData, idEmpresa,
                filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);

        PaginatedDto paginatedDto = new PaginatedDto<CnPlanCuentaGetListDto>();

        List<CentroCostosDtoResponse> dtoList = page.stream().map(centroCostosBuilder::builderResponse).toList();
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


    private Integer getNivel(String cuentaOriginal, Boolean mayor) {
        int nivel = cuentaOriginal.length() - cuentaOriginal.replace(".", "").length();
        if (!mayor) {
            return nivel + 1;
        }
        return nivel;
    }


}
