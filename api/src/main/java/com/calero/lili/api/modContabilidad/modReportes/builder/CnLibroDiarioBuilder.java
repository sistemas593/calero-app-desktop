package com.calero.lili.api.modContabilidad.modReportes.builder;

import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosDetalleEntity;
import com.calero.lili.api.modContabilidad.modAsientos.CnAsientosEntity;
import com.calero.lili.api.modContabilidad.modAsientos.dto.LibroDiarioDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CnLibroDiarioBuilder {

    public LibroDiarioDto builderLibroDiario(CnAsientosEntity model) {
        return LibroDiarioDto.builder()
                .idAsiento(model.getIdAsiento())
                .fechaAsiento(DateUtils.toString(model.getFechaAsiento()))
                .numeroAsiento(model.getNumeroAsiento())
                .concepto(model.getConcepto())
                .detalleCuentas(builderListDetalleLibroDiario(model.getDetalleEntity()))
                .build();
    }

    private List<LibroDiarioDto.DetalleCuenta> builderListDetalleLibroDiario(List<CnAsientosDetalleEntity> detalleEntity) {
        return detalleEntity.stream()
                .map(this::builderDetalleLibroDiario)
                .toList();
    }

    private LibroDiarioDto.DetalleCuenta builderDetalleLibroDiario(CnAsientosDetalleEntity model) {
        return LibroDiarioDto.DetalleCuenta.builder()
                .idCuenta(Objects.nonNull(model.getCuenta()) ? model.getCuenta().getIdCuenta() : null)
                .codigoCuenta(model.getCuenta().getCodigoCuenta())
                .nombreCuenta(model.getCuenta().getCuenta())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .idCentroCostos(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getIdCentroCostos() : null)
                .nombreCentroCostos(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getCentroCostos() : null)
                .codigoCentroCostos(Objects.nonNull(model.getCentroCostos()) ? model.getCentroCostos().getCodigoCentroCostos() : null)
                .debe(model.getDebe())
                .haber(model.getHaber())
                .build();
    }

}
