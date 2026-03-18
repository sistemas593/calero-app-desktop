package com.calero.lili.core.utils.validaciones;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Service
public class ValidarValoresComprobantesPdf {


    public String convertirNumeroALetras(BigDecimal valor) {
        if (valor == null) return "";

        long parteEntera = valor.longValue();
        int parteDecimal = valor.remainder(BigDecimal.ONE)
                .movePointRight(valor.scale())
                .abs()
                .intValue();

        String letras = convertirEnteroALetras(parteEntera);
        return letras + " " + String.format("%02d", parteDecimal) + "/100";
    }

    // Convierte solo la parte entera
    private String convertirEnteroALetras(long numero) {
        if (numero == 0) return "Cero";
        if (numero == 1) return "Uno";
        if (numero == 2) return "Dos";
        if (numero == 3) return "Tres";
        if (numero == 4) return "Cuatro";
        if (numero == 5) return "Cinco";
        if (numero == 6) return "Seis";
        if (numero == 7) return "Siete";
        if (numero == 8) return "Ocho";
        if (numero == 9) return "Nueve";
        if (numero < 20) return new String[]{
                "Diez", "Once", "Doce", "Trece", "Catorce", "Quince",
                "Dieciséis", "Diecisiete", "Dieciocho", "Diecinueve"}[(int) numero - 10];
        if (numero < 30)
            return "Veinti" + convertirEnteroALetras(numero - 20).toLowerCase();
        if (numero < 100) {
            String[] decenas = {"", "", "Veinte", "Treinta", "Cuarenta", "Cincuenta", "Sesenta", "Setenta", "Ochenta", "Noventa"};
            return decenas[(int) numero / 10] + ((numero % 10 != 0) ? " y " + convertirEnteroALetras(numero % 10).toLowerCase() : "");
        }
        if (numero < 200)
            return "Ciento " + convertirEnteroALetras(numero - 100).toLowerCase();
        if (numero < 1000) {
            String[] centenas = {"", "Ciento", "Doscientos", "Trescientos", "Cuatrocientos", "Quinientos",
                    "Seiscientos", "Setecientos", "Ochocientos", "Novecientos"};
            return centenas[(int) numero / 100] + ((numero % 100 != 0) ? " " + convertirEnteroALetras(numero % 100).toLowerCase() : "");
        }
        if (numero < 2000)
            return "Mil " + convertirEnteroALetras(numero % 1000).toLowerCase();
        if (numero < 1_000_000)
            return convertirEnteroALetras(numero / 1000) + " mil" + ((numero % 1000 != 0) ? " " + convertirEnteroALetras(numero % 1000).toLowerCase() : "");
        if (numero < 2_000_000)
            return "Un millón " + convertirEnteroALetras(numero % 1_000_000).toLowerCase();
        if (numero < 1_000_000_000)
            return convertirEnteroALetras(numero / 1_000_000) + " millones" + ((numero % 1_000_000 != 0) ? " " + convertirEnteroALetras(numero % 1_000_000).toLowerCase() : "");
        return String.valueOf(numero); // Por si es más grande
    }


    public String getValor(BigDecimal valor) {

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator(',');  // separador de miles
        simbolos.setDecimalSeparator('.');

        DecimalFormat formato = new DecimalFormat("#,##0.00", simbolos);
        return formato.format(valor);
    }


}
