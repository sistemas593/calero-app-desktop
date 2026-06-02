package com.calero.lili.core.modContabilidad.modAsientos.builder;

import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
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
                .idCuenta(Objects.nonNull(model.getCuenta()) ? model.getCuenta().getIdCuenta() : null)
                .cuenta(Objects.nonNull(model.getCuenta()) ? model.getCuenta().getCuenta() : null)
                .codigoCuenta(Objects.nonNull(model.getCuenta()) ? model.getCuenta().getCodigoCuenta() : null)
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .tercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .idItem(Objects.nonNull(model.getGeItem()) ? model.getGeItem().getIdItem() : null)
                .item(Objects.nonNull(model.getGeItem()) ? model.getGeItem().getDescripcion() : null)
                .idCentroCosto(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getIdCentroCostos() : null)
                .centroCosto(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getCentroCostos() : null)
                .codigoCentroCosto(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getCodigoCentroCostos() : null)
                .build();
    }
}
