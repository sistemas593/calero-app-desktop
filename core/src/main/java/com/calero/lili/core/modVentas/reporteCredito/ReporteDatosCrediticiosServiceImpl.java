package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.enums.TipoPersoneria;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
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


    private final DatosCrediticiosRepository datosCrediticiosRepository;
    private final AdEmpresasRepository adEmpresasRepository;
    private final FormatoValores formatoValores;

    public byte[] generarTxt(Long idData, Long idEmpresa) {

        List<DatosCrediticiosEntity> lista = datosCrediticiosRepository.getFindAll(idData, idEmpresa);

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException("No se encontró la empresa con idData: " + idData + " e idEmpresa: " + idEmpresa));

        if (lista.isEmpty()) {
            throw new GeneralException("No existe información para generar el reporte");
        }

        StringBuilder sb = new StringBuilder();

        for (DatosCrediticiosEntity cabecera : lista) {
            for (DatosCrediticiosDetalleEntity f : cabecera.getDatosCrediticiosDetalle()) {
                sb.append(construirLinea(cabecera, f, empresa)).append("\n");
            }
        }


        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String construirLinea(DatosCrediticiosEntity cabecera, DatosCrediticiosDetalleEntity f, AdEmpresaEntity empresa) {

        String parroquia = "";
        String canton = "";
        String provincia = "";

        String sexo = "";
        String estadoCivil = "";
        String origenIngreso = "";


        if (Objects.nonNull(f.getTercero().getParroquia())) {

            String x = f.getTercero().getParroquia().getCodigoParroquia();
            parroquia = x.substring(x.length() - 2);
            if (Objects.nonNull(f.getTercero().getParroquia().getCanton())) {
                String z = f.getTercero().getParroquia().getCanton().getCodigoCanton();
                canton = z.substring(z.length() - 2);
                if (Objects.nonNull(f.getTercero().getParroquia().getCanton().getProvincia())) {
                    provincia = f.getTercero().getParroquia().getCanton().getProvincia().getCodigoProvincia();
                }
            }

        } else {
            throw new GeneralException("El tercero con identificación "
                    + f.getTercero().getNumeroIdentificacion() + " no tiene asignada una parroquia, lo cual es obligatorio para generar el reporte.");
        }


        if (Objects.nonNull(f.getTercero().getTipoPersoneria())) {

            if (f.getTercero().getTipoPersoneria().equals(TipoPersoneria.N)) {

                if (Objects.nonNull(f.getTercero().getSexo())
                        && Objects.nonNull(f.getTercero().getEstadoCivil())
                        && Objects.nonNull(f.getTercero().getOrigenIngresos())) {

                    sexo = sexo.replace("|", f.getTercero().getSexo().name());
                    estadoCivil = estadoCivil.replace("|", f.getTercero().getEstadoCivil().name());
                    origenIngreso = origenIngreso.replace("|", f.getTercero().getOrigenIngresos().name());
                } else {
                    throw new GeneralException("El tercero con identificación "
                            + f.getTercero().getNumeroIdentificacion() + " es un cliente natural, por lo tanto debe tener asignados los campos sexo, estado civil y origen de ingresos para generar el reporte.");
                }
            }
        }

        return String.join("|",
                Objects.nonNull(empresa.getCodigoDinardap()) ? empresa.getCodigoDinardap() : "",
                DateUtils.toString(cabecera.getFechaDatos()),
                f.getTercero().getTipoIdentificacion(),
                f.getTercero().getNumeroIdentificacion(),
                f.getTercero().getTercero(),
                f.getTercero().getTipoPersoneria().name(),
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
                Objects.nonNull(f.getPeriodicidadPago()) ? f.getPeriodicidadPago().toString() : "",
                Objects.nonNull(f.getDiasMorosidad()) ? f.getDiasMorosidad().toString() : "",
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
                Objects.nonNull(f.getFechaCancelacion()) ? DateUtils.toString(f.getFechaCancelacion()) : "",
                Objects.nonNull(f.getFormaCancelacion()) ? f.getFormaCancelacion() : ""); // TODO Tipos Efectivo (E), Cheque(C), Tarjeta de Crédito (T)
    }

}
