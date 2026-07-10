package com.calero.lili.core.adConfiguracion.builder;

import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosResponseDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AdMailEnviadosBuilder {


    public AdMailEnviadosResponseDto builderResponse(AdMailEnviadosEntity model) {
        return AdMailEnviadosResponseDto.builder()
                .id(model.getId())
                .clave1(model.getClave1())
                .codigoDocumento(model.getCodigoDocumento())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .mailTo(censurarCorreos(model.getMailTo()))
                .fecha(Objects.nonNull(model.getFecha()) ? DateUtils.obtenerFechaHoraSegundos(model.getFecha()) : null)
                .total(model.getTotal())
                .build();
    }


    private static String censurarCorreos(String correos) {
        String[] lista = correos.split(",");

        for (int i = 0; i < lista.length; i++) {
            lista[i] = censurarCorreo(lista[i].trim());
        }

        return String.join(",", lista);
    }

    private static String censurarCorreo(String correo) {
        int arroba = correo.indexOf("@");

        if (arroba == -1) {
            return correo;
        }

        if (correo.contains("****")) {
            return correo;
        }

        if (arroba <= 2) {
            return correo.substring(0, arroba) + "****" + correo.substring(arroba);
        }

        return correo.substring(0, 2) + "****" + correo.substring(arroba);
    }


}
