package com.calero.lili.core.modVentas.service;


import com.calero.lili.core.dtos.ImpuestoItemsDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import com.calero.lili.core.modVentas.dto.DetailDto;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ValidarValoresServiceImpl {

    private final GeImpuestosItemsRepository geImpuestosItemsRepository;


    public List<ValoresDto> validarValores(List<DetailDto> detalles) {

        Map<String, GeImpuestosEntity> impuestosMap = getImpuestosItems(detalles);

        for (DetailDto item : detalles) {

            BigDecimal subTotalItem = item.getPrecioUnitario().multiply(item.getCantidad());
            BigDecimal subTotalConDescuento = subTotalItem.subtract(item.getDescuento());
            item.setSubtotalItem(subTotalConDescuento);

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

    private List<ValoresDto> calcularValores(List<DetailDto> detalles) {

        Map<String, BigDecimal> subtotalesPorImpuesto = new HashMap<>();
        Map<String, ImpuestoItemsDto> impuestosMap = new HashMap<>();

        for (DetailDto detail : detalles) {
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
    private Map<String, GeImpuestosEntity> getImpuestosItems(List<DetailDto> detalles) {

        List<String> claves = detalles.stream()
                .flatMap(detalle -> detalle.getImpuesto().stream())
                .map(imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje())
                .distinct()
                .toList();

        List<GeImpuestosEntity> impuestos = new ArrayList<>();
        for (String clave : claves) {

            Optional<GeImpuestosEntity> impuesto = geImpuestosItemsRepository.findCodigoAndCodigoPorcentaje(clave);
            if (impuesto.isPresent()) {
                impuestos.add(impuesto.get());
            } else {
                throw new GeneralException(MessageFormat.format("El impuesto con codigos {0}, no existe", clave));
            }
        }

        Map<String, GeImpuestosEntity> impuestosMap = impuestos.stream()
                .collect(Collectors.toMap(
                        imp -> imp.getCodigo() + "-" + imp.getCodigoPorcentaje(),
                        imp -> imp
                ));
        return impuestosMap;
    }
}
