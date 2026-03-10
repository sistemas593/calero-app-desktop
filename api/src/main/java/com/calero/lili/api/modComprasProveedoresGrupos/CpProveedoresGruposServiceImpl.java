package com.calero.lili.api.modComprasProveedoresGrupos;

import com.calero.lili.api.modComprasProveedoresGrupos.builder.CpProveedoresGruposBuilder;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationRequestDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoCreationResponseDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoListFilterDto;
import com.calero.lili.api.modComprasProveedoresGrupos.dto.CpProveedorGrupoReportDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CpProveedoresGruposServiceImpl {

    private final CpProveedoresGruposRepository cpProveedoresGruposRepository;
    private final CpProveedoresGruposBuilder cpProveedoresGruposBuilder;
    private final AuditorAware<String> auditorAware;

    public CpProveedorGrupoCreationResponseDto create(Long idData, Long idEmpresa, CpProveedorGrupoCreationRequestDto request) {
        CpProveedoresGruposEntity createdDto = cpProveedoresGruposRepository
                .save(cpProveedoresGruposBuilder.builderEntity(request, idData, idEmpresa));
        return cpProveedoresGruposBuilder.builderResponse(createdDto);
    }

    public CpProveedorGrupoCreationResponseDto update(Long idData, Long idEmpresa, UUID id, CpProveedorGrupoCreationRequestDto request) {

        CpProveedoresGruposEntity entidad = cpProveedoresGruposRepository.findByIdDataAndIdEmpresaAndIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException("No existe el grupo"));

        CpProveedoresGruposEntity update = cpProveedoresGruposBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        update.setModifiedDate(LocalDateTime.now());

        cpProveedoresGruposRepository.save(update);
        return cpProveedoresGruposBuilder.builderResponse(update);
    }

    public void delete(Long idData, Long idEmpresa, UUID id) {

        CpProveedoresGruposEntity entidad = cpProveedoresGruposRepository.findByIdDataAndIdEmpresaAndIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException("No existe el grupo"));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        entidad.setDeletedDate(LocalDateTime.now());

        cpProveedoresGruposRepository.save(entidad);
    }

    public CpProveedorGrupoCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        CpProveedoresGruposEntity entidad = cpProveedoresGruposRepository.findByIdDataAndIdEmpresaAndIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException("No existe el grupo"));
        return cpProveedoresGruposBuilder.builderResponse(entidad);
    }

    public List<CpProveedorGrupoReportDto> findAllPaginate(Long idData, Long idEmpresa, CpProveedorGrupoListFilterDto filters, Pageable pageable) {

        return cpProveedoresGruposRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(), (filters.getFilter() != null)
                        ? filters.getFilter() : "")
                .stream()
                .map(cpProveedoresGruposBuilder::builderListResponse)
                .toList();

    }


    public CpProveedorGrupoCreationResponseDto updatePredeterminado(UUID idGrupo, Long idData, Long idEmpresa) {

        List<CpProveedoresGruposEntity> listGrupo = cpProveedoresGruposRepository.findAllPaginate(idData, idEmpresa, "", "");
        CpProveedoresGruposEntity proveedorGrupo = cpProveedoresGruposRepository.findByIdDataAndIdEmpresaAndIdGrupo(idData, idEmpresa, idGrupo)
                .orElseThrow(() -> new GeneralException("No existe el grupo"));

        if (!proveedorGrupo.getPredeterminado()) {

            for (CpProveedoresGruposEntity entidad : listGrupo) {
                entidad.setPredeterminado(Boolean.FALSE);
                cpProveedoresGruposRepository.save(entidad);
            }
            proveedorGrupo.setPredeterminado(Boolean.TRUE);
            cpProveedoresGruposRepository.save(proveedorGrupo);
        }

        return cpProveedoresGruposBuilder.builderResponse(proveedorGrupo);
    }
}
