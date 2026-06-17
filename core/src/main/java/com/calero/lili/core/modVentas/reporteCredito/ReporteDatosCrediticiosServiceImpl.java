package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.builder.DetalleErrorBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.dtos.errors.DetalleError;
import com.calero.lili.core.dtos.errors.EnumError;
import com.calero.lili.core.enums.TipoPersoneria;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.errors.exceptions.ListErrorException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modVentas.reporteCredito.builder.DatosCrediticiosBuilder;
import com.calero.lili.core.modVentas.reporteCredito.dto.DatosCrediticiosResponseDto;
import com.calero.lili.core.modVentas.reporteCredito.dto.FilterDatosCrediticiosDto;
import com.calero.lili.core.modVentas.reporteCredito.projection.DatosCrediticiosProjection;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.DatosCrediticiosValorBusquedaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosServiceImpl {


    private final DatosCrediticiosRepository datosCrediticiosRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final FormatoValores formatoValores;
    private final DatosCrediticiosValorBusquedaService valorBusquedaService;
    private final DetalleErrorBuilder detalleErrorBuilder;
    private final DatosCrediticiosBuilder datosCrediticiosBuilder;


    public byte[] generarTxt(Long idData, Long idEmpresa, FilterDatosCrediticiosDto filter) {


        List<DetalleError> detalleErrores = new ArrayList<>();
        List<DatosCrediticiosProjection> lista = datosCrediticiosRepository.obtenerDatosCrediticios(idData, idEmpresa,
                valorBusquedaService.obtenerValorAnual(filter.getPeriodo()), DateUtils.getPeriodo(filter.getPeriodo()));

        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData, idEmpresa);

        if (empresa.isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("No se encontró la empresa con idData: " + idData + " e idEmpresa: " + idEmpresa);
            detalleErrores.add(detalleError);
            throwErrors(detalleErrores);
        }

        if (Objects.isNull(empresa.get().getCodigoDinardap()) || empresa.get().getCodigoDinardap().isEmpty()) {
            DetalleError detalleError = detalleErrorBuilder.builderDetalleError(0, EnumError.DOCUMENTO_ERROR);
            detalleError.setDetalle("No existe codigo de dinardap para generar el reporte en la empresa: " + idEmpresa);
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
            sb.append(construirLinea(cabecera, empresa.get(), detalleErrores)).append("\n");

        }

        if (detalleErrores.isEmpty()) {
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            List<String> list = detalleErrores.stream()
                    .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                    .toList();
            throw new ListErrorException(list);
        }

    }

    private String construirLinea(DatosCrediticiosProjection f, AdEmpresaEntity empresa, List<DetalleError> detalleErrores) {

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
        }

        LocalDate fechaDatos = DateUtils.toPeriodoDateDinarap(f.getPeriodo());

        return String.join("|",
                Objects.nonNull(empresa.getCodigoDinardap()) ? empresa.getCodigoDinardap() : "",
                DateUtils.toString(fechaDatos),
                f.getTipoIdentificacion(),
                f.getIdentificacionSujeto(),
                formatearTextoNombreSujeto(f.getNombreSujeto()),
                validarPersoneria(f.getIdentificacionSujeto(), f.getTipoIdentificacion(), f.getClaseSujeto()),
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


    public void delete(Long idData, Long idEmpresa, String periodo) {
        DatosCrediticiosEntity entidad = datosCrediticiosRepository.findByPeriodo(idData, idEmpresa, periodo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El periodo {0} no existe", periodo)));
        datosCrediticiosRepository.delete(entidad);

    }


    public List<DatosCrediticiosResponseDto> getAll(Long idData, Long idEmpresa) {
        return datosCrediticiosRepository.findAllByIdDataAndIdEmpresa(idData, idEmpresa)
                .stream()
                .map(datosCrediticiosBuilder::builderResponseList)
                .toList();
    }

    private static void throwErrors(List<DetalleError> detalleErrores) {
        List<String> list = detalleErrores.stream()
                .map(detalleError -> detalleError.getLinea() + "   " + detalleError.getType().getDescription() + " " + detalleError.getDetalle())
                .toList();
        throw new ListErrorException(list);
    }


    private String formatearTextoNombreSujeto(String nombreSujeto) {
        if (Objects.nonNull(nombreSujeto)) {

            nombreSujeto = nombreSujeto.toUpperCase()
                    .replace("Ñ", "N");

            return Normalizer.normalize(nombreSujeto, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
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

    private String validarPersoneria(String numeroIdentifiacion, String tipoIdentificacion, String tipoPersoneria) {

        if (tipoIdentificacion.equals("R")) {
            int validador = Integer.parseInt(numeroIdentifiacion.substring(2, 3));
            if (validador <= 5) {
                return "N";
            }
        }

        if (tipoIdentificacion.equals("C")) {
            if (tipoPersoneria.equals("N")) {
                return tipoPersoneria;
            } else {
                return "N";
            }
        }

        return tipoPersoneria;
    }


}
