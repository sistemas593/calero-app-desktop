package com.calero.lili.core.utils.calcularValores;

import com.calero.lili.core.dtos.DetallesDto;
import com.calero.lili.core.dtos.ImpuestoItemsDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CalcularValoresDocumentos {

    private final GeImpuestosItemsRepository geImpuestosItemsRepository;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;


    public List<ValoresDto> validarValores(List<DetallesDto> detalles) {

        Map<String, GeImpuestosEntity> impuestosMap = getImpuestosItems(detalles);

        for (DetallesDto item : detalles) {

            BigDecimal subTotalItem = item.getPrecioUnitario().multiply(item.getCantidad())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal subTotalConDescuento = subTotalItem.subtract(item.getDescuento());
            item.setSubtotalItem(subTotalItem);

            for (ImpuestoItemsDto impuesto : item.getImpuesto()) {

                String key = impuesto.getCodigo() + "-" + impuesto.getCodigoPorcentaje();
                GeImpuestosEntity impuestoItem = impuestosMap.get(key);

                if (Objects.nonNull(impuestoItem)) {
                    impuesto.setBaseImponible(subTotalConDescuento);
                    BigDecimal valor = subTotalConDescuento
                            .multiply(impuestoItem.getTarifa())
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    impuesto.setValor(valor);
                    impuesto.setTarifa(impuestoItem.getTarifa());
                }
            }
        }
        return calcularValores(detalles);
    }

    private List<ValoresDto> calcularValores(List<DetallesDto> detalles) {

        Map<String, BigDecimal> subtotalesPorImpuesto = new HashMap<>();
        Map<String, ImpuestoItemsDto> impuestosMap = new HashMap<>();

        for (DetallesDto detail : detalles) {
            for (ImpuestoItemsDto impuesto : detail.getImpuesto()) {
                String key = impuesto.getCodigo() + "-" + impuesto.getCodigoPorcentaje();
                impuestosMap.putIfAbsent(key, impuesto);
                subtotalesPorImpuesto.merge(key, impuesto.getBaseImponible(), BigDecimal::add);
            }
        }

        List<ValoresDto> valores = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> subtotal : subtotalesPorImpuesto.entrySet()) {
            ImpuestoItemsDto impuesto = impuestosMap.get(subtotal.getKey());
            mapearValor(subtotal, impuesto, valores);
        }
        return valores;
    }

    private static void mapearValor(Map.Entry<String, BigDecimal> subtotal, ImpuestoItemsDto impuesto,
                                    List<ValoresDto> valores) {
        ValoresDto valoresDto = new ValoresDto();

        // VALIDAR TARIFA CERO
        BigDecimal valorImpuesto = subtotal.getValue()
                .multiply(impuesto.getTarifa())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        valoresDto.setCodigo(impuesto.getCodigo());
        valoresDto.setCodigoPorcentaje(impuesto.getCodigoPorcentaje());
        valoresDto.setBaseImponible(subtotal.getValue());
        valoresDto.setValor(valorImpuesto);
        valoresDto.setTarifa(impuesto.getTarifa());
        valores.add(valoresDto);
    }


    @NotNull
    private Map<String, GeImpuestosEntity> getImpuestosItems(List<DetallesDto> detalles) {

        List<String> claves = detalles.stream()
                .flatMap(detalle -> detalle.getImpuesto().stream())
                .map(imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje())
                .distinct()
                .toList();

        List<GeImpuestosEntity> impuestos = new ArrayList<>();
        for (String clave : claves) {
            GeImpuestosEntity impuesto = validacionDocumentosGeneral.existeImpuesto(clave);
            impuestos.add(impuesto);
        }

        Map<String, GeImpuestosEntity> impuestosMap = impuestos.stream()
                .collect(Collectors.toMap(
                        imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje(),
                        imp -> imp
                ));
        return impuestosMap;
    }

}
