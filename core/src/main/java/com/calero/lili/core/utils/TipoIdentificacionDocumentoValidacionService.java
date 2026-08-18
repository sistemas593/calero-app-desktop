package com.calero.lili.core.utils;


import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TipoIdentificacionDocumentoValidacionService {

    public boolean validacionIdentDoc(String tipoIdentificacion, String codDocumento) {
        return combinaciones.contains(tipoIdentificacion + "-" + codDocumento);
    }

    private final Set<String> combinaciones = Set.of(
            "01-01",
            "01-02",
            "02-03",
            "03-03",
            "01-04",
            "02-04",
            "03-04",
            "01-05",
            "02-05",
            "03-05",
            "01-08",
            "01-09",
            "01-11",
            "01-12",
            "03-15",
            "01-19",
            "01-20",
            "01-21",
            "01-41",
            "01-42",
            "01-43",
            "01-47",
            "01-48",
            "01-45",
            "02-19",
            "02-41",
            "03-19",
            "03-41",
            "02-09",
            "03-09",
            "03-45",
            "02-294",
            "03-294",
            "02-344",
            "03-344",
            "01-364",
            "02-364",
            "03-364",
            "01-03",
            "01-374");
}
