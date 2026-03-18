package com.calero.lili.core.comprobantes.builder.documentos.liquidaciones;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormaDePagoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Impuesto;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.InfoLiquidacionCompra;
import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesDetalleEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesValoresEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LiquidacionBuilder {


    private final InfoTributariaLiquidacionBuilder infoTributariaLiquidacionBuilder;
    private final FormaDePagoBuilder formaDePagoBuilder;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final FormatoValores formatoValores;

    public LiquidacionCompra builderLiquidacion(CpLiquidacionesEntity liquidacion, AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie) {
        return LiquidacionCompra.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_1_1_0)
                .infoTributaria(infoTributariaLiquidacionBuilder.builderInfoTributaria(liquidacion, empresa, serie))
                .infoLiquidacionCompra(builderInfoLiquidacionCompra(liquidacion, empresa, serie))
                .detalle(builderListDetalle(liquidacion.getDetalle()))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(liquidacion.getInformacionAdicional()))
                .build();
    }


    private List<Detalle> builderListDetalle(List<CpLiquidacionesDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private Detalle builderDetalle(CpLiquidacionesDetalleEntity detalle) {
        return Detalle.builder()
                .codigoPrincipal(detalle.getCodigoPrincipal())
                .descripcion(detalle.getDescripcion())
                .cantidad(formatoValores.convertirBigDecimalToString(detalle.getCantidad()))
                .precioUnitario(formatoValores.convertirBigDecimalToString(detalle.getPrecioUnitario()))
                .descuento(formatoValores.convertirBigDecimalToString(detalle.getDescuento()))
                .detAdicional(builderListDetalleAdicional(detalle.getDetAdicional()))
                .impuesto(builderListImpuestos(detalle.getImpuesto()))
                .precioTotalSinImpuesto(formatoValores.convertirBigDecimalToString(detalle.getSubtotalItem()))
                .build();
    }

    private List<Impuesto> builderListImpuestos(List<CpLiquidacionesDetalleEntity.Impuestos> list) {
        return list.stream()
                .map(this::builderImpuesto)
                .toList();
    }

    private Impuesto builderImpuesto(CpLiquidacionesDetalleEntity.Impuestos impuesto) {
        return Impuesto.builder()
                .codigo(impuesto.getCodigo())
                .codigoPorcentaje(impuesto.getCodigoPorcentaje())
                .baseImponible(formatoValores.convertirBigDecimalToString(impuesto.getBaseImponible()))
                .tarifa(formatoValores.convertirTarifaToString(impuesto.getTarifa()))
                .valor(formatoValores.convertirBigDecimalToString(impuesto.getValor()))
                .build();
    }

    private List<DetAdicional> builderListDetalleAdicional(List<CpLiquidacionesDetalleEntity.DetalleAdicional> list) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(this::builderDetalleAdicional)
                .toList();
    }

    private DetAdicional builderDetalleAdicional(CpLiquidacionesDetalleEntity.DetalleAdicional detalleAdicional) {
        return DetAdicional.builder()
                .nombre(detalleAdicional.getNombre())
                .valor(detalleAdicional.getValor())
                .build();
    }

    private InfoLiquidacionCompra builderInfoLiquidacionCompra(CpLiquidacionesEntity liquidacion, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return InfoLiquidacionCompra.builder()
                .fechaEmision(DateUtils.toString(liquidacion.getFechaEmision()))
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .contribuyenteEspecial(empresa.getContribuyenteEspecial())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .tipoIdentificacionProveedor(TipoIdentificacion.valueOf(liquidacion.getProveedor().getTipoIdentificacion()).getCodigo())
                .razonSocialProveedor(liquidacion.getProveedor().getTercero())
                .identificacionProveedor(liquidacion.getProveedor().getNumeroIdentificacion())
                .direccionProveedor(liquidacion.getProveedor().getDireccion())
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(liquidacion.getSubtotal()))
                .totalDescuento(formatoValores.convertirBigDecimalToString(liquidacion.getTotalDescuento()))
                .totalImpuesto(builderListTotalImpuesto(liquidacion.getValoresEntity()))
                .importeTotal(formatoValores.convertirBigDecimalToString(liquidacion.getTotal()))
                .moneda(ConstantesDocumento.MONEDA)
                .pago(formaDePagoBuilder.builderListFormaPagos(liquidacion.getFormasPagoSri()))
                .build();
    }


    public List<TotalImpuesto> builderListTotalImpuesto(List<CpLiquidacionesValoresEntity> list) {
        return list.stream()
                .map(this::builderTotalImpuesto)
                .toList();
    }

    private TotalImpuesto builderTotalImpuesto(CpLiquidacionesValoresEntity model) {
        return TotalImpuesto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(formatoValores.convertirBigDecimalToString(model.getBaseImponible()))
                .valor(formatoValores.convertirBigDecimalToString(model.getValor()))
                .build();
    }


}
