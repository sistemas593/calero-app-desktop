package com.calero.lili.api.modTerceros.builder;

import com.calero.lili.api.modComprasProveedoresGrupos.CpProveedoresGruposEntity;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.api.modTerceros.GeTercerosGruposClientesEntity;
import com.calero.lili.api.modTerceros.GeTercerosGruposProveedoresEntity;
import com.calero.lili.api.modVentasCientesGrupos.VtClienteGrupoEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GeTercerosGruposBuilder {

    public GeTercerosGruposClientesEntity builderClienteEntity(VtClienteGrupoEntity grupo,
                                                               GeTerceroEntity tercero,
                                                               Long idData, Long idEmpresa) {
        return GeTercerosGruposClientesEntity.builder()
                .idTerceroGrupoCliente(UUID.randomUUID())
                .grupo(grupo)
                .tercero(tercero)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .build();
    }

    public GeTercerosGruposProveedoresEntity builderProveedorEntity(CpProveedoresGruposEntity grupo,
                                                                    GeTerceroEntity tercero,
                                                                    Long idData, Long idEmpresa) {
        return GeTercerosGruposProveedoresEntity.builder()
                .idTerceroGrupoProveedor(UUID.randomUUID())
                .grupo(grupo)
                .tercero(tercero)
                .idData(idData)
                .idEmpresa(idEmpresa)
                .build();
    }

}
