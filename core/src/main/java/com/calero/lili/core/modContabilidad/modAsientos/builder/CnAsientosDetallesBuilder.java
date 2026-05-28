package com.calero.lili.core.modContabilidad.modAsientos.builder;

import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CnAsientosDetallesBuilder {


    public CnAsientosDetalleEntity builderAsientoDetalle(CreationAsientosRequestDto.DetailDto model, Long idData, Long idEmpresa) {
        return CnAsientosDetalleEntity.builder()
                .idAsientoDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .itemOrden(model.getItemOrden())
                .detalle(model.getDetalle())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento())
                        ? DateUtils.toLocalDate(model.getFechaDocumento()) : null)
                .debe(model.getDebe())
                .haber(model.getHaber())
                .cuenta(builderCuenta(model.getIdCuenta()))
                .build();
    }


    private CnPlanCuentaEntity builderCuenta(UUID idCuenta) {
        return CnPlanCuentaEntity.builder()
                .idCuenta(idCuenta)
                .build();
    }


    public List<DetalleGetDto> builderListResponse(List<CnAsientosDetalleEntity> list) {
        return list.stream()
                .map(this::builderResponseDetalle)
                .toList();
    }

    private DetalleGetDto builderResponseDetalle(CnAsientosDetalleEntity model) {
        return DetalleGetDto.builder()
                .itemOrden(model.getItemOrden())
                .detalle(model.getDetalle())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .fechaDocumento(Objects.nonNull(model.getFechaDocumento())
                        ? DateUtils.toString(model.getFechaDocumento()) : null)
                .debe(model.getDebe())
                .haber(model.getHaber())
                .cuentas(builderResponseCuenta(model.getCuenta()))
                .cliente(builderReponseTercero(model.getTercero()))
                .item(builderResponseItem(model.getGeItem()))
                .build();
    }

    private DetalleGetDto.Cliente builderReponseTercero(GeTerceroEntity cliente) {
        if (Objects.isNull(cliente)) return null;
        return DetalleGetDto.Cliente.builder()
                .idTercero(cliente.getIdTercero())
                .cliente(cliente.getTercero())
                .build();
    }


    private DetalleGetDto.GeItem builderResponseItem(GeItemEntity geItem) {
        if (Objects.isNull(geItem)) return null;
        return DetalleGetDto.GeItem.builder()
                .idItem(geItem.getIdItem())
                .item(geItem.getDescripcion())
                .build();
    }


    private DetalleGetDto.Cuenta builderResponseCuenta(CnPlanCuentaEntity cuenta) {
        if (Objects.isNull(cuenta)) return null;
        return DetalleGetDto.Cuenta.builder()
                .idCuenta(cuenta.getIdCuenta())
                .codigoCuenta(cuenta.getCodigoCuenta())
                .cuenta(cuenta.getCuenta())
                .build();
    }

}
