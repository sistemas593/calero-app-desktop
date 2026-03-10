package com.calero.lili.api.utils.generarClaveAcceso;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerarClaveAcceso {

    public String generarClaveAcceso(String fechaEmision, String tipoComprobante,
                                     String ruc, Integer ambiente, String serie, String numeroComprobante, Integer tipoEmision) {


        String fechaEmisionCorrecta = fechaEmision.substring(0, 2) + fechaEmision.substring(3, 5) + fechaEmision.substring(6, 10);
        String numeroAleatorio = generaCodigoNumerico();

        String javi15 = fechaEmisionCorrecta + tipoComprobante + ruc + ambiente + serie + numeroComprobante + numeroAleatorio + tipoEmision;

        javi15 = javi15 + digver(javi15);
        return javi15;
    }

    String digver(String javi15) {

        int pos = 0;
        int acu = 0;

        for (int w = 1; w <= 8; w++) {

            for (int i = 7; i >= 2; i--) {
                String extraer = javi15.substring(pos, (pos + 1));
                int temporal = (Integer.parseInt(extraer));
                int multiplica = temporal * i;
                acu += multiplica;
                pos = pos + 1;
            }

        }
        String javi19 = "" + (11 - (acu % 11));

        switch (javi19) {
            case "11":
                javi19 = "0";
                break;
            case "10":
                javi19 = "1";
                break;
        }
        return javi19;
    }

    public String generaCodigoNumerico() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            codigo.append(random.nextInt(10));
        }
        return codigo.toString();
    }

}
