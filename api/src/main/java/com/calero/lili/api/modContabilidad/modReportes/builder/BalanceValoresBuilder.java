package com.calero.lili.api.modContabilidad.modReportes.builder;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.modContabilidad.modReportes.dto.BalanceComprobacionPdfDto;
import com.calero.lili.api.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.api.modContabilidad.modReportes.projection.BalanceValoresProjection;
import com.calero.lili.api.modContabilidad.modReportes.projection.SaldoInicialBCProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class BalanceValoresBuilder {

    private final FormatoValores formatoValores;


    public List<BalanceValoresDto> builderDefaultListBalanceComprobacion() {

        List<BalanceValoresDto> lista = new ArrayList<>();
        BalanceValoresDto dto = BalanceValoresDto.builder()
                .idCuenta(UUID.randomUUID())
                .codigoCuentaOriginal("")
                .idCuentaPadre(UUID.randomUUID())
                .codigoCuenta("")
                .cuenta("")
                .saldoFinal(BigDecimal.ZERO)
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoInicial(BigDecimal.ZERO)
                .build();
        lista.add(dto);
        return lista;

    }

    public BalanceValoresDto builderBalanceComprobacionCuenta(BalanceValoresProjection model) {
        return BalanceValoresDto.builder()
                .idCuenta(model.getIdCuenta())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .saldoFinal(BigDecimal.ZERO)
                .debe(model.getDebe())
                .haber(model.getHaber())
                .saldoInicial(BigDecimal.ZERO)
                .esMayor(model.getMayor())
                .build();
    }


    public BalanceValoresDto builderEstadoFinancieroCuentaSaldoFinal(BalanceValoresProjection model) {
        return BalanceValoresDto.builder()
                .idCuenta(model.getIdCuenta())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .saldoFinal(model.getSaldoFinal())
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoInicial(BigDecimal.ZERO)
                .grupo(model.getGrupo())
                .totalMayor(Boolean.FALSE)
                .totalMayor(Boolean.FALSE)
                .centroCostos(model.getCentroCostos())
                .codigoCentroCostos(model.getCodigoCentroCostos())
                .build();
    }


    public BalanceValoresDto builderEstadoComparativo(BalanceValoresProjection model) {

        return BalanceValoresDto.builder()
                .idCuenta(model.getIdCuenta())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .saldoFinal(model.getSaldoFinal())
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoInicial(BigDecimal.ZERO)
                .grupo(model.getGrupo())
                .fechaMes(model.getMesFecha())
                .enero(BigDecimal.ZERO)
                .febrero(BigDecimal.ZERO)
                .marzo(BigDecimal.ZERO)
                .abril(BigDecimal.ZERO)
                .mayo(BigDecimal.ZERO)
                .junio(BigDecimal.ZERO)
                .julio(BigDecimal.ZERO)
                .agosto(BigDecimal.ZERO)
                .septiembre(BigDecimal.ZERO)
                .octubre(BigDecimal.ZERO)
                .noviembre(BigDecimal.ZERO)
                .diciembre(BigDecimal.ZERO)
                .totalMayor(Boolean.FALSE)
                .build();
    }


    public BalanceValoresDto builderBalanceComprobacionCuentaSaldoInicial(SaldoInicialBCProjection model) {
        return BalanceValoresDto.builder()
                .idCuenta(model.getIdCuenta())
                .codigoCuenta(model.getCodigoCuenta())
                .codigoCuentaOriginal(model.getCodigoCuentaOriginal())
                .cuenta(model.getCuenta())
                .saldoInicial(model.getSaldoInicial())
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoFinal(BigDecimal.ZERO)
                .esMayor(model.getMayor())
                .build();
    }


    public BalanceComprobacionPdfDto builderPdfValores(BalanceValoresDto item) {
        return BalanceComprobacionPdfDto.builder()
                .codigoCuenta(item.getCodigoCuenta())
                .cuenta(item.getCuenta())
                .saldoInicial(item.getSaldoInicial().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(item.getSaldoInicial()))
                .debe(item.getDebe().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(item.getDebe()))
                .haber(item.getHaber().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(item.getHaber()))
                .saldoFinal(item.getSaldoFinal().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(item.getSaldoFinal()))
                .build();
    }

    public BalanceValoresDto builderEstadoFinancieroUtilidad(String cuenta, String codigoCuenta,
                                                             String codigoCuentaOriginal, BigDecimal utilidad) {
        return BalanceValoresDto.builder()
                .idCuenta(UUID.randomUUID())
                .cuenta(cuenta)
                .codigoCuenta(codigoCuenta)
                .codigoCuentaOriginal(codigoCuentaOriginal)
                .saldoInicial(BigDecimal.ZERO)
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoFinal(utilidad)
                .grupo(3)
                .tieneMovimiento(Boolean.TRUE)
                .build();
    }


    public BalanceValoresDto builderEstadoFinancieroComparativoUtilidad(String cuenta, String codigoCuenta,
                                                                        String codigoCuentaOriginal) {
        return BalanceValoresDto.builder()
                .idCuenta(UUID.randomUUID())
                .cuenta(cuenta)
                .codigoCuenta(codigoCuenta)
                .codigoCuentaOriginal(codigoCuentaOriginal)
                .saldoInicial(BigDecimal.ZERO)
                .debe(BigDecimal.ZERO)
                .haber(BigDecimal.ZERO)
                .saldoFinal(BigDecimal.ZERO)
                .grupo(3)
                .tieneMovimiento(Boolean.TRUE)
                .enero(BigDecimal.ZERO)
                .febrero(BigDecimal.ZERO)
                .marzo(BigDecimal.ZERO)
                .abril(BigDecimal.ZERO)
                .mayo(BigDecimal.ZERO)
                .junio(BigDecimal.ZERO)
                .julio(BigDecimal.ZERO)
                .agosto(BigDecimal.ZERO)
                .septiembre(BigDecimal.ZERO)
                .octubre(BigDecimal.ZERO)
                .noviembre(BigDecimal.ZERO)
                .diciembre(BigDecimal.ZERO)
                .build();
    }
}
