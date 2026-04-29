package com.calero.lili.core.modContabilidad.modAsientos.builder;

import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetListDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CnAsientosBuilder {

    private final CnAsientosDetallesBuilder cnAsientosDetallesBuilder;

    public CnAsientosEntity builderEntity(CreationAsientosRequestDto model, Long idData, Long idEmpresa) {
        return CnAsientosEntity.builder()
                .idAsiento(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .idPeriodo(model.getIdPeriodo())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroAsiento())
                .fechaAsiento(DateUtils.toLocalDate(model.getFechaAsiento()))
                .concepto(model.getConcepto())
                .mayorizado(model.getMayorizado())
                .anulada(model.getAnulada())
                .detalleEntity(cnAsientosDetallesBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .tercero(builderTercero(model.getIdTercero()))
                .build();
    }


    public CnAsientosEntity builderUpdateEntity(CreationAsientosRequestDto model, CnAsientosEntity item) {
        return CnAsientosEntity.builder()
                .idAsiento(item.getIdAsiento())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .idPeriodo(model.getIdPeriodo())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroAsiento())
                .fechaAsiento(DateUtils.toLocalDate(model.getFechaAsiento()))
                .concepto(model.getConcepto())
                .mayorizado(model.getMayorizado())
                .anulada(model.getAnulada())
                .detalleEntity(cnAsientosDetallesBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .tercero(builderTercero(model.getIdTercero()))
                .build();
    }

    public GetDto builderResponse(CnAsientosEntity model) {
        return GetDto.builder()
                .idAsiento(model.getIdAsiento())
                .sucursal(model.getSucursal())
                .idPeriodo(model.getIdPeriodo())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroAsiento())
                .fechaAsiento(model.getFechaAsiento())
                .concepto(model.getConcepto())
                .mayorizado(model.getMayorizado())
                .anulada(model.getAnulada())
                .detalle(cnAsientosDetallesBuilder.builderListResponse(model.getDetalleEntity()))
                .build();
    }

    public GetListDto builderListResponse(CnAsientosEntity model) {
        return GetListDto.builder()
                .idAsiento(model.getIdAsiento())
                .sucursal(model.getSucursal())
                .idPeriodo(model.getIdPeriodo())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroAsiento())
                .fechaAsiento(Objects.nonNull(model.getFechaAsiento())
                        ? DateUtils.toString(model.getFechaAsiento()) : null)
                .concepto(model.getConcepto())
                .mayorizado(model.getMayorizado())
                .anulada(model.getAnulada())
                .build();
    }


    private GeTerceroEntity builderTercero(UUID idTercero) {
        if (Objects.isNull(idTercero)) return null;

        return GeTerceroEntity.builder()
                .idTercero(idTercero)
                .build();
    }
}
