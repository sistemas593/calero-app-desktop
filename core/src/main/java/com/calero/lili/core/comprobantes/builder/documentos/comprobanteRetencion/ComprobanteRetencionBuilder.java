package com.calero.lili.core.comprobantes.builder.documentos.comprobanteRetencion;

import com.calero.lili.core.comprobantes.builder.documentos.CampoAdicionalBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormaDePagoBuilder;
import com.calero.lili.core.comprobantes.builder.documentos.FormatoValores;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.DocSustento;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ImpuestoDocSustento;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.InfoCompRetencion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.Retencion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosCodigosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ComprobanteRetencionBuilder {


    private final InfoTributariaRetencionBuilder infoTributariaRetencionBuilder;
    private final CampoAdicionalBuilder campoAdicionalBuilder;
    private final FormaDePagoBuilder formaDePagoBuilder;
    private final FormatoValores formatoValores;

    public ComprobanteRetencion builderComprobanteRetencion(CpRetencionesEntity retencion,
                                                            AdEmpresaEntity empresa,
                                                            AdEmpresasSeriesEntity serie,
                                                            List<CpImpuestosEntity> listaImpuestos,
                                                            GeTerceroEntity proveedor) {

        return ComprobanteRetencion.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_2_1_0)
                .infoTributaria(infoTributariaRetencionBuilder.builderInfoTributaria(retencion, empresa, serie))
                .infoCompRetencion(infoCompRetencionBuilder(retencion, empresa, serie, proveedor))
                .docSustento(builderListDocumentoSustento(listaImpuestos))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(retencion.getInformacionAdicional()))
                .build();
    }

    private List<DocSustento> builderListDocumentoSustento(List<CpImpuestosEntity> listaImpuestos) {
        return listaImpuestos.stream()
                .map(this::builderDocSustento)
                .toList();
    }

    private DocSustento builderDocSustento(CpImpuestosEntity impuesto) {

        BigDecimal totalSinImpuestos = validarTotalSinImpuesto(impuesto.getValoresEntity());
        BigDecimal importeTotal = validarImporteTotal(impuesto.getValoresEntity(), totalSinImpuestos);

        return DocSustento.builder()
                .codSustento(impuesto.getSustento().getCodigoSustento())
                .codDocSustento(impuesto.getDocumento().getCodigoDocumento())
                .numDocSustento(impuesto.getSerie() + impuesto.getSecuencial())
                .fechaEmisionDocSustento(DateUtils.toString(impuesto.getFechaEmision()))
                .fechaRegistroContable(DateUtils.toString(impuesto.getFechaRegistro()))
                .numAutDocSustento(impuesto.getNumeroAutorizacion())
                .pagoLocExt(impuesto.getPagoLocExt())
                .paisEfecPago(Objects.nonNull(impuesto.getPagoExterior())
                        ? impuesto.getPagoExterior().getPaisEfecPago()
                        : "NA")
                .totalComprobantesReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalBaseImponibleReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalImpuestoReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(totalSinImpuestos))
                .importeTotal(formatoValores.convertirBigDecimalToString(importeTotal))
                .impuestoDocSustento(builderImpuestoList(impuesto.getValoresEntity()))
                .retencion(builderListRetencion(impuesto.getCodigosEntity()))
                .pago(formaDePagoBuilder.builderListFormaPagos(impuesto.getFormasPagoSri()))
                .build();
    }

    private List<Retencion> builderListRetencion(List<CpImpuestosCodigosEntity> codigosEntity) {
        return codigosEntity.stream()
                .map(this::builderRetencion)
                .toList();
    }

    private Retencion builderRetencion(CpImpuestosCodigosEntity retencion) {
        return Retencion.builder()
                .codigo(retencion.getRetencion().getCodigo())
                .codigoRetencion(retencion.getRetencionCodigos().getCodigoRetencion())
                .baseImponible(formatoValores.convertirBigDecimalToString(retencion.getBaseImponible()))
                .porcentajeRetener(formatoValores.convertirBigDecimalToString(retencion.getPorcentajeRetener()))
                .valorRetenido(formatoValores.convertirBigDecimalToString(retencion.getValorRetenido()))
                .build();
    }

    private List<ImpuestoDocSustento> builderImpuestoList(List<CpImpuestosValoresEntity> valoresEntity) {
        return valoresEntity.stream()
                .map(this::builderImpuestoDocSustento)
                .toList();
    }

    private ImpuestoDocSustento builderImpuestoDocSustento(CpImpuestosValoresEntity valores) {
        return ImpuestoDocSustento.builder()
                .codImpuestoDocSustento(valores.getCodigo())
                .codigoPorcentaje(valores.getCodigoPorcentaje())
                .baseImponible(formatoValores.convertirBigDecimalToString(valores.getBaseImponible()))
                .tarifa(formatoValores.convertirTarifaToString(valores.getTarifa()))
                .valorImpuesto(formatoValores.convertirBigDecimalToString(valores.getValor()))
                .build();
    }


    private InfoCompRetencion infoCompRetencionBuilder(CpRetencionesEntity retencion,
                                                       AdEmpresaEntity empresa, AdEmpresasSeriesEntity serie,
                                                       GeTerceroEntity proveedor) {
        return InfoCompRetencion.builder()
                .fechaEmision(DateUtils.toString(retencion.getFechaEmisionRetencion()))
                .dirEstablecimiento(serie.getDireccionEstablecimiento())
                .contribuyenteEspecial(empresa.getContribuyenteEspecial())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .tipoIdentificacionSujetoRetenido(retencion.getTipoIdentificacion().getCodigo())
                .parteRel(retencion.getRelacionado())
                .razonSocialSujetoRetenido(proveedor.getTercero())
                .identificacionSujetoRetenido(retencion.getNumeroIdentificacion())
                .periodoFiscal(retencion.getPeriodoFiscal())
                .build();
    }


    private BigDecimal validarImporteTotal(List<CpImpuestosValoresEntity> valoresEntity, BigDecimal baseImponible) {

        BigDecimal totalImpuestos = valoresEntity.stream()
                .map(CpImpuestosValoresEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalImpuestos.add(baseImponible);
    }

    private BigDecimal validarTotalSinImpuesto(List<CpImpuestosValoresEntity> valoresEntity) {
        return valoresEntity.stream()
                .map(CpImpuestosValoresEntity::getBaseImponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
