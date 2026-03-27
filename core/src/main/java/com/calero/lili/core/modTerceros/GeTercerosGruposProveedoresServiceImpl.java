package com.calero.lili.core.modTerceros;

import com.calero.lili.core.modComprasProveedoresGrupos.CpProveedoresGruposEntity;
import com.calero.lili.core.modComprasProveedoresGrupos.CpProveedoresGruposRepository;
import com.calero.lili.core.modTerceros.builder.GeTercerosGruposBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GeTercerosGruposProveedoresServiceImpl {

    private final GeTercerosGruposProveedoresRepository geTercerosGruposProveedoresRepository;
    private final CpProveedoresGruposRepository cpProveedoresGruposRepository;
    private final GeTercerosGruposBuilder geTercerosGruposBuilder;

    public void save(UUID idGrupo, GeTerceroEntity tercero, Long idData, Long idEmpresa) {

        Optional<CpProveedoresGruposEntity> entidad = getGrupoProveedor(idGrupo, idData, idEmpresa);
        entidad.ifPresent(grupo -> validateSaveGroup(tercero, idData, idEmpresa, grupo));
    }

    private Optional<CpProveedoresGruposEntity> getGrupoProveedor(UUID idGrupo, Long idData, Long idEmpresa) {
        return cpProveedoresGruposRepository.findByIdDataAndIdEmpresaAndIdGrupo(idData,
                idEmpresa, idGrupo);
    }

    public void update(UUID idGrupo, GeTerceroEntity tercero, Long idData, Long idEmpresa) {

        List<GeTercerosGruposProveedoresEntity> entidades = geTercerosGruposProveedoresRepository
                .findByIdTercero(idData, idEmpresa, tercero.getIdTercero());

        if (Objects.nonNull(entidades) && !entidades.isEmpty()) {
            entidades.forEach(geTercerosGruposProveedoresRepository::delete);

            Optional<CpProveedoresGruposEntity> entidad = getGrupoProveedor(idGrupo, idData, idEmpresa);
            entidad.ifPresent(grupo -> validateSaveGroup(tercero, idData, idEmpresa, grupo));
        } else {
            Optional<CpProveedoresGruposEntity> entidad = getGrupoProveedor(idGrupo, idData, idEmpresa);
            entidad.ifPresent(grupo -> validateSaveGroup(tercero, idData, idEmpresa, grupo));
        }

    }

    private void validateSaveGroup(GeTerceroEntity tercero, Long idData, Long idEmpresa, CpProveedoresGruposEntity entidad) {
        if (!entidad.getPredeterminado()) {
            geTercerosGruposProveedoresRepository.save(geTercerosGruposBuilder
                    .builderProveedorEntity(entidad, tercero, idData, idEmpresa));
        }

    }


    // TODO CORRECIÓN, EN CASO DE NO EXISTIR PREDETERMNIADO, DEVUELVA EL ERROR, POR CIERTO GUARDAR CLIENTES EN DESKTOP
    public CpProveedoresGruposEntity getGrupo(GeTerceroEntity tercero, Long idEmpresa) {
        Optional<GeTercerosGruposProveedoresEntity> grupo = geTercerosGruposProveedoresRepository
                .findByDataTercero(tercero.getIdData(), idEmpresa, tercero.getIdTercero());

        if (grupo.isPresent()) {
            return grupo.get().getGrupo();
        } else {
            Optional<CpProveedoresGruposEntity> predeterminado = cpProveedoresGruposRepository
                    .findByIdPredeterminado(tercero.getIdData(), idEmpresa, Boolean.TRUE);
            return predeterminado.orElse(null);
        }

    }

}
