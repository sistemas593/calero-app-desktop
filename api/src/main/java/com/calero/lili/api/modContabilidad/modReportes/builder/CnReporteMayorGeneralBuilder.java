package com.calero.lili.api.modContabilidad.modReportes.builder;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.modContabilidad.modPlanCuentas.CnPlanCuentaEntity;
import com.calero.lili.api.modContabilidad.modReportes.dto.DetalleMayorDtoPDF;
import com.calero.lili.api.modContabilidad.modReportes.dto.MayorGeneralDto;
import com.calero.lili.api.modContabilidad.modReportes.projection.CabeceraMayorProjection;
import com.calero.lili.api.modContabilidad.modReportes.projection.MayorGeneralProjection;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
                .build();
    }

}
