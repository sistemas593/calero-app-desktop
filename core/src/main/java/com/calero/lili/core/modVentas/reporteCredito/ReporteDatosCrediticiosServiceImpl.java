package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.enums.TipoPersoneria;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
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
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosServiceImpl {


    private final DatosCrediticiosRepository datosCrediticiosRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final FormatoValores formatoValores;
    private final DatosCrediticiosValorBusquedaService valorBusquedaService;

    public byte[] generarTxt(Long idData, Long idEmpresa, FilterDatosCrediticiosDto filter) {

        List<DatosCrediticiosProjection> lista = datosCrediticiosRepository.obtenerDatosCrediticios(idData, idEmpresa,
                valorBusquedaService.obtenerValorAnual(filter.getPeriodo()), filter.getPeriodo());

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("No se encontró la empresa con idData: " + idData + " e idEmpresa: " + idEmpresa));

        if (lista.isEmpty()) {
            throw new GeneralException("No existe información para generar el reporte");
        }

        StringBuilder sb = new StringBuilder();

        for (DatosCrediticiosProjection cabecera : lista) {
            sb.append(construirLinea(cabecera, empresa)).append("\n");

        }


        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String construirLinea(DatosCrediticiosProjection f, AdEmpresaEntity empresa) {

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
            throw new GeneralException("El tercero con identificación "
                    + f.getIdentificacionSujeto() + " no tiene asignada una parroquia, lo cual es obligatorio para generar el reporte.");
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
                    throw new GeneralException("El tercero con identificación "
                            + f.getIdentificacionSujeto() + " es un cliente natural, por lo tanto debe tener asignados los campos sexo, estado civil y origen de ingresos para generar el reporte.");
                }
            }
        }

        LocalDate fechaDatos = DateUtils.toPeriodoDate(f.getPeriodo());


        return String.join("|",
                Objects.nonNull(empresa.getCodigoDinardap()) ? empresa.getCodigoDinardap() : "",
                DateUtils.toString(fechaDatos),
                f.getTipoIdentificacion(),
                f.getIdentificacionSujeto(),
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

    public void delete(Long idData, Long idEmpresa, String periodo) {
        DatosCrediticiosEntity entidad = datosCrediticiosRepository.findByPeriodo(idData, idEmpresa, periodo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("El periodo {0} no existe", periodo)));
        datosCrediticiosRepository.delete(entidad);

    }

}
