package com.calero.lili.api.modTerceros;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.api.modTerceros.builder.GeTercerosGruposBuilder;
import com.calero.lili.api.modVentasCientesGrupos.VtClienteGrupoEntity;
import com.calero.lili.api.modVentasCientesGrupos.VtClientesGruposRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GeTercerosGruposClientesServiceImpl {

    private final GeTercerosGruposClientesRepository geTercerosGruposClientesRepository;
    private final VtClientesGruposRepository vtClientesGruposRepository;
    private final GeTercerosGruposBuilder geTercerosGruposBuilder;

    public void save(UUID idGrupo, GeTerceroEntity tercero, Long idData, Long idEmpresa) {

        Optional<VtClienteGrupoEntity> entidad = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, idGrupo);

        if (entidad.isPresent()) {
            if (!entidad.get().getPredeterminado()) {
                validateSaveGroup(tercero, idData, idEmpresa, entidad.get());
            }
        }

    }

    public void update(UUID idGrupo, GeTerceroEntity tercero, Long idData, Long idEmpresa) {

        List<GeTercerosGruposClientesEntity> entidades = geTercerosGruposClientesRepository
                .findByIdTercero(idData, idEmpresa, tercero.getIdTercero());

        if (Objects.nonNull(entidades) && !entidades.isEmpty()) {
            entidades.forEach(geTercerosGruposClientesRepository::delete);
            Optional<VtClienteGrupoEntity> grupo = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, idGrupo);
            if (grupo.isPresent()) {
                if (!grupo.get().getPredeterminado()) {
                    validateSaveGroup(tercero, idData, idEmpresa, grupo.get());
                }
            }
        } else {
            Optional<VtClienteGrupoEntity> grupo = vtClientesGruposRepository.findByIdGrupo(idData, idEmpresa, idGrupo);
            if (grupo.isPresent()) {
                if (!grupo.get().getPredeterminado()) {
                    validateSaveGroup(tercero, idData, idEmpresa, grupo.get());
                }
            }
        }

    }

    private void validateSaveGroup(GeTerceroEntity tercero, Long idData, Long idEmpresa, VtClienteGrupoEntity entidad) {
        geTercerosGruposClientesRepository.save(geTercerosGruposBuilder
                .builderClienteEntity(entidad, tercero, idData, idEmpresa));
    }

    public VtClienteGrupoEntity getGrupo(GeTerceroEntity tercero, Long idEmpresa) {
        Optional<GeTercerosGruposClientesEntity> grupo = geTercerosGruposClientesRepository
                .findByDataTercero(tercero.getIdData(), idEmpresa, tercero.getIdTercero());

        if (grupo.isPresent()) {
            return grupo.get().getGrupo();
        } else {
            return vtClientesGruposRepository.findByIdPredeterminado(tercero.getIdData(), idEmpresa, Boolean.TRUE)
                    .orElseThrow(() -> new GeneralException("No existe el grupo"));
        }

    }

}
