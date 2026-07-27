package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.modImpuestosAnexos.ats.Reembolso;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ValidacionReembolsoService {

    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;

    public void validarReembolso(Reembolso item, List<CpImpuestoDetalleError> detalleErrors, LocalDate fechaCabecera) {


        LocalDate fechaReembolso = DateUtils.toLocalDate(item.getFechaEmisionReemb());

        BigDecimal baseImponible = new BigDecimal(!item.getBaseImponibleReemb().isEmpty()
                ? item.getBaseImponibleReemb() : "0.00");

        BigDecimal baseImponibleGrav = new BigDecimal(!item.getBaseImpGravReemb().isEmpty()
                ? item.getBaseImpGravReemb() : "0.00");

        BigDecimal baseImponibleNoGrav = new BigDecimal(!item.getBaseNoGraIvaReemb().isEmpty()
                ? item.getBaseNoGraIvaReemb() : "0.00");

        BigDecimal baseImponibleExc = new BigDecimal(!item.getBaseImpExeReemb().isEmpty()
                ? item.getBaseImpExeReemb() : "0.00");

        BigDecimal montoIva = new BigDecimal(!item.getMontoIvaRemb().isEmpty()
                ? item.getMontoIvaRemb() : "0.00");

        BigDecimal montoIce = new BigDecimal(!item.getMontoIceRemb().isEmpty()
                ? item.getMontoIceRemb() : "0.00");


        if (fechaCabecera.isBefore(fechaReembolso)) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("La fecha del reembolso: " + fechaReembolso +
                    "no puede ser menor que la fecha del documento principal" + fechaCabecera));
        }

        if (Objects.nonNull(item.getEstablecimientoReemb()) && Objects.nonNull(item.getPuntoEmisionReemb())) {

            // VALIDACION SERIE
            String serie = item.getEstablecimientoReemb() + item.getPuntoEmisionReemb();
            String validacionSerie = validacionDocumentosGeneral.validarSerie(serie);
            if (!validacionSerie.isEmpty()) {
                detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder(validacionSerie + " en el rembolso"));
            }

        } else {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("No existe establecimiento, ni punto de emisión para reembolso"));
        }

        // VALIDACION NUMERO AUTORIZACION
        String validacionNumAut = validacionDocumentosGeneral.validarNumeroAutorizacion(item.getAutorizacionReemb());
        if (!validacionNumAut.isEmpty()) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder(validacionNumAut + " en el rembolso"));
        }

        // VALIDACION SECUENCIAL
        String validacionSec = validacionDocumentosGeneral.validarSecuencial(item.getSecuencialReemb());
        if (!validacionSec.isEmpty()) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder(validacionSec + " en el rembolso"));
        }

        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible no puede ser negativa en el reembolso"));
        }

        if (baseImponibleGrav.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible gravada no puede ser negativa en el reembolso"));
        }

        if (baseImponibleNoGrav.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible no gravada no puede ser negativa en el reembolso"));
        }


        if (baseImponibleExc.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible excenta no puede ser negativa en el reembolso"));
        }

        if (montoIva.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("El valor del IVA no puede ser negativo en el reembolso"));
        }

        if (montoIce.compareTo(BigDecimal.ZERO) < 0) {
            detalleErrors.add(cpImpuestoDetalleErrorBuilder.builder("El valor del ICE no puede ser negativo en el reembolso"));
        }


    }

}
