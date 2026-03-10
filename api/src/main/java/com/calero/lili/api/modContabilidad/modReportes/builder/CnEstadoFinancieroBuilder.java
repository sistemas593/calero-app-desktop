package com.calero.lili.api.modContabilidad.modReportes.builder;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.api.modContabilidad.modReportes.dto.EstadoFinancieroPdfDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CnEstadoFinancieroBuilder {

    private final FormatoValores formatoValores;

    public EstadoFinancieroPdfDto builderValoresPdf(BalanceValoresDto model) {
        return EstadoFinancieroPdfDto.builder()
                .codigoCuenta(model.getCodigoCuenta())
                .cuenta(model.getCuenta())
                .saldoFinal(model.getSaldoFinal().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getSaldoFinal()))
                .totalMayor(model.getTotalMayor())
                .build();
    }


    public EstadoFinancieroPdfDto builderComparativoValoresPdf(BalanceValoresDto model) {
        return EstadoFinancieroPdfDto.builder()
                .codigoCuenta(model.getCodigoCuenta())
                .cuenta(model.getCuenta())
                .totalMayor(model.getTotalMayor())
                .enero(model.getEnero().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getEnero()))
                .febrero(model.getFebrero().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getFebrero()))
                .marzo(model.getMarzo().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getMarzo()))
                .abril(model.getAbril().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getAbril()))
                .mayo(model.getMayo().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getMayo()))
                .junio(model.getJunio().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getJunio()))
                .julio(model.getJulio().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getJulio()))
                .agosto(model.getAgosto().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getAgosto()))
                .septiembre(model.getSeptiembre().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getSeptiembre()))
                .octubre(model.getOctubre().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getOctubre()))
                .noviembre(model.getNoviembre().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getNoviembre()))
                .diciembre(model.getDiciembre().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getDiciembre()))

                .saldoFinal(model.getSaldoFinal().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getSaldoFinal()))
                .saldoInicial(model.getSaldoInicial().compareTo(BigDecimal.ZERO) == 0
                        ? ""
                        : formatoValores.convertirBigDecimalToStringPDF(model.getSaldoInicial()))
                .build();

    }

    public List<EstadoFinancieroPdfDto> builderDefaultListBalanceComprobacion() {
        List<EstadoFinancieroPdfDto> lista = new ArrayList<>();
        EstadoFinancieroPdfDto model = EstadoFinancieroPdfDto.builder()
                .codigoCuenta("0000000")
                .cuenta("0000000")
                .saldoFinal("0.00")
                .build();

        lista.add(model);
        return lista;
    }
}
