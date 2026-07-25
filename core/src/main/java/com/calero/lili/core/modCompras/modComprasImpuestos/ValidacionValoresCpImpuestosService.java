package com.calero.lili.core.modCompras.modComprasImpuestos;


import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesRepository;
import com.calero.lili.core.modCompras.modComprasImpuestos.builder.CpImpuestoDetalleErrorBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CpImpuestoDetalleError;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class ValidacionValoresCpImpuestosService {

    private final CpImpuestoDetalleErrorBuilder cpImpuestoDetalleErrorBuilder;
    private final AdIvaPorcentajesRepository adIvaPorcentajesRepository;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;


    public void validacionValoresGeneral(CpImpuestosEntity model, List<CpImpuestoDetalleError> detalleErrores) {

        validateIvaPorcentaje(getIntegerTarifaIva(model.getValoresEntity()), model.getFechaEmision(),
                detalleErrores);

        validarValoresImpuestos(model.getValoresEntity(), detalleErrores);

        /*if (Objects.nonNull(model.getCompraImpuestos())) {
            validarValoresRetenciones(model.getCompraImpuestos(), detalleErrores);
        }*/

        if (!detalleErrores.isEmpty()) {

        }

    }

    private void validateIvaPorcentaje(List<Integer> valores, LocalDate fechaFactura,
                                       List<CpImpuestoDetalleError> detalleErrores) {


        Optional<AdIvaPorcentajesEntity> porcentaje = adIvaPorcentajesRepository.findVigente(fechaFactura);

        if (porcentaje.isEmpty()) {
            detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("No existe porcentajes de iva para la fecha:" + fechaFactura));
        }


        Set<Integer> tarifasVigentes = new HashSet<>();

        if (Objects.nonNull(porcentaje.get().getIva1()))

            if (porcentaje.get().getIva1() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva1());
            }

        if (Objects.nonNull(porcentaje.get().getIva2())) {
            if (porcentaje.get().getIva2() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva2());
            }
        }

        if (Objects.nonNull(porcentaje.get().getIva3())) {
            if (porcentaje.get().getIva3() != 0) {
                tarifasVigentes.add(porcentaje.get().getIva3());
            }
        }

        Set<Integer> tarifasExentas = Set.of(0, 6, 7); // 0%=0, No Objeto=6, Exento=7

        for (Integer tarifa : valores) {
            if (tarifasExentas.contains(tarifa)) continue;
            if (!tarifasVigentes.contains(tarifa)) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa de IVA: " + tarifa + " , no está vigente para la fecha: " + fechaFactura));
            }
        }


    }


    private void validarValoresImpuestos(List<CpImpuestosValoresEntity> valores, List<CpImpuestoDetalleError> detalleErrores) {

        valores.forEach(item -> {

            String impuestos = item.getCodigo() + "-" + item.getCodigoPorcentaje();
            validacionDocumentosGeneral.existeImpuesto(impuestos);

            if (item.getBaseImponible().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible no puede ser negativa"));
            }

            if (item.getTarifa().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa no puede ser negativa"));
            }

            if (item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La tarifa no puede ser negativa"));
            }

        });

    }

    private List<Integer> getIntegerTarifaIva(List<CpImpuestosValoresEntity> valores) {
        return valores.stream()
                .map(CpImpuestosValoresEntity::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }


    /*private void validarValoresRetenciones(List<CompraImpuestosDto> listCompraImpuesto, List<CpImpuestoDetalleError> detalleErrores) {


        for (CompraImpuestosDto dto : listCompraImpuesto) {

            dto.getImpuestoCodigos().forEach(item -> {


                if (Objects.isNull(item.getCodigo()) && Objects.isNull(item.getCodigoRetencion())) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El codigo y el codigo de retención siempre no existen"));
                }


                if (item.getCodigo().equals(CodigoRetencion.IVA)) {

                    List<String> codigosValidos = Arrays.asList("1", "2", "3", "7", "9", "10", "11");
                    if (!codigosValidos.contains(item.getCodigoRetencion())) {
                        detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El codigo de retención para el IVA no debe ser: " + item.getCodigoRetencion()));
                    }
                }


                if (item.getBaseImponible().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("La base imponible de la retención no puede ser negativa"));
                }


                if (item.getPorcentajeRetener().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El porcentaje de la retención no puede ser negativa"));
                }

                if (item.getPorcentajeRetener().compareTo(BigDecimal.ZERO) < 0 ||
                        item.getPorcentajeRetener().compareTo(BigDecimal.valueOf(100)) > 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El porcentaje a retener de la retención debe estar entre 0 y 100"));
                }


                if (item.getValorRetenido().compareTo(BigDecimal.ZERO) < 0) {
                    detalleErrores.add(cpImpuestoDetalleErrorBuilder.builder("El valor de la retención no puede ser negativo"));
                }


            });

        }
    }*/


}
