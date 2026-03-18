package com.calero.lili.core.modContabilidad.modPlanCuentas;

import com.calero.lili.core.builder.ResponseApiBuilder;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modContabilidad.modPlanCuentas.builder.CnPlanCuentaBuilder;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaCreationRequestDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetListDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetOneDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CnPlanCuentasServiceImpl {

    private final CnPlanCuentasRepository cnPlanCuentasRepository;
    private final ResponseApiBuilder responseApiBuilder;
    private final CnPlanCuentaBuilder cnPlanCuentaBuilder;


    public ResponseDto create(Long idData, Long idEmpresa, CnPlanCuentaCreationRequestDto request, String usuario) {

        Optional<CnPlanCuentaEntity> existe = cnPlanCuentasRepository.findByCodigoCuenta(idData, idEmpresa, request.getCodigoCuenta());
        if (existe.isPresent()) {
            throw new GeneralException(MessageFormat.format("La cuenta con codigo {0} ya existe", request.getCodigoCuenta()));
        }


        CnPlanCuentaEntity saved = cnPlanCuentaBuilder
                .builderEntity(request, idData, idEmpresa);

        saved.setNivel(getNivel(saved.getCodigoCuentaOriginal(), saved.getMayor()));
        saved.setGrupo(validarNumeroGrupo(request));
        saved.setCreatedBy(usuario);
        saved.setCreatedDate(LocalDateTime.now());
        CnPlanCuentaEntity entidad = cnPlanCuentasRepository.save(saved);

        return responseApiBuilder.builderResponse(entidad.getIdCuenta().toString());

    }


    public ResponseDto update(Long idData, Long idEmpresa, UUID id, CnPlanCuentaCreationRequestDto request, String usuario) {

        CnPlanCuentaEntity existe = cnPlanCuentasRepository.findByIdCuenta(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Cuenta con id {0} no existe", id)));

        CnPlanCuentaEntity updated = cnPlanCuentaBuilder.builderUpdateEntity(request, existe);

        updated.setModifiedBy(usuario);
        updated.setModifiedDate(LocalDateTime.now());

        updated.setGrupo(validarNumeroGrupo(request));
        CnPlanCuentaEntity entidad = cnPlanCuentasRepository
                .save(updated);
        return responseApiBuilder.builderResponse(entidad.getIdCuenta().toString());
    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {
        CnPlanCuentaEntity planCuenta = cnPlanCuentasRepository.findByIdCuenta(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Cuenta con id {0} no existe", id)));


        planCuenta.setDelete(Boolean.TRUE);
        planCuenta.setDeletedBy(usuario);
        planCuenta.setDeletedDate(LocalDateTime.now());

        cnPlanCuentasRepository.delete(planCuenta);
    }

    public CnPlanCuentaGetOneDto findById(Long idData, Long idEmpresa, UUID id) {

        CnPlanCuentaEntity entidad = cnPlanCuentasRepository.findByIdCuenta(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Cuenta con id {0} no existe", id)));
        return cnPlanCuentaBuilder.builderResponse(entidad);
    }

    public List<CnPlanCuentaGetListDto> findAll(Long idData, Long idEmpresa) {

        List<CnPlanCuentaEntity> page = cnPlanCuentasRepository.findAll(idData, idEmpresa);
        List<CnPlanCuentaGetListDto> dtoList = page.stream().map(cnPlanCuentaBuilder::builderListResponse).toList();

        return dtoList;
    }

    public PaginatedDto<CnPlanCuentaGetListDto> findAllPaginate(Long idData, Long idEmpresa, CnPlanCuentaListFilterDto filters, Pageable pageable) {

        Page<CnPlanCuentaEntity> page = cnPlanCuentasRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "", pageable);
        PaginatedDto paginatedDto = new PaginatedDto<CnPlanCuentaGetListDto>();

        List<CnPlanCuentaGetListDto> dtoList = page.stream().map(cnPlanCuentaBuilder::builderListResponse).toList();
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


    private Integer validarNumeroGrupo(CnPlanCuentaCreationRequestDto request) {
        String grupo = request.getCodigoCuenta().substring(0, 1);
        return Integer.parseInt(grupo);
    }

    private Integer getNivel(String cuentaOriginal, Boolean mayor) {
        int nivel = cuentaOriginal.length() - cuentaOriginal.replace(".", "").length();
        if (!mayor) {
            return nivel + 1;
        }
        return nivel;
    }

}
