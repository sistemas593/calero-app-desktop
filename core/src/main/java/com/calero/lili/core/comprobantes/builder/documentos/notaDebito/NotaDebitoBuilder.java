package com.calero.lili.core.comprobantes.builder.documentos.notaDebito;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.builder.documentos.InfoTributariaBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.Impuesto;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.InfoNotaDebito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.Motivo;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotaDebitoBuilder {

    private final InfoTributariaBuilder infoTributariaBuilder;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final FormatoValores formatoValores;

    public NotaDebito builderNotaDebito(VtVentaEntity venta, AdEmpresaEntity empresa,
                                        AdEmpresasSeriesEntity serie) {
        return NotaDebito.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_1_0_0)
                .infoTributaria(infoTributariaBuilder.builderInfoTributaria(venta, empresa, serie, "05"))
                .infoNotaDebito(builderInfoNotaDebito(venta, empresa, serie))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(venta.getInformacionAdicional()))
                .motivo(builderListMotivo(venta))
                .build();
    }

    private List<Motivo> builderListMotivo(VtVentaEntity venta) {
        if (Objects.isNull(venta.getDetalle())) return null;
        return venta.getDetalle()
                .stream()
                .map(this::builderMotivo)
                .toList();
    }

    private Motivo builderMotivo(VtVentaDetalleEntity model) {
        return Motivo.builder()
                .razon(model.getDescripcion())
                .valor(formatoValores.convertirBigDecimalToString(model.getPrecioUnitario()))
                .build();
    }

    private InfoNotaDebito builderInfoNotaDebito(VtVentaEntity venta, AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie) {
        return InfoNotaDebito.builder()
                .fechaEmision(DateUtils.toStringFechaEmision(venta.getFechaEmision()))
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .contribuyenteEspecial(Objects.isNull(empresa.getContribuyenteEspecial())
                        || empresa.getContribuyenteEspecial().isEmpty() ? null : empresa.getContribuyenteEspecial())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .codDocModificado(venta.getModCodigoDocumento())
                .numDocModificado(venta.getModSerie().substring(0, 3) + "-" + venta.getModSerie().substring(3, 6) + "-" + venta.getModSecuencial())
                .fechaEmisionDocSustento(Objects.nonNull(venta.getModFechaEmision())
                        ? DateUtils.toString(venta.getModFechaEmision())
                        : DateUtils.toString(LocalDate.now()))
                .tipoIdentificacionComprador(TipoIdentificacion.valueOf(venta.getTercero().getTipoIdentificacion()).getCodigo())
                .razonSocialComprador(venta.getTercero().getTercero())
                .identificacionComprador(venta.getTercero().getNumeroIdentificacion())
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(venta.getSubtotal()))
                .impuesto(builderImpuestoListNotaDebito(venta.getValoresEntity()))
                .valorTotal(formatoValores.convertirBigDecimalToString(venta.getTotal()))
                .build();
    }

    private List<Impuesto> builderImpuestoListNotaDebito(List<VtVentaValoresEntity> valoresEntity) {
        return valoresEntity.stream()
                .map(this::builderImpuestoNotaDebito)
                .toList();
    }

    private Impuesto builderImpuestoNotaDebito(VtVentaValoresEntity model) {
        return Impuesto.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(formatoValores.convertirBigDecimalToString(model.getTarifa()))
                .baseImponible(formatoValores.convertirBigDecimalToString(model.getBaseImponible()))
                .valor(formatoValores.convertirBigDecimalToString(model.getValor()))
                .build();
    }

}
