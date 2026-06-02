package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosServiceImpl {

    private final ReporteDatosCrediticiosRepository reporteDatosCrediticiosRepository;
    private final FormatoValores formatoValores;

    public byte[] generarTxt(Long idData, Long idEmpresa) {

        List<DatosCrediticiosDetalleEntity> lista = reporteDatosCrediticiosRepository.getFindAll(idData, idEmpresa);

        if (lista.isEmpty()) {
            throw new GeneralException("No existe información para generar el reporte");
        }

        StringBuilder sb = new StringBuilder();

        for (DatosCrediticiosDetalleEntity f : lista) {
            sb.append(construirLinea(f)).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String construirLinea(DatosCrediticiosDetalleEntity f) {


        // TODO REVISAR ESTO TODAVIA EL REPORTE LA VALIDACION PARA ENVIAR LOS PARAMETROS CORRECTOS
        String parroquia = "|";
        String canton = "|";
        String provincia = "|";
        if (f.getTercero().getDatosAdicionales()) {


            if (Objects.nonNull(f.getTercero().getParroquia())) {
                parroquia = parroquia.replace("|", f.getTercero().getParroquia().getCodigoParroquia());

                if (Objects.nonNull(f.getTercero().getParroquia().getCanton())) {
                    canton = canton.replace("|", f.getTercero().getParroquia().getCanton().getCodigoCanton());

                    if (Objects.nonNull(f.getTercero().getParroquia().getCanton().getProvincia())) {
                        provincia = provincia.replace("|", f.getTercero().getParroquia().getCanton().getProvincia().getCodigoProvincia());
                    }
                }

            }
        }

        return String.join("|",
                f.getNumeroOperacion(),
                Objects.nonNull(f.getFechaConcesion()) ? DateUtils.toString(f.getFechaConcesion()) : "|",
                f.getTercero().getTipoIdentificacion(),
                f.getTercero().getNumeroIdentificacion(),
                f.getTercero().getTercero(),
                f.getTercero().getTipoClienteProveedor().name(),
                provincia,
                canton,
                parroquia,
                Objects.nonNull(f.getTercero().getSexo()) ? f.getTercero().getSexo().name() : "|",
                Objects.nonNull(f.getTercero().getEstadoCivil()) ? f.getTercero().getEstadoCivil().name() : "|",
                Objects.nonNull(f.getTercero().getOrigenIngresos()) ? f.getTercero().getOrigenIngresos().name() : "|",
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorOperacion()) ? f.getValorOperacion() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getSaldoOperacion()) ? f.getSaldoOperacion() : BigDecimal.ZERO),
                Objects.nonNull(f.getFechaConcesion()) ? DateUtils.toString(f.getFechaConcesion()) : "|",
                Objects.nonNull(f.getFechaVencimiento()) ? DateUtils.toString(f.getFechaVencimiento()) : "|",
                Objects.nonNull(f.getFechaExigible()) ? DateUtils.toString(f.getFechaExigible()) : "|",
                Objects.nonNull(f.getPlazoOperacion()) ? f.getPlazoOperacion().toString() : "|",
                Objects.nonNull(f.getPeriodicidadPago()) ? f.getPeriodicidadPago().toString() : "|",
                Objects.nonNull(f.getDiasMorosidad()) ? f.getDiasMorosidad().toString() : "|",
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getMontoMorosidad()) ? f.getMontoMorosidad() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getMontoInteresMora()) ? f.getMontoInteresMora() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorXVencer1a30Dias()) ? f.getValorXVencer1a30Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorXVencer31a90Dias()) ? f.getValorXVencer31a90Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorXVencer91a180Dias()) ? f.getValorXVencer91a180Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorXVencer181a360Dias()) ? f.getValorXVencer181a360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorXVencerMas360Dias()) ? f.getValorXVencerMas360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido1a30Dias()) ? f.getValorVencido1a30Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido31a90Dias()) ? f.getValorVencido31a90Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido91a180Dias()) ? f.getValorVencido91a180Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencido181a360Dias()) ? f.getValorVencido181a360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorVencidoMas360Dias()) ? f.getValorVencidoMas360Dias() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getValorDemandaJudicial()) ? f.getValorDemandaJudicial() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getCarteraCastigada()) ? f.getCarteraCastigada() : BigDecimal.ZERO),
                formatoValores.convertirBigDecimalToString(Objects.nonNull(f.getCoutaCredito()) ? f.getCoutaCredito() : BigDecimal.ZERO),
                Objects.nonNull(f.getFechaCancelacion()) ? DateUtils.toString(f.getFechaCancelacion()) : "|",
                f.getFormaCancelacion());
    }

}
