package com.calero.lili.core.modCompras.modComprasImpuestos.builder;

import com.calero.lili.core.modCompras.modComprasImpuestos.dto.ValoresCompraImpuestoDto;
import com.calero.lili.core.modImpuestosAnexos.ats.Reembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class CpImpuestoReembolsoValidacionBuilder {


    public List<Reembolso> builderListReembolsos(List<com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso> reembolsos) {
        if (Objects.isNull(reembolsos)) return null;
        return reembolsos.stream()
                .map(this::builderReembolso)
                .toList();
    }

    private Reembolso builderReembolso(com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso model) {
        Reembolso reembolso = Reembolso.builder()
                .tipoComprobanteReemb(model.getCodigoDocumentoReemb())
                .tpIdProvReemb(model.getTipoProveedorReemb())
                .idProvReemb(model.getNumeroIdentificacionReemb())
                .establecimientoReemb(model.getSerieReemb().substring(0, 3))
                .puntoEmisionReemb(model.getSerieReemb().substring(3, 6))
                .fechaEmisionReemb(Objects.nonNull(model.getFechaEmisionReemb()) ? model.getFechaEmisionReemb() : null)
                .autorizacionReemb(model.getNumeroAutorizacionReemb())
                .build();
        setearValores(reembolso, model);
        return reembolso;
    }


    private void setearValores(Reembolso reembolso, com.calero.lili.core.modCompras.modComprasImpuestos.dto.Reembolso model) {

        List<String> codigoIva = Arrays.asList("4", "5", "8");

        BigDecimal baseImponibleNoGravada = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        item.getCodigoPorcentaje().equals("0"))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseImponibleGravada = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        codigoIva.contains(item.getCodigoPorcentaje()))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseImponibleExcenta = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        item.getCodigoPorcentaje().equals("7"))
                .map(ValoresCompraImpuestoDto::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal montoIva = model.getReembolsosValores().stream()
                .filter(item -> item.getCodigo().equals("2") &&
                        codigoIva.contains(item.getCodigoPorcentaje()))
                .map(ValoresCompraImpuestoDto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalBaseImponible = baseImponibleExcenta.add(baseImponibleGravada).add(baseImponibleNoGravada);

        reembolso.setBaseImponibleReemb(totalBaseImponible.toString());
        reembolso.setBaseImpExeReemb(baseImponibleExcenta.toString());
        reembolso.setBaseNoGraIvaReemb(baseImponibleNoGravada.toString());
        reembolso.setBaseImpGravReemb(baseImponibleGravada.toString());
        reembolso.setMontoIvaRemb(montoIva.toString());

    }


}
