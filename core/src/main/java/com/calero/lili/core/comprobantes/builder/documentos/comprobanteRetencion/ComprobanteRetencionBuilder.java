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
import com.calero.lili.core.enums.PagoLocalExterior;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import com.calero.lili.core.utils.validaciones.ObligadoContabilidad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
                                                            GeTerceroEntity proveedor,
                                                            CreationRetencionRequestDto request) {

        return ComprobanteRetencion.builder()
                .id(ConstantesDocumento.NOMBRE_COMPROBANTE)
                .version(ConstantesDocumento.VERSION_2_0_0)
                .infoTributaria(infoTributariaRetencionBuilder.builderInfoTributaria(retencion, empresa, serie))
                .infoCompRetencion(infoCompRetencionBuilder(retencion, empresa, serie, proveedor))
                .docSustento(builderListDocumentoSustento(listaImpuestos, request))
                .campoAdicional(campoAdicionalBuilder.builderListCampoAdicional(retencion.getInformacionAdicional()))
                .build();
    }

    private List<DocSustento> builderListDocumentoSustento(List<CpImpuestosEntity> listaImpuestos, CreationRetencionRequestDto request) {

        Map<UUID, List<ImpuestoCodigoDto>> codigosPorImpuesto =
                request.getCompraImpuestos()
                        .stream()
                        .collect(Collectors.toMap(
                                CompraImpuestosDto::getCompraImpuestoId,
                                CompraImpuestosDto::getImpuestoCodigos
                        ));

        List<DocSustento> docSustentos = new ArrayList<>();

        for (CpImpuestosEntity impuesto : listaImpuestos) {

            DocSustento docSustento = builderDocSustento(impuesto);

            List<ImpuestoCodigoDto> impuestoCodigos =
                    codigosPorImpuesto.getOrDefault(
                            impuesto.getIdImpuestos(),
                            Collections.emptyList()
                    );

            docSustento.setRetencion(
                    impuestoCodigos.stream()
                            .map(this::builderRetencion)
                            .toList()
            );

            docSustentos.add(docSustento);
        }

        return docSustentos;
    }

    private DocSustento builderDocSustento(CpImpuestosEntity impuesto) {

        BigDecimal totalSinImpuestos = validarTotalSinImpuesto(impuesto.getValoresEntity());
        BigDecimal importeTotal = validarImporteTotal(impuesto.getValoresEntity(), totalSinImpuestos);

        DocSustento docSustento = DocSustento.builder()
                .codSustento(impuesto.getCodigoSustento().name().replace("S", ""))
                .codDocSustento(impuesto.getCodigoDocumento().getCodigo())
                .numDocSustento(impuesto.getSerie() + impuesto.getSecuencial())
                .fechaEmisionDocSustento(DateUtils.toString(impuesto.getFechaEmision()))
                .fechaRegistroContable(DateUtils.toString(impuesto.getFechaRegistro()))
                .numAutDocSustento(impuesto.getNumeroAutorizacion())
                .pagoLocExt(impuesto.getPagoLocExt().getCodigo())
                .totalComprobantesReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalBaseImponibleReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalImpuestoReembolso(formatoValores.convertirBigDecimalToString(new BigDecimal("0.00")))
                .totalSinImpuestos(formatoValores.convertirBigDecimalToString(totalSinImpuestos))
                .importeTotal(formatoValores.convertirBigDecimalToString(importeTotal))
                .impuestoDocSustento(builderImpuestoList(impuesto.getValoresEntity()))
                .pago(formaDePagoBuilder.builderListFormaPagos(impuesto.getFormasPagoSri()))
                .build();

        switch (impuesto.getPagoLocExt()) {
            case PagoLocalExterior.L -> {
                docSustento.setPaisEfecPago("593");
            }
            case PagoLocalExterior.E -> {
                docSustento.setTipoRegi(impuesto.getPagoExterior().getTipoRegi());
                docSustento.setPaisEfecPago(impuesto.getPagoExterior().getPaisEfecPago());
                docSustento.setAplicConvDobTrib(impuesto.getPagoExterior().getAplicConvDobTrib());
                docSustento.setPagExtSujRetNorLeg(impuesto.getPagoExterior().getPagExtSujRetNorLeg());
                docSustento.setPagoRegFis(impuesto.getPagoExterior().getPagoRegFis());
            }
        }


        return docSustento;
    }


    private Retencion builderRetencion(ImpuestoCodigoDto retencion) {
        return Retencion.builder()
                .codigo(retencion.getCodigo().getCodigo())
                .codigoRetencion(retencion.getCodigoRetencion())
                .baseImponible(formatoValores.convertirBigDecimalToString(retencion.getBaseImponible()))
                .porcentajeRetener(formatoValores.convertirBigDecimalToString(retencion.getPorcentajeRetener()))
                .valorRetenido(formatoValores.convertirBigDecimalToString(retencion.getValorRetenido()))
                .build();
    }

    /*private List<Retencion> builderListRetencion(List<CpImpuestosCodigosEntity> codigosEntity) {
        return codigosEntity.stream()
                .map(this::builderRetencion)
                .toList();
    }



    private Retencion builderRetencion(CpImpuestosCodigosEntity retencion) {
        return Retencion.builder()
                .codigo(retencion.getCodigo().getCodigo())
                .codigoRetencion(retencion.getCodigoRetencion())
                .baseImponible(formatoValores.convertirBigDecimalToString(retencion.getBaseImponible()))
                .porcentajeRetener(formatoValores.convertirBigDecimalToString(retencion.getPorcentajeRetener()))
                .valorRetenido(formatoValores.convertirBigDecimalToString(retencion.getValorRetenido()))
                .build();
    }*/

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
                .contribuyenteEspecial(Objects.isNull(empresa.getContribuyenteEspecial())
                        || empresa.getContribuyenteEspecial().isEmpty() ? null : empresa.getContribuyenteEspecial())
                .obligadoContabilidad(ObligadoContabilidad.getObligadoContabilidad(empresa.getObligadoContabilidad()))
                .tipoIdentificacionSujetoRetenido(TipoIdentificacion.valueOf(retencion.getProveedor().getTipoIdentificacion()).getCodigo())
                .parteRel(retencion.getRelacionado())
                .razonSocialSujetoRetenido(proveedor.getTercero())
                .identificacionSujetoRetenido(retencion.getProveedor().getNumeroIdentificacion())
                .periodoFiscal(DateUtils.toLocalDatePeriodoFiscal(retencion.getPeriodoFiscal()))
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
