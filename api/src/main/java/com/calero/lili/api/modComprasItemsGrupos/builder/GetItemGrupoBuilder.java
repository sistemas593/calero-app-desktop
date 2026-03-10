package com.calero.lili.api.modComprasItemsGrupos.builder;

import com.calero.lili.api.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoCreationRequestDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetListDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetOneDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetItemGrupoBuilder {


    public GeItemGrupoEntity builderEntity(GeItemGrupoCreationRequestDto model,Long idData, Long idEmpresa) {
        return GeItemGrupoEntity.builder()

                .idGrupo(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .grupo(model.getGrupo())
                .tipoGrupo(model.getTipoGrupo())
                .idCuentaInventario(model.getIdCuentaInventario())
                .idCuentaIngreso(model.getIdCuentaIngreso())
                .idCuentaCosto(model.getIdCuentaCosto())
                .idCuentaDescuento(model.getIdCuentaDescuento())
                .idCuentaDevolucion(model.getIdCuentaDevolucion())
                .idCuentaGasto(model.getIdCuentaGasto())
                .build();
    }


    public GeItemGrupoEntity builderUpdateEntity(GeItemGrupoCreationRequestDto model, GeItemGrupoEntity item) {
        return GeItemGrupoEntity.builder()

                .idGrupo(item.getIdGrupo())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .grupo(model.getGrupo())
                .tipoGrupo(model.getTipoGrupo())
                .idCuentaInventario(model.getIdCuentaInventario())
                .idCuentaIngreso(model.getIdCuentaIngreso())
                .idCuentaCosto(model.getIdCuentaCosto())
                .idCuentaDescuento(model.getIdCuentaDescuento())
                .idCuentaDevolucion(model.getIdCuentaDevolucion())
                .idCuentaGasto(model.getIdCuentaGasto())
                .build();
    }

    public GeItemGrupoGetOneDto builderDto(GeItemGrupoEntity model){
        return GeItemGrupoGetOneDto.builder()
                .idGrupo(model.getIdGrupo())
                .grupo(model.getGrupo())
                .tipoGrupo(model.getTipoGrupo())
                .idCuentaInventario(model.getIdCuentaInventario())
                .idCuentaIngreso(model.getIdCuentaIngreso())
                .idCuentaCosto(model.getIdCuentaCosto())
                .idCuentaDescuento(model.getIdCuentaDescuento())
                .idCuentaDevolucion(model.getIdCuentaDevolucion())
                .idCuentaGasto(model.getIdCuentaGasto())
                .build();
    }

    public GeItemGrupoGetListDto builderListDto(GeItemGrupoEntity model){
        return GeItemGrupoGetListDto.builder()
                .idGrupo(model.getIdGrupo())
                .grupo(model.getGrupo())
                .tipoGrupo(model.getTipoGrupo())
                .idCuentaInventario(model.getIdCuentaInventario())
                .idCuentaIngreso(model.getIdCuentaIngreso())
                .idCuentaCosto(model.getIdCuentaCosto())
                .idCuentaDescuento(model.getIdCuentaDescuento())
                .idCuentaDevolucion(model.getIdCuentaDevolucion())
                .idCuentaGasto(model.getIdCuentaGasto())
                .build();
    }

}
