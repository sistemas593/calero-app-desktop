package com.calero.lili.core.adConfiguracion.dto;

import com.calero.lili.core.utils.DateUtils;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Data
@ToString
public class FilterMailEnviadosDto {

    private String clave1;
    private String codigoDocumento;
    private String serie;
    private String secuencial;
    private String correo;
    private String fechaInicial;
    private String fechaFinal;


    public LocalDateTime getFechaInicial() {
        if (Objects.nonNull(fechaInicial)) {
            LocalDate fecha = DateUtils.toLocalDate(fechaInicial);
            return fecha.atStartOfDay();
        }
        return null;
    }

    public LocalDateTime getFechaFinal() {
        if (Objects.nonNull(fechaFinal)) {
            LocalDate fecha = DateUtils.toLocalDate(fechaFinal);
            return fecha.atTime(LocalTime.MAX);
        }
        return null;
    }
}
