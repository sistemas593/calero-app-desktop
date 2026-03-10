package com.calero.lili.core.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ComprobanteSustentoService {


    private final Set<String> combinaciones = new HashSet<>();


    public boolean validacionCodigos(String codigoComprobante, String codigoSustento){
        return combinaciones.contains(codigoComprobante + "-" + codigoSustento);
    }

    @PostConstruct
    public void init() {
        combinaciones.add("01-00");
        combinaciones.add("01-01");
        combinaciones.add("01-02");
        combinaciones.add("01-03");
        combinaciones.add("01-04");
        combinaciones.add("01-05");
        combinaciones.add("01-06");
        combinaciones.add("01-07");
        combinaciones.add("01-08");
        combinaciones.add("01-09");
        combinaciones.add("02-00");
        combinaciones.add("02-02");
        combinaciones.add("02-04");
        combinaciones.add("02-05");
        combinaciones.add("02-07");
        combinaciones.add("02-08");
        combinaciones.add("03-01");
        combinaciones.add("03-02");
        combinaciones.add("03-03");
        combinaciones.add("03-04");
        combinaciones.add("03-05");
        combinaciones.add("03-06");
        combinaciones.add("03-07");
        combinaciones.add("03-08");
        combinaciones.add("04-00");
        combinaciones.add("04-01");
        combinaciones.add("04-02");
        combinaciones.add("04-03");
        combinaciones.add("04-04");
        combinaciones.add("04-05");
        combinaciones.add("04-06");
        combinaciones.add("04-07");
        combinaciones.add("04-08");
        combinaciones.add("04-09");
        combinaciones.add("05-00");
        combinaciones.add("05-01");
        combinaciones.add("05-02");
        combinaciones.add("05-03");
        combinaciones.add("05-04");
        combinaciones.add("05-05");
        combinaciones.add("05-06");
        combinaciones.add("05-07");
        combinaciones.add("05-08");
        combinaciones.add("05-09");
        combinaciones.add("11-01");
        combinaciones.add("11-02");
        combinaciones.add("11-05");
        combinaciones.add("12-01");
        combinaciones.add("12-02");
        combinaciones.add("12-11");
        combinaciones.add("15-02");
        combinaciones.add("15-04");
        combinaciones.add("15-05");
        combinaciones.add("15-07");
        combinaciones.add("19-10");
        combinaciones.add("19-02");
        combinaciones.add("20-02");
        combinaciones.add("21-01");
        combinaciones.add("21-02");
        combinaciones.add("21-08");
        combinaciones.add("41-01");
        combinaciones.add("41-02");
        combinaciones.add("41-03");
        combinaciones.add("41-04");
        combinaciones.add("41-05");
        combinaciones.add("41-06");
        combinaciones.add("41-07");
        combinaciones.add("42-00");
        combinaciones.add("43-01");
        combinaciones.add("43-02");
        combinaciones.add("43-06");
        combinaciones.add("43-07");
        combinaciones.add("45-09");
        combinaciones.add("47-01");
        combinaciones.add("47-02");
        combinaciones.add("47-03");
        combinaciones.add("47-04");
        combinaciones.add("47-06");
        combinaciones.add("47-07");
        combinaciones.add("48-01");
        combinaciones.add("48-02");
        combinaciones.add("48-03");
        combinaciones.add("48-04");
        combinaciones.add("48-06");
        combinaciones.add("48-07");
        combinaciones.add("17-01");
        combinaciones.add("17-02");
        combinaciones.add("17-03");
        combinaciones.add("17-04");
        combinaciones.add("17-05");
        combinaciones.add("17-06");
        combinaciones.add("17-07");
        combinaciones.add("17-08");
        combinaciones.add("09-02");
        combinaciones.add("19-13");
        combinaciones.add("42-12");
        combinaciones.add("294-01");
        combinaciones.add("294-02");
        combinaciones.add("294-03");
        combinaciones.add("294-04");
        combinaciones.add("294-05");
        combinaciones.add("294-06");
        combinaciones.add("294-07");
        combinaciones.add("294-08");
        combinaciones.add("344-01");
        combinaciones.add("344-02");
        combinaciones.add("344-03");
        combinaciones.add("344-04");
        combinaciones.add("344-05");
        combinaciones.add("344-06");
        combinaciones.add("344-07");
        combinaciones.add("344-08");
        combinaciones.add("364-02");
        combinaciones.add("364-07");
        combinaciones.add("01-14");
        combinaciones.add("02-14");
        combinaciones.add("03-14");
        combinaciones.add("04-14");
        combinaciones.add("05-14");
    }

}
