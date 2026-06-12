package com.calero.lili.core.modContabilidad.modReportes.builder;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.core.modContabilidad.modReportes.dto.DetalleMayorDtoPDF;
import com.calero.lili.core.modContabilidad.modReportes.dto.MayorGeneralDto;
import com.calero.lili.core.modContabilidad.modReportes.projection.CabeceraMayorProjection;
import com.calero.lili.core.modContabilidad.modReportes.projection.MayorGeneralProjection;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CnReporteMayorGeneralBuilder {


    private final FormatoValores formatoValores;

    public MayorGeneralDto.DetalleMayorGeneralDto builderMayorGeneralDto(MayorGeneralProjection model) {
        return MayorGeneralDto.DetalleMayorGeneralDto.builder()
                .idAsiento(model.getIdAsiento())
                .fechaAsiento(DateUtils.toString(model.getFechaAsiento()))
                .cuenta(model.getCuenta())
                .tipoAsiento(model.getTipoAsiento())
                .numeroAsiento(model.getNumeroAsiento())
                .tipoDocumento(model.getTipoDocumento())
                .numeroDocumento(model.getNumeroDocumento())
                .concepto(model.getConcepto())
                .debe(model.getDebe())
                .haber(model.getHaber())
                .saldo(model.getSaldoAcumulado())
                .centroCostos(model.getCentroCostos())
                .codigoCentroCostos(model.getCodigoCentroCostos())
                .idTercero(model.getIdTercero())
                .tercero(model.getTercero())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idItem(model.getIdItem())
                .item(model.getDescripcion())
                .tercero(model.getTercero())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .item(model.getDescripcion())
                .codigoItem(model.getCodigoPrincipal())
                .build();
    }

    public MayorGeneralDto.CabeceraMayorGeneralDto builderMayorCabeceraGeneralDto(CabeceraMayorProjection model) {
        return MayorGeneralDto.CabeceraMayorGeneralDto.builder()
                .numeroCuenta(model.getCodigoCuenta())
                .cuenta(model.getCuenta())
                .saldo(model.getSaldoInicial())
                .build();
    }

    public MayorGeneralDto.CabeceraMayorGeneralDto builderDefault(CnPlanCuentaEntity cuenta) {
        return MayorGeneralDto.CabeceraMayorGeneralDto.builder()
                .numeroCuenta(cuenta.getCodigoCuenta())
                .cuenta(cuenta.getCuenta())
                .saldo(new BigDecimal("0.00"))
                .build();
    }

    public List<DetalleMayorDtoPDF> builderListMayorGeneralPdf(List<MayorGeneralDto.DetalleMayorGeneralDto> list) {
        return list.stream()
                .map(this::builderMayorGeneralPdf)
                .toList();
    }

    private DetalleMayorDtoPDF builderMayorGeneralPdf(MayorGeneralDto.DetalleMayorGeneralDto item) {
        return DetalleMayorDtoPDF.builder()
                .fechaAsiento(item.getFechaAsiento())
                .tipoAsiento(item.getTipoAsiento())
                .numeroAsiento(item.getNumeroAsiento())
                .concepto(item.getConcepto())
                .debe(item.getDebe().compareTo(BigDecimal.ZERO) == 0
                        ? "" : formatoValores.convertirBigDecimalToStringPDF(item.getDebe()))
                .haber(item.getHaber().compareTo(BigDecimal.ZERO) == 0
                        ? "" : formatoValores.convertirBigDecimalToStringPDF(item.getHaber()))
                .saldo(item.getSaldo().compareTo(BigDecimal.ZERO) == 0
                        ? "" : formatoValores.convertirBigDecimalToStringPDF(item.getSaldo()))
                .cuenta(item.getCuenta())
                .numeroIdentificacion(Objects.nonNull(item.getNumeroIdentificacion()) ? item.getNumeroIdentificacion() : "")
                .tercero(Objects.nonNull(item.getTercero()) ? item.getTercero() : "")
                .item(Objects.nonNull(item.getItem()) ? item.getItem() : "")
                .codigoItem(Objects.nonNull(item.getCodigoItem()) ? item.getCodigoItem() : "")
                .build();
    }

}
