package com.calero.lili.core.utils;

import com.calero.lili.core.errors.exceptions.GeneralException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime toLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatterTime);
    }

    public static String toString(LocalDate date) {
        return date.format(formatter);
    }

    public static String toString(LocalDateTime date) {
        return date.format(formatterTime);
    }

    public static LocalDate toLocalDateOffsetTime(String dateOffsetTime) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateOffsetTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return offsetDateTime.toLocalDate();

    }

    public static String obtenerFormatoFechaLetras(LocalDate fechaAsiento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fecha = fechaAsiento.format(formatter);
        return fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
    }

    public static String obtenerFechaHora(LocalDateTime fechaActual) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        return fechaActual.format(formato);
    }

    public static String obtenerMesLetras(LocalDate fecha) {
        return fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    public static boolean validacionFechaEnero(LocalDate fechaEmisionDesde) {
        return fechaEmisionDesde.getMonthValue() == 1 && fechaEmisionDesde.getDayOfMonth() == 1;
    }

    public static LocalDate getFechaInicio(LocalDate fechaEmisionDesde) {
        return LocalDate.of(fechaEmisionDesde.getYear(), 1, 1);
    }

    public static LocalDate getFechaFin(LocalDate fechaEmisionHasta) {
        return LocalDate.of(fechaEmisionHasta.getYear(), 12, 31);
    }

    public static void validacionFechas(LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta) {
        if (fechaEmisionDesde.getYear() != fechaEmisionDesde.getYear()) {
            throw new GeneralException("Las fechas no pueden ser de diferentes años");
        }

        if (fechaEmisionDesde.isAfter(fechaEmisionHasta)) {
            throw new GeneralException("La fecha inicial no puede ser menor que la fecha final");
        }


        if (fechaEmisionHasta.isBefore(fechaEmisionDesde)) {
            throw new GeneralException("La fecha final no puede ser menor que la fecha inicial");
        }
    }

    public static void validarFechaEmision(String fechaEmision) {

        if (Objects.isNull(fechaEmision) || fechaEmision.isEmpty()) {
            throw new GeneralException("La fecha de emisión del documento es obligatoria");
        }
        LocalDate fecha = DateUtils.toLocalDate(fechaEmision);

        if (!fecha.isEqual(LocalDate.now())) {
            throw new GeneralException(MessageFormat.format("La fecha de emisión: {0}," +
                    " no coincide con la fecha actual", fechaEmision));
        }
    }

    public static void validarFechaEmisionGuia(String fechaEmision, String fechaInicioTransporte) {

        if (Objects.isNull(fechaEmision) || fechaEmision.isEmpty()) {
            throw new GeneralException("La fecha de emisión del documento es obligatoria");
        }
        LocalDate fecha = DateUtils.toLocalDate(fechaEmision);
        LocalDate fechaInicio = DateUtils.toLocalDate(fechaInicioTransporte);

        if (!fecha.isEqual(LocalDate.now())) {
            throw new GeneralException(MessageFormat.format("La fecha de emisión: {0}," +
                    " no coincide con la fecha actual", fechaEmision));
        }

        if (!fechaInicio.isEqual(fecha)) {
            throw new GeneralException(MessageFormat.format("La fecha de emisión: {0}," +
                    " no coincide con la fecha de inicio de transporte {1} ", fechaEmision, fechaInicioTransporte));
        }
    }


}
