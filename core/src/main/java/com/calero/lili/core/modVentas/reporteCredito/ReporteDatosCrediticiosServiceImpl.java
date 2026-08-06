package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoPersoneria;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modVentas.reporteCredito.builder.DatosCrediticiosBuilder;
import com.calero.lili.core.modVentas.reporteCredito.dto.DatosCrediticiosResponseDto;
import com.calero.lili.core.modVentas.reporteCredito.projection.DatosCrediticiosProjection;
import com.calero.lili.core.modVentas.reporteCredito.projection.PeriodoProjection;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.DatosCrediticiosValorBusquedaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosServiceImpl {


    private final DatosCrediticiosRepository datosCrediticiosRepository;

    private final FormatoValores formatoValores;
    private final DatosCrediticiosValorBusquedaService valorBusquedaService;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final DatosCrediticiosBuilder datosCrediticiosBuilder;


    public byte[] generarReporteDatosCrediticios(Long idData, AdEmpresaEntity empresa, PeriodoProjection entidad,
                                                 UUID idDatosCrediticios, LocalDate fechaPeriodo) throws IOException {


        List<DetalleError> detalleErrores = new ArrayList<>();


        List<DatosCrediticiosProjection> lista = datosCrediticiosRepository.obtenerDatosCrediticios(idData, empresa.getIdEmpresa(),
                valorBusquedaService.obtenerValorAnual(entidad.getPeriodo()), idDatosCrediticios);


        if (Objects.isNull(empresa.getCodigoDinardap()) || empresa.getCodigoDinardap().isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("No existe codigo de dinardap para generar el reporte en la empresa: " + empresa.getIdEmpresa());
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);
        }

        if (lista.isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("No existe información para generar el reporte");
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);
        }

        StringBuilder sb = new StringBuilder();

        for (DatosCrediticiosProjection cabecera : lista) {
            sb.append(construirLinea(cabecera, empresa, detalleErrores, fechaPeriodo)).append("\r\n");
        }

        if (!detalleErrores.isEmpty()) {

            List<String> list = detalleErrores.stream()
                    .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                    .toList();
            throw new ListErrorException(list);
        }


        Charset charset = Charset.forName("windows-1252");
        byte[] bytes = sb.toString().getBytes(charset);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            String periodo = DateUtils.toStringPeriodoFiscal(fechaPeriodo);

            String nombreArchivo = empresa.getRuc() + periodo + ".txt";

            ZipEntry entry = new ZipEntry(nombreArchivo);
            zos.putNextEntry(entry);
            zos.write(bytes);

            zos.closeEntry();
        }

        return baos.toByteArray();
    }


    public void delete(Long idData, Long idEmpresa, UUID idDatosCrediticios) {
        DatosCrediticiosEntity entidad = datosCrediticiosRepository.findById(idData, idEmpresa, idDatosCrediticios)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("La cabecera de los datos" +
                        " crediticios con id {0}, no existe", idDatosCrediticios)));
        datosCrediticiosRepository.delete(entidad);

    }


    public PaginatedDto<DatosCrediticiosResponseDto> getAll(Long idData, Long idEmpresa, Pageable pageable) {


        Page<DatosCrediticiosEntity> page = datosCrediticiosRepository.findAllPaginate(idData, idEmpresa, pageable);

        List<DatosCrediticiosResponseDto> dtoList = page.stream().map(datosCrediticiosBuilder::builderResponseList).toList();


        PaginatedDto paginatedDto = new PaginatedDto();
        paginatedDto.setContent(dtoList);

        Paginator paginated = new Paginator();
        paginated.setTotalElements(page.getTotalElements());
        paginated.setTotalPages(page.getTotalPages());
        paginated.setNumberOfElements(page.getNumberOfElements());
        paginated.setSize(page.getSize());
        paginated.setFirst(page.isFirst());
        paginated.setLast(page.isLast());
        paginated.setPageNumber(page.getPageable().getPageNumber());
        paginated.setPageSize(page.getPageable().getPageSize());
        paginated.setEmpty(page.isEmpty());
        paginated.setNumber(page.getNumber());

        paginatedDto.setPaginator(paginated);

        return paginatedDto;
    }


    private String construirLinea(DatosCrediticiosProjection f,
                                  AdEmpresaEntity empresa, List<DetalleError> detalleErrores, LocalDate fechaPeriodo) {


        long dias = Math.abs(ChronoUnit.DAYS.between(f.getFechaVencimiento(), f.getFechaConcesion()));

        if (dias != f.getPlazoOperacion()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("Fecha Vencimiento: " + f.getFechaVencimiento() + " y Fecha Concesion: " + f.getFechaConcesion() + " diferencia de días: "
                    + dias + " plazo operacion "
                    + f.getPlazoOperacion() + " para la operación: " + f.getNumeroOperacion());
            detalleErrores.add(detalleError);
        }

        String parroquia = "";
        String canton = "";
        String provincia = "";

        String sexo = "";
        String estadoCivil = "";
        String origenIngreso = "";


        if (Objects.nonNull(f.getCodigoParroquia())) {

            String x = f.getCodigoParroquia();
            parroquia = x.substring(x.length() - 2);
            if (Objects.nonNull(f.getCodigoCanton())) {
                String z = f.getCodigoCanton();
                canton = z.substring(z.length() - 2);
                if (Objects.nonNull(f.getCodigoProvincia())) {
                    provincia = f.getCodigoProvincia();
                }
            }

        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El tercero con identificación "
                    + f.getIdentificacionSujeto() + " no tiene asignada una parroquia, lo cual es obligatorio para generar el reporte.");
            detalleErrores.add(detalleError);
        }


        if (Objects.nonNull(f.getClaseSujeto())) {

            if (f.getClaseSujeto().equals(TipoPersoneria.N.name())) {

                if (Objects.nonNull(f.getSexo())
                        && Objects.nonNull(f.getEstadoCivil())
                        && Objects.nonNull(f.getOrigenIngresos())) {

                    sexo = sexo.replace("", f.getSexo());
                    estadoCivil = estadoCivil.replace("", f.getEstadoCivil());
                    origenIngreso = origenIngreso.replace("", f.getOrigenIngresos());

                } else {

                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("El tercero con identificación "
                            + f.getIdentificacionSujeto() + " es un cliente natural, por lo tanto debe tener asignados los campos sexo, estado civil y origen de ingresos para generar el reporte.");
                    detalleErrores.add(detalleError);
                }
            }
        } else {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("El tercero con identificación "
                    + f.getIdentificacionSujeto() + " no tiene asignado un tipo de personería, lo cual es obligatorio para generar el reporte.");
            detalleErrores.add(detalleError);
        }

        LocalDate fechaDatos = DateUtils.toPeriodoDateDinarap(f.getPeriodo());

        validarPersoneria(f.getIdentificacionSujeto(), f.getTipoIdentificacion(), f.getClaseSujeto(), detalleErrores);

        String tipoSujeto = Objects.nonNull(f.getClaseSujeto()) ? f.getClaseSujeto() : "";
        String tipoIdentificacion = Objects.nonNull(f.getTipoIdentificacion()) ? f.getTipoIdentificacion() : "";
        String numeroIdentificacion = f.getIdentificacionSujeto();


        if (tipoIdentificacion.equals("R") && tipoSujeto.equals("N")) {
            tipoIdentificacion = "C";
            numeroIdentificacion = numeroIdentificacion.substring(0, 10);
        }

        // TODO VALIDAR TAMBIEN POR EL DIA DE MOROSIDAD, SI ES NEGATIVO VALIDAR QUE getFechaConcesion SEA MENOR A
        // FECHA DE VENCIMIENTO Y FECHA EXIGIBLE.


        if (f.getFechaConcesion().isAfter(f.getFechaVencimiento())) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de concesion: " + f.getFechaConcesion() +
                    " no puede ser menor a la fecha de vencimiento: " + f.getFechaVencimiento());
            detalleErrores.add(detalleError);
        }

        if (f.getFechaConcesion().isAfter(f.getFechaExigible())) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("La fecha de concesion: " + f.getFechaConcesion() +
                    " no puede ser menor a la fecha de exigible: " + f.getFechaExigible());
            detalleErrores.add(detalleError);
        }


        long diferenciaDias = ChronoUnit.DAYS.between(f.getFechaVencimiento(), fechaPeriodo);


        diferenciaDias = Math.abs(diferenciaDias);
        int diasMorosidad = Math.abs(f.getDiasMorosidad());

        if (diferenciaDias != diasMorosidad) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle(f.getNumeroOperacion() + " los días de morosidad " +
                    " no son iguales a la diferencia entre la fecha de vencimiento y el periodo. "
                    + " Diferencia de días:" + diferenciaDias + " Días de morosidad:" + f.getDiasMorosidad()
                    + " Fecha de vencimiento:" + f.getFechaVencimiento() + " Periodo:" + fechaPeriodo);
            detalleErrores.add(detalleError);
        }

        return String.join("|",
                Objects.nonNull(empresa.getCodigoDinardap()) ? empresa.getCodigoDinardap() : "",
                DateUtils.toString(fechaDatos),
                tipoIdentificacion,
                numeroIdentificacion,
                formatearTextoNombreSujeto(f.getNombreSujeto()),
                f.getClaseSujeto(),
                provincia,
                canton,
                parroquia,
                sexo,
                estadoCivil,
                origenIngreso,
                f.getNumeroOperacion(),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorOperacion()) ? f.getValorOperacion() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getSaldoOperacion()) ? f.getSaldoOperacion() : BigDecimal.ZERO),
                Objects.nonNull(f.getFechaConcesion()) ? DateUtils.toString(f.getFechaConcesion()) : "",
                Objects.nonNull(f.getFechaVencimiento()) ? DateUtils.toString(f.getFechaVencimiento()) : "",
                Objects.nonNull(f.getFechaExigible()) ? DateUtils.toString(f.getFechaExigible()) : "",
                Objects.nonNull(f.getPlazoOperacion()) ? f.getPlazoOperacion().toString() : "",
                Objects.nonNull(f.getPeriosidadPago()) ? f.getPeriosidadPago() : "",
                Objects.nonNull(f.getDiasMorosidad()) ? retornarDiasCorrectos(f.getDiasMorosidad()) : "",
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getMontoMorisidad()) ? f.getMontoMorisidad() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getMontoInteresMora()) ? f.getMontoInteresMora() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorPorVencer1a30Dias()) ? f.getValorPorVencer1a30Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorPorVencer31a90Dias()) ? f.getValorPorVencer31a90Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorPorVencer91a180Dias()) ? f.getValorPorVencer91a180Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorPorVencer181a360Dias()) ? f.getValorPorVencer181a360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorPorVencerMas360Dias()) ? f.getValorPorVencerMas360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido1a30Dias()) ? f.getValorVencido1a30Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido31a90Dias()) ? f.getValorVencido31a90Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido91a180Dias()) ? f.getValorVencido91a180Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido181a360Dias()) ? f.getValorVencido181a360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencidoMas360Dias()) ? f.getValorVencidoMas360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorDemandaJudicial()) ? f.getValorDemandaJudicial() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getCarteraCastigada()) ? f.getCarteraCastigada() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getCuotaCredito()) ? f.getCuotaCredito() : BigDecimal.ZERO),
                Objects.nonNull(f.getFechaCancelacion()) ? DateUtils.toString(f.getFechaCancelacion()) : "",
                Objects.nonNull(f.getFormaCancelacion()) ? f.getFormaCancelacion() : ""); // TODO Tipos Efectivo (E), Cheque(C), Tarjeta de Crédito (T)
    }


    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
    }


    private String formatearTextoNombreSujeto(String nombreSujeto) {
        if (Objects.nonNull(nombreSujeto)) {

            nombreSujeto = nombreSujeto.strip();

            /*nombreSujeto = nombreSujeto.toUpperCase()
                    .replace("Ñ", "N");*/

            nombreSujeto = nombreSujeto.length() <= 100 ? nombreSujeto : nombreSujeto.substring(0, 100);

            /*return Normalizer.normalize(nombreSujeto, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");*/

            return nombreSujeto;
        }

        return "";
    }

    private String retornarDiasCorrectos(Integer diasMorosidad) {
        if (diasMorosidad >= 0) {
            return diasMorosidad.toString();
        } else {
            return "0";
        }
    }

    private void validarPersoneria(String numeroIdentifiacion, String tipoIdentificacion,
                                   String tipoPersoneria, List<DetalleError> detalleErrores) {

        if (tipoIdentificacion.equals("R")) {
            int validador = Integer.parseInt(numeroIdentifiacion.substring(2, 3));
            if (validador <= 5) {
                if (tipoPersoneria.equals("J")) {
                    DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
                    detalleError.setDetalle("El tercero con identificación "
                            + numeroIdentifiacion + " corresponde a un RUC de persona natural, por lo que el tipo de personería debe ser Natural (N) y no Jurídica (J).");
                    detalleErrores.add(detalleError);
                }
            }
        }

        if (tipoIdentificacion.equals("C")) {
            if (!tipoPersoneria.equals("N")) {
                DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
                detalleError.setDetalle("El tercero con identificación "
                        + numeroIdentifiacion
                        + " corresponde a una Cédula, por lo que el tipo de personería debe ser Natural (N)");
                detalleErrores.add(detalleError);
            }
        }
    }


}
