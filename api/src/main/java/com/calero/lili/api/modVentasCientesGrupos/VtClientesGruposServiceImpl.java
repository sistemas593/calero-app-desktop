package com.calero.lili.api.modVentasCientesGrupos;

import com.calero.lili.api.modVentasCientesGrupos.builder.VtClienteGrupoBuilder;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationRequestDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoCreationResponseDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoListFilterDto;
import com.calero.lili.api.modVentasCientesGrupos.dto.VtClienteGrupoReportDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VtClientesGruposServiceImpl {

    private final VtClientesGruposRepository vtClientesGruposRepository;
    private final VtClienteGrupoBuilder vtClienteGrupoBuilder;


    public VtClienteGrupoCreationResponseDto create(Long idData, Long idEmpresa,
                                                    VtClienteGrupoCreationRequestDto request, String usuario) {

        VtClienteGrupoEntity entity = vtClienteGrupoBuilder.builderEntity(request, idData, idEmpresa);
        entity.setCreatedBy(usuario);
        entity.setCreatedDate(LocalDateTime.now());
        VtClienteGrupoEntity createdDto = vtClientesGruposRepository.save(entity);
        return vtClienteGrupoBuilder.builderClienteGroupResponse(createdDto);
    }

    public VtClienteGrupoCreationResponseDto update(Long idData, Long idEmpresa, UUID id,
                                                    VtClienteGrupoCreationRequestDto request, String usuario) {

        VtClienteGrupoEntity entidad = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        VtClienteGrupoEntity update = vtClienteGrupoBuilder.builderUpdateEntity(request, entidad);

        update.setModifiedBy(usuario);
        update.setModifiedDate(LocalDateTime.now());

        vtClientesGruposRepository.save(update);
        return vtClienteGrupoBuilder.builderClienteGroupResponse(entidad);

    }

    public void delete(Long idData, Long idEmpresa, UUID id, String usuario) {

        VtClienteGrupoEntity entidad = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));

        entidad.setDelete(Boolean.TRUE);
        entidad.setDeletedBy(usuario);
        entidad.setDeletedDate(LocalDateTime.now());

        vtClientesGruposRepository.save(entidad);

    }

    public VtClienteGrupoCreationResponseDto findFirstById(Long idData, Long idEmpresa, UUID id) {

        VtClienteGrupoEntity entidad = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no existe", id)));
        return vtClienteGrupoBuilder.builderClienteGroupResponse(entidad);
    }

    public List<VtClienteGrupoReportDto> findAllPaginate(Long idData, Long idEmpresa, VtClienteGrupoListFilterDto filters, Pageable pageable) {

        return vtClientesGruposRepository.findAllPaginate(idData, idEmpresa, filters.getFilter(),
                        (filters.getFilter() != null) ? filters.getFilter() : "")
                .stream()
                .map(vtClienteGrupoBuilder::builderListClienteGroupResponse)
                .toList();

    }

    public VtClienteGrupoCreationResponseDto updatePredeterminado(UUID idClienteGrupo, Long idData, Long idEmpresa) {

        List<VtClienteGrupoEntity> clienteGruposList = vtClientesGruposRepository.findAllPaginate(idData, idEmpresa, "", "");
        VtClienteGrupoEntity clienteGrupo = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, idClienteGrupo)
                .orElseThrow(() -> new GeneralException("No existe grupo"));

        if (!clienteGrupo.getPredeterminado()) {

            for (VtClienteGrupoEntity entidad : clienteGruposList) {
                entidad.setPredeterminado(Boolean.FALSE);
                vtClientesGruposRepository.save(entidad);
            }
            clienteGrupo.setPredeterminado(Boolean.TRUE);
            vtClientesGruposRepository.save(clienteGrupo);
        }

        return vtClienteGrupoBuilder.builderClienteGroupResponse(clienteGrupo);
    }
}
