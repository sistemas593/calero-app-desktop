package com.calero.lili.core.modVentas.facturas;

import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesRepository;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.builder.DetallesVentasErrorBuilder;
import com.calero.lili.core.modVentas.dto.DetallesErrorVentasDto;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ValidacionValoresVtVentasService {

    private final AdIvaPorcentajesRepository adIvaPorcentajesRepository;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;
    private final DetallesVentasErrorBuilder detallesVentasErrorBuilder;


    public void validacionValoresVtVentasGeneral(VtVentaEntity model, List<DetallesErrorVentasDto> detalleErrores) {

        validateIvaPorcentaje(getIntegerTarifaIvaVentas(model.getValoresEntity()), model.getFechaEmision().toLocalDate(),
                detalleErrores);

        validarVentasValoresImpuestos(model.getValoresEntity(), detalleErrores);

        setearValoresNegativosNotasCredito(model);

        /*if (Objects.nonNull(model.getCompraImpuestos())) {
            validarValoresRetenciones(model.getCompraImpuestos(), detalleErrores);
        }*/

    }

    private List<Integer> getIntegerTarifaIvaVentas(List<VtVentaValoresEntity> valores) {
        return valores.stream()
                .map(VtVentaValoresEntity::getTarifa)
                .filter(Objects::nonNull)
                .map(BigDecimal::intValue)
                .toList();
    }

    private void validarVentasValoresImpuestos(List<VtVentaValoresEntity> valores,
                                               List<DetallesErrorVentasDto> detalleErrores) {


        valores.forEach(item -> {

            String impuestos = item.getCodigo() + "-" + item.getCodigoPorcentaje();
            validacionDocumentosGeneral.existeImpuesto(impuestos);

            if (item.getBaseImponible().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(detallesVentasErrorBuilder.builder("La base imponible no puede ser negativa"));
            }

            if (item.getTarifa().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(detallesVentasErrorBuilder.builder("La tarifa no puede ser negativa"));
            }

            if (item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                detalleErrores.add(detallesVentasErrorBuilder.builder("EL valor no puede ser negativo"));
            }

        });
    }

    private void setearValoresNegativosNotasCredito(VtVentaEntity model) {
        if (model.getTipoVenta().equals("NCR")) {

            model.setTotal(model.getTotal().negate());
            model.setSubtotal(model.getSubtotal().negate());
            model.setTotalImpuesto(model.getTotalImpuesto().negate());

            model.getValoresEntity().forEach(item -> {
                item.setValor(item.getValor().negate());
                item.setBaseImponible(item.getValor().negate());
            });
        }
    }

    private void validateIvaPorcentaje(List<Integer> valores, LocalDate fechaFactura,
                                       List<DetallesErrorVentasDto> detalleErrores) {


        Optional<AdIvaPorcentajesEntity> porcentaje = adIvaPorcentajesRepository.findVigente(fechaFactura);

        if (porcentaje.isEmpty()) {
            detalleErrores.add(detallesVentasErrorBuilder.builder("No existe porcentajes de iva para la fecha:" + fechaFactura));
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
                detalleErrores.add(detallesVentasErrorBuilder.builder("La tarifa de IVA: " + tarifa + " , no está vigente para la fecha: " + fechaFactura));
            }
        }


    }

}
