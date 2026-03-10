package com.calero.lili.api.comprobantes.builder.documentos.liquidaciones;

import com.calero.lili.api.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.api.comprobantes.objetosXml.liquidacionCompras.DetalleImpuesto;
import com.calero.lili.api.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.api.comprobantes.objetosXml.liquidacionCompras.ReembolsoDetalle;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LiquidacionReembolsoBuilder {

    private final FormatoValores formatoValores;


    public void builderLiquidacionReembolso(CpLiquidacionesEntity liquidacion, LiquidacionCompra liquidacionCompra) {
        builderInfoLiquidacionCompra(liquidacion, liquidacionCompra);
        liquidacionCompra.setReembolsoDetalle(builderListReembolsoDetalle(liquidacion.getReembolsosEntity()));
    }

    private List<ReembolsoDetalle> builderListReembolsoDetalle(List<CpLiquidacionesReembolsosEntity> list) {
        return list.stream()
                .map(this::builderReembolsoDetalle)
                .toList();
    }

    private ReembolsoDetalle builderReembolsoDetalle(CpLiquidacionesReembolsosEntity reembolso) {
        return ReembolsoDetalle.builder()
                .tipoIdentificacionProveedorReembolso(TipoIdentificacion.valueOf(reembolso.getTipoIdentificacionReemb()).getCodigo())
                .identificacionProveedorReembolso(reembolso.getNumeroIdentificacionReemb())
                .codPaisPagoProveedorReembolso(reembolso.getPais().getCodigoPais())
                .tipoProveedorReembolso(reembolso.getTipoProveedorReemb().getTipoCliente())
                .codDocReembolso(reembolso.getCodigoDocumentoReemb())
                .estabDocReembolso(reembolso.getSerieReemb().substring(0, 3))
                .ptoEmiDocReembolso(reembolso.getSerieReemb().substring(3, 6))
                .secuencialDocReembolso(reembolso.getSecuencialReemb())
                .fechaEmisionDocReembolso(DateUtils.toString(reembolso.getFechaEmisionReemb()))
                .numeroautorizacionDocReemb(reembolso.getNumeroAutorizacionReemb())
                .detalleImpuesto(builderListDetalleImpuestoReembolso(reembolso.getReembolsosValores()))
                .build();
    }

    private List<DetalleImpuesto> builderListDetalleImpuestoReembolso(List<CpLiquidacionesReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderDetalleReembolso)
                .toList();
    }

    private DetalleImpuesto builderDetalleReembolso(CpLiquidacionesReembolsosValoresEntity detalle) {
        return DetalleImpuesto.builder()
                .codigo(detalle.getCodigo())
                .codigoPorcentaje(detalle.getCodigoPorcentaje())
                .baseImponibleReembolso(formatoValores.convertirBigDecimalToString(detalle.getBaseImponible()))
                .tarifa(formatoValores.convertirTarifaToString(detalle.getTarifa()))
                .impuestoReembolso(formatoValores.convertirBigDecimalToString(detalle.getValor()))
                .build();
    }


    private void builderInfoLiquidacionCompra(CpLiquidacionesEntity liquidacion, LiquidacionCompra liquidacionCompra) {

        liquidacionCompra.getInfoLiquidacionCompra().setCodDocReembolso(liquidacion.getCodDocReembolso());
        liquidacionCompra.getInfoLiquidacionCompra().setTotalComprobantesReembolso(formatoValores.convertirBigDecimalToString(liquidacion.getTotalComprobantesReembolso()));
        liquidacionCompra.getInfoLiquidacionCompra().setTotalBaseImponibleReembolso(formatoValores.convertirBigDecimalToString(liquidacion.getTotalBaseImponibleReembolso()));
        liquidacionCompra.getInfoLiquidacionCompra().setTotalImpuestoReembolso(formatoValores.convertirBigDecimalToString(liquidacion.getTotalImpuestoReembolso()));
    }

}
