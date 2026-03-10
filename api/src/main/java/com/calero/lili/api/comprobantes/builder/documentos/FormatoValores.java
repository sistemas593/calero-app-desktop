package com.calero.lili.api.comprobantes.builder.documentos;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Component
@AllArgsConstructor
public class FormatoValores {

    public String convertirBigDecimalToString(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(valor.abs());
        return formatted.replace(",", ".");
    }

    public String convertirTarifaToString(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("0");
        String formatted = df.format(valor.abs());
        return formatted.replace(",", ".");
    }

    public String convertirBigDecimalToStringPDF(BigDecimal valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(valor);
    }

}
