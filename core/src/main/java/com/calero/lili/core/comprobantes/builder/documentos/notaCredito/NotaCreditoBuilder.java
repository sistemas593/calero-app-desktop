package com.calero.lili.core.comprobantes.builder.documentos.notaCredito;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.DetalleAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.builder.documentos.ImpuestoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.InfoTributariaBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.TotalImpuestoBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.InfoNotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotaCreditoBuilder {

    private final InfoTributariaBuilder infoTributariaBuilder;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final ImpuestoBuilder impuestoBuilder;
    private final DetalleAdicionalBuilder detalleAdicionalBuilder;
    private final TotalImpuestoBuilder totalImpuestoBuilder;
    private final FormatoValores formatoValores;

    public NotaCredito builderNotaCredito(VtVentaEntity venta, AdEmpresaEntity empresa,
                                          AdEmpresasSeriesEntity serie) {
        return NotaCredito.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_1_1_0)
                .infoTributaria(infoTributariaBuilder.builderInfoTributaria(venta, empresa, serie, "04"))
                .infoNotaCredito(builderInfoNotaCredito(venta, empresa, serie))
                .detalle(builderListDetalle(venta.getDetalle()))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(venta.getInformacionAdicional()))
                .build();
    }

    private InfoNotaCredito builderInfoNotaCredito(VtVentaEntity venta, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return InfoNotaCredito.builder()
                .fechaEmision(DateUtils.toString(venta.getFechaEmision()))
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .contribuyenteEspecial(empresa.getContribuyenteEspecial())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .codDocModificado(venta.getModCodigoDocumento())
                .numDocModificado(venta.getModSerie().substring(0, 3) + "-" + venta.getModSerie().substring(3, 6) + "-" + venta.getModSecuencial())
                .fechaEmisionDocSustento(Objects.nonNull(venta.getModFechaEmision())
                        ? DateUtils.toString(venta.getModFechaEmision())
                        : DateUtils.toString(LocalDate.now()))
                .tipoIdentificacionComprador(TipoIdentificacion.valueOf(venta.getTipoIdentificacion()).getCodigo())
                .razonSocialComprador(venta.getTerceroNombre())
                .identificacionComprador(venta.getNumeroIdentificacion())
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(venta.getSubtotal()))
                .valorModificacion(formatoValores.convertirBigDecimalToString(venta.getTotal()))
                .totalImpuesto(totalImpuestoBuilder.builderListTotalImpuesto(venta.getValoresEntity()))
                .moneda(ConstantesDocumento.MONEDA)
                .motivo(venta.getConcepto())
                .build();
    }

    private List<Detalle> builderListDetalle(List<VtVentaDetalleEntity> list) {
        return list.stream()
                .map(this::builderDetalle)
                .toList();
    }

    private Detalle builderDetalle(VtVentaDetalleEntity detalle) {
        return Detalle.builder()
                .codigoInterno(detalle.getCodigoPrincipal())
                .codigoAdicional(detalle.getCodigoAuxiliar())
                .cantidad(formatoValores.convertirBigDecimalToString(detalle.getCantidad()))
                .precioUnitario(formatoValores.convertirBigDecimalToString(detalle.getPrecioUnitario()))
                .descuento(formatoValores.convertirBigDecimalToString(detalle.getDescuento()))
                .precioTotalSinImpuesto(formatoValores.convertirBigDecimalToString(detalle.getSubtotalItem()))
                .impuesto(impuestoBuilder.builderListImpuesto(detalle.getImpuesto()))
                .detAdicional(detalleAdicionalBuilder.builderListDetalleAdicional(detalle.getDetAdicional()))
                .descripcion(detalle.getDescripcion())
                .build();
    }
}
