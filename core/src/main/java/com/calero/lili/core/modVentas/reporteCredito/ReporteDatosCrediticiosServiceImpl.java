package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReporteDatosCrediticiosServiceImpl {

    private final ReporteDatosCrediticiosRepository reporteDatosCrediticiosRepository;
    private final FormatoValores formatoValores;

    public byte[] generarTxt(Long idData, Long idEmpresa) {

        List<ReporteDatosCrediticiosEntity> lista = reporteDatosCrediticiosRepository.getFindAll(idData, idEmpresa);

        if (lista.isEmpty()) {
            throw new GeneralException("No existe información para generar el reporte");
        }

        StringBuilder sb = new StringBuilder();

        for (ReporteDatosCrediticiosEntity f : lista) {
            sb.append(construirLinea(f)).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String construirLinea(ReporteDatosCrediticiosEntity f) {


        // TODO REVISAR ESTO TODAVIA EL REPORTE LA VALIDACION PARA ENVIAR LOS PARAMETROS CORRECTOS
        String parroquia = "|";
        String canton = "|";
        String provincia = "|";
        String sexo = "|";
        String estadoCivil = "|";
        String origenIngresos = "|";

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
                DateUtils.toString(f.getFechaConcesion()),
                f.getTercero().getTipoIdentificacion(),
                f.getTercero().getNumeroIdentificacion(),
                f.getTercero().getTercero(),
                f.getTercero().getTipoClienteProveedor().name(),
                provincia,
                canton,
                parroquia,
                f.getTercero().getSexo().name(),
                f.getTercero().getEstadoCivil().name(),
                f.getTercero().getOrigenIngresos().name(),
                formatoValores.convertirBigDecimalToString(f.getValorOperacion()),
                formatoValores.convertirBigDecimalToString(f.getSaldoOperacion()),
                DateUtils.toString(f.getFechaConcesion()),
                DateUtils.toString(f.getFechaVencimiento()),
                DateUtils.toString(f.getFechaExigible()),
                f.getPlazoOperacion().toString(),
                f.getPeriodicidadPago().toString(),
                f.getDiasMorosidad().toString(),
                formatoValores.convertirBigDecimalToString(f.getMontoMorosidad()),
                formatoValores.convertirBigDecimalToString(f.getMontoInteresMora()),
                formatoValores.convertirBigDecimalToString(f.getValorXVencer1a30Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorXVencer31a90Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencido91a180Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorXVencer181a360Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorXVencerMas360Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencido1a30Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencido31a90Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencido91a180Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencido181a360Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorVencidoMas360Dias()),
                formatoValores.convertirBigDecimalToString(f.getValorDemandaJudicial()),
                formatoValores.convertirBigDecimalToString(f.getCarteraCastigada()),
                formatoValores.convertirBigDecimalToString(f.getCoutaCredito()),
                DateUtils.toString(f.getFechaCancelacion()),
                f.getFormaCancelacion());
    }

}
