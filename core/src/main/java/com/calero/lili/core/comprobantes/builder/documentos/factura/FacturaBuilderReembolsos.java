package com.calero.lili.core.comprobantes.builder.documentos.factura;

import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetalleImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.ReembolsoDetalle;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacturaBuilderReembolsos {

    private final FormatoValores formatoValores;


    public void builderFacturaReembolso(Factura model, VtVentaEntity venta) {

        if (!venta.getReembolsosEntity().isEmpty()) {
            model.setReembolsoDetalle(builderListReembolsos(venta.getReembolsosEntity()));
            model.getInfoFactura().setCodDocReembolso(ConstantesDocumento.CODIGO_REEMBOLSO);
            model.getInfoFactura().setTotalComprobantesReembolso(formatoValores.convertirBigDecimalToString(venta.getTotal()));
            model.getInfoFactura().setTotalBaseImponibleReembolso(formatoValores.convertirBigDecimalToString(venta.getSubtotal()));
            model.getInfoFactura().setTotalImpuestoReembolso(formatoValores.convertirBigDecimalToString(venta.getTotalImpuesto()));
        }

    }


    private List<ReembolsoDetalle> builderListReembolsos(List<VtVentaReembolsosEntity> list) {
        return list.stream()
                .map(this::builderReembolso)
                .toList();
    }

    private ReembolsoDetalle builderReembolso(VtVentaReembolsosEntity reembolso) {
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
                .detalleImpuesto(builderListDetalleReembolso(reembolso.getReembolsosValores()))
                .build();
    }

    private List<DetalleImpuesto> builderListDetalleReembolso(List<VtVentaReembolsosValoresEntity> list) {
        return list.stream()
                .map(this::builderDetalleReembolso)
                .toList();
    }

    private DetalleImpuesto builderDetalleReembolso(VtVentaReembolsosValoresEntity detalle) {
        return DetalleImpuesto.builder()
                .codigo(detalle.getCodigo())
                .codigoPorcentaje(detalle.getCodigoPorcentaje())
                .tarifa(detalle.getTarifa().toString())
                .baseImponibleReembolso(formatoValores.convertirBigDecimalToString(detalle.getBaseImponible()))
                .impuestoReembolso(formatoValores.convertirBigDecimalToString(detalle.getValor()))
                .tarifa(formatoValores.convertirTarifaToString(detalle.getTarifa()))
                .build();
    }
}
