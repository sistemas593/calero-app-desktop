package com.calero.lili.core.comprobantes.builder.documentos.factura;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormaDePagoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.builder.documentos.InfoTributariaBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.TotalImpuestoBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoFactura;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FacturaBuilder {

    private final InfoTributariaBuilder infoTributariaBuilder;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final TotalImpuestoBuilder totalImpuestoBuilder;
    private final FormaDePagoBuilder formaDePagoBuilder;
    private final DetallesBuilder detallesBuilder;
    private final FormatoValores formatoValores;

    public Factura builderFactura(VtVentaEntity venta, AdEmpresaEntity empresa,
                                  AdEmpresasSeriesEntity serie) {


        return Factura.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_2_1_0)
                .infoTributaria(infoTributariaBuilder.builderInfoTributaria(venta, empresa, serie, "01"))
                .infoFactura(builderInfoFactura(venta, empresa, serie))
                .detalle(detallesBuilder.builderListDetalle(venta.getDetalle()))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(venta.getInformacionAdicional()))
                .build();
    }

    private InfoFactura builderInfoFactura(VtVentaEntity venta, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return InfoFactura.builder()
                .fechaEmision(DateUtils.toString(venta.getFechaEmision()))
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .contribuyenteEspecial(Objects.nonNull(empresa.getContribuyenteEspecial()) && !empresa.getContribuyenteEspecial().isEmpty()
                        ? empresa.getContribuyenteEspecial()
                        : null)
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .tipoIdentificacionComprador(TipoIdentificacion.valueOf(venta.getTipoIdentificacion()).getCodigo())
                .razonSocialComprador(venta.getTerceroNombre())
                .direccionComprador(venta.getTercero().getDireccion())
                .identificacionComprador(venta.getNumeroIdentificacion())
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(venta.getSubtotal()))
                .totalDescuento(formatoValores.convertirBigDecimalToString(venta.getTotalDescuento()))
                .importeTotal(formatoValores.convertirBigDecimalToString(venta.getTotal()))
                .moneda(ConstantesDocumento.MONEDA)
                .totalImpuesto(totalImpuestoBuilder.builderListTotalImpuesto(venta.getValoresEntity()))
                .pago(formaDePagoBuilder.builderListFormaPagos(venta.getFormasPagoSri()))
                .propina(ConstantesDocumento.PROPINA)
                .guiaRemision(validarGuiaRemision(venta))
                .build();
    }

    private String validarGuiaRemision(VtVentaEntity venta) {
        if (Objects.nonNull(venta.getGuiaRemisionSecuencial()) && Objects.nonNull(venta.getGuiaRemisionSerie())) {
            if (!venta.getGuiaRemisionSecuencial().isEmpty() && !venta.getGuiaRemisionSerie().isEmpty()) {
                return venta.getGuiaRemisionSerie().substring(0, 3) + "-" + venta.getGuiaRemisionSerie().substring(3) + "-" + venta.getGuiaRemisionSecuencial();
            }
        }
        return null;
    }
}
