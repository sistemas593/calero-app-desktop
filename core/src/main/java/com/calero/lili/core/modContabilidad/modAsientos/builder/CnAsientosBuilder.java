package com.calero.lili.core.modContabilidad.modAsientos.builder;

import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetAsientoDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetListDto;
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
                .build();
    }


    public CnAsientosEntity builderUpdateEntity(CreationAsientosRequestDto model, CnAsientosEntity item) {

        item.setSucursal(model.getSucursal());
        item.setIdPeriodo(model.getIdPeriodo());
        item.setTipoAsiento(model.getTipoAsiento());
        item.setNumeroAsiento(model.getNumeroAsiento());
        item.setFechaAsiento(DateUtils.toLocalDate(model.getFechaAsiento()));
        item.setConcepto(model.getConcepto());
        item.setMayorizado(model.getMayorizado());
        item.setAnulada(model.getAnulada());


        return item;
       /*return CnAsientosEntity.builder()
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
                .build();*/
    }

    public GetAsientoDto builderResponse(CnAsientosEntity model) {
        return GetAsientoDto.builder()
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
}
