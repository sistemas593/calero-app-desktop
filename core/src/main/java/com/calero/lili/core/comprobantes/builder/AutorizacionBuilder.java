package com.calero.lili.core.comprobantes.builder;

import com.calero.lili.core.comprobantes.objetosXml.TotalImpuesto;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.DocSustento;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.Retencion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.Impuesto;
import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Detalle;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoFactura;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.dtos.FormasPagoSri;
import com.calero.lili.core.dtos.InformacionAdicional;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.CodigoDocumento;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosValoresEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.CpLiquidacionesReembolsosValoresEntity;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionReferencias;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.VtVentaDetalleEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentaValoresEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosEntity;
import com.calero.lili.core.modVentas.reembolsos.VtVentaReembolsosValoresEntity;
import com.calero.lili.core.modVentasRetenciones.VtRetencionesEntity;
import com.calero.lili.core.modVentasRetenciones.VtRetencionesValoresEntity;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentoEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class AutorizacionBuilder {


    public CpImpuestosEntity builderFactura(Autorizacion model, Factura documento,
                                            Long idData, Long idEmpresa,
                                            GeTerceroEntity proveedor) {
        return CpImpuestosEntity.builder()
                .idImpuestos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serie(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencial(documento.getInfoTributaria().getSecuencial())
                .documento(builderTipoDocumento(CodigoDocumento.FACTURA))
                .fechaEmision(DateUtils.toLocalDate(documento.getInfoFactura().getFechaEmision()))
                .tercero(proveedor)
                .formasPagoSri(builderListFormasPagoSri(documento.getInfoFactura().getPago()))
                .pagoLocExt("01")
                .build();
    }

    private List<FormasPagoSri> builderListFormasPagoSri(List<Pago> pago) {
        if (Objects.isNull(pago)) return null;
        return pago.stream()
                .map(this::builderFormaPagos)
                .toList();
    }

    private FormasPagoSri builderFormaPagos(Pago pago) {

        String x = pago.getTotal().replace(" ", "");
        return FormasPagoSri.builder()
                .formaPago(pago.getFormaPago())
                .plazo(pago.getPlazo())
                .total(new BigDecimal(x))
                .unidadTiempo(pago.getUnidadTiempo())
                .build();
    }

    public CpImpuestosEntity builderNotaCredito(Autorizacion model, NotaCredito documento,
                                                Long idData, Long idEmpresa,
                                                GeTerceroEntity proveedor) {
        return CpImpuestosEntity.builder()
                .idImpuestos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serie(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencial(documento.getInfoTributaria().getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(documento.getInfoNotaCredito().getFechaEmision()))
                .documento(builderTipoDocumento(CodigoDocumento.NOTA_CREDITO))
                .tercero(proveedor)
                .fechaRegistro(LocalDate.now())
                .build();
    }


    public CpImpuestosEntity builderNotaDebito(Autorizacion model, NotaDebito documento,
                                               Long idData, Long idEmpresa,
                                               GeTerceroEntity proveedor) {
        return CpImpuestosEntity.builder()
                .idImpuestos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serie(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencial(documento.getInfoTributaria().getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(documento.getInfoNotaDebito().getFechaEmision()))
                .documento(builderTipoDocumento(CodigoDocumento.NOTA_DEBITO))
                .tercero(proveedor)
                .fechaRegistro(LocalDate.now())
                .build();
    }

    public VtRetencionesEntity builderRetencionRecibidaDos(Autorizacion model, ComprobanteRetencion documento,
                                                           Long idData, Long idEmpresa,
                                                           GeTerceroEntity cliente) {
        return VtRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serieRetencion(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialRetencion(documento.getInfoTributaria().getSecuencial())
                .fechaEmisionRetencion(DateUtils.toLocalDate(documento.getInfoCompRetencion().getFechaEmision()))
                .cliente(cliente)
                .valoresEntity(builderListValoresRetencion(documento.getDocSustento(), idData, idEmpresa))
                .claveAcceso(documento.getInfoTributaria().getClaveAcceso())
                .build();
    }


    public VtRetencionesEntity builderRetencionRecibidaUno(Autorizacion model, com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento,
                                                           Long idData, Long idEmpresa,
                                                           GeTerceroEntity cliente) {
        return VtRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serieRetencion(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialRetencion(documento.getInfoTributaria().getSecuencial())
                .fechaEmisionRetencion(DateUtils.toLocalDate(documento.getInfoCompRetencion().getFechaEmision()))
                .cliente(cliente)
                .valoresEntity(builderImpuestos(documento.getImpuesto(), idData, idEmpresa))
                .claveAcceso(documento.getInfoTributaria().getClaveAcceso())
                .build();
    }

    private List<VtRetencionesValoresEntity> builderImpuestos(List<Impuesto> impuesto, Long idData, Long idEmpresa) {
        if (Objects.isNull(impuesto)) return null;
        return impuesto.stream()
                .map(item -> builderImpuestoValor(item, idData, idEmpresa))
                .toList();
    }

    private VtRetencionesValoresEntity builderImpuestoValor(Impuesto impuesto, Long idData, Long idEmpresa) {
        return VtRetencionesValoresEntity.builder()
                .idRetencionValores(UUID.randomUUID())
                .codigo(impuesto.getCodigo())
                .codigoRetencion(impuesto.getCodigoRetencion())
                .porcentajeRetener(new BigDecimal(impuesto.getPorcentajeRetener()))
                .baseImponible(new BigDecimal(impuesto.getBaseImponible()))
                .valorRetenido(new BigDecimal(impuesto.getValorRetenido()))
                .codigoDocumento(impuesto.getCodDocSustento())
                .serie(impuesto.getNumDocSustento().substring(0, 6))
                .secuencial(impuesto.getNumDocSustento().substring(6, 9))
                .fechaEmision(DateUtils.toLocalDate(impuesto.getFechaEmisionDocSustento()))
                .idEmpresa(idEmpresa)
                .idData(idData)
                .build();
    }


    private List<VtRetencionesValoresEntity> builderListValoresRetencion(List<DocSustento> docSustento, Long idData, Long idEmpresa) {
        if (Objects.isNull(docSustento)) return null;
        return docSustento.stream()
                .flatMap(doc -> doc.getRetencion().stream()
                        .map(ret -> builderRetencion(ret, doc, idData, idEmpresa)))
                .toList();
    }

    private VtRetencionesValoresEntity builderRetencion(Retencion retencion, DocSustento docSustento, Long idData, Long idEmpresa) {
        return VtRetencionesValoresEntity.builder()
                .idRetencionValores(UUID.randomUUID())
                .codigo(retencion.getCodigo())
                .codigoRetencion(retencion.getCodigoRetencion())
                .porcentajeRetener(new BigDecimal(retencion.getPorcentajeRetener()))
                .baseImponible(new BigDecimal(retencion.getBaseImponible()))
                .valorRetenido(new BigDecimal(retencion.getValorRetenido()))
                .codigoDocumento(docSustento.getCodDocSustento())
                .serie(docSustento.getNumDocSustento().substring(0, 6))
                .secuencial(docSustento.getNumDocSustento().substring(6, 9))
                .fechaEmision(DateUtils.toLocalDate(docSustento.getFechaEmisionDocSustento()))
                .idEmpresa(idEmpresa)
                .idData(idData)
                .build();
    }


    public CpRetencionesEntity builderRetencionEmitidaDos(Autorizacion model, ComprobanteRetencion documento,
                                                          Long idData, Long idEmpresa, GeTerceroEntity proveedor,
                                                          BigDecimal total) {
        return CpRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serieRetencion(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialRetencion(documento.getInfoTributaria().getSecuencial())
                .fechaEmisionRetencion(DateUtils.toLocalDate(documento.getInfoCompRetencion().getFechaEmision()))
                .proveedor(proveedor)
                .referencias(builderReferencias(documento))
                .periodoFiscal(documento.getInfoCompRetencion().getPeriodoFiscal())
                .informacionAdicional(builderInformacionListAdicional(documento.getCampoAdicional()))
                .ambiente(Ambiente.obtenerAmbiente(Integer.parseInt(documento.getInfoTributaria().getAmbiente())))
                .claveAcceso(model.getNumeroAutorizacion())
                .codigoDocumento(documento.getInfoTributaria().getCodDoc())
                .anulada(Boolean.FALSE)
                .impresa(Boolean.FALSE)
                .concepto("")
                .email("")
                .estadoDocumento(EstadoDocumento.AUT)
                .formatoDocumento(FormatoDocumento.E)
                .total(total)
                .build();
    }


    public CpRetencionesEntity builderRetencionEmitidaUno(Autorizacion model,
                                                          com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento,
                                                          Long idData, Long idEmpresa, GeTerceroEntity proveedor,
                                                          BigDecimal total) {
        return CpRetencionesEntity.builder()
                .idRetencion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(model.getComprobante())
                .numeroAutorizacionRetencion(model.getNumeroAutorizacion())
                .fechaAutorizacion(DateUtils.toLocalDateTime(model.getFechaAutorizacion()))
                .serieRetencion(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialRetencion(documento.getInfoTributaria().getSecuencial())
                .fechaEmisionRetencion(DateUtils.toLocalDate(documento.getInfoCompRetencion().getFechaEmision()))
                .proveedor(proveedor)
                .referencias(builderImpuestosReferencias(documento))
                .periodoFiscal(documento.getInfoCompRetencion().getPeriodoFiscal())
                .informacionAdicional(builderInformacionListAdicional(documento.getCampoAdicional()))
                .ambiente(Ambiente.obtenerAmbiente(Integer.parseInt(documento.getInfoTributaria().getAmbiente())))
                .claveAcceso(model.getNumeroAutorizacion())
                .codigoDocumento(documento.getInfoTributaria().getCodDoc())
                .anulada(Boolean.FALSE)
                .impresa(Boolean.FALSE)
                .concepto("")
                .email("")
                .estadoDocumento(EstadoDocumento.AUT)
                .formatoDocumento(FormatoDocumento.E)
                .total(total)
                .build();
    }


    private List<InformacionAdicional> builderInformacionListAdicional(List<CampoAdicional> campoAdicional) {
        if (Objects.isNull(campoAdicional)) return null;
        return campoAdicional.stream()
                .map(this::builderInfoAdicional)
                .toList();
    }

    private InformacionAdicional builderInfoAdicional(CampoAdicional campoAdicional) {
        return InformacionAdicional.builder()
                .nombre(campoAdicional.getNombre())
                .valor(campoAdicional.getValor())
                .build();
    }

    private List<CpRetencionReferencias> builderReferencias(ComprobanteRetencion documento) {

        String numeroIdentificacion = documento.getInfoCompRetencion().getIdentificacionSujetoRetenido();
        return documento.getDocSustento()
                .stream()
                .map(doc -> builderReferencia(doc, numeroIdentificacion))
                .toList();
    }


    private List<CpRetencionReferencias> builderImpuestosReferencias(com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion documento) {

        String numeroIdentificacion = documento.getInfoCompRetencion().getIdentificacionSujetoRetenido();
        return documento.getImpuesto()
                .stream()
                .map(x -> builderImpuestoReferencia(x, numeroIdentificacion))
                .toList();
    }

    private CpRetencionReferencias builderImpuestoReferencia(Impuesto model, String numeroSusIdentificacion) {
        return CpRetencionReferencias.builder()
                .idReferencia(UUID.randomUUID())
                .serie(model.getNumDocSustento().substring(0, 6))
                .secuencial(model.getNumDocSustento().substring(6, 15))
                .numeroIdentificacion(numeroSusIdentificacion)
                .documento(builderDocumento(model.getCodDocSustento()))
                .impuestosCodigos(builderListImpuestoCodigosUno(model))
                .build();
    }

    private List<CpRetencionReferencias.ImpuestosCodigo> builderListImpuestoCodigosUno(Impuesto model) {
        List<CpRetencionReferencias.ImpuestosCodigo> lista = new ArrayList<>();

        CpRetencionReferencias.ImpuestosCodigo impuestosCodigo = CpRetencionReferencias
                .ImpuestosCodigo.builder()
                .codigo(model.getCodigo())
                .codigoRetencion(model.getCodigoRetencion())
                .baseImponible(new BigDecimal(model.getBaseImponible()))
                .porcentajeRetener(new BigDecimal(model.getPorcentajeRetener()))
                .valorRetenido(new BigDecimal(model.getValorRetenido()))
                .build();

        lista.add(impuestosCodigo);
        return lista;

    }


    private CpRetencionReferencias builderReferencia(DocSustento model, String numeroIdentificacion) {

        return CpRetencionReferencias.builder()
                .idReferencia(UUID.randomUUID())
                .serie(model.getNumDocSustento().substring(0, 6))
                .secuencial(model.getNumDocSustento().substring(6, 15))
                .numeroIdentificacion(numeroIdentificacion)
                .documento(builderDocumento(model.getCodDocSustento()))
                .impuestosCodigos(builderListImpuestoCodigos(model.getRetencion()))
                .build();
    }

    private List<CpRetencionReferencias.ImpuestosCodigo> builderListImpuestoCodigos(List<Retencion> retencion) {
        return retencion.stream()
                .map(this::builderImpuestoCodigos)
                .toList();
    }

    private CpRetencionReferencias.ImpuestosCodigo builderImpuestoCodigos(Retencion model) {
        return CpRetencionReferencias.ImpuestosCodigo.builder()
                .codigo(model.getCodigo())
                .codigoRetencion(model.getCodigoRetencion())
                .baseImponible(new BigDecimal(model.getBaseImponible()))
                .porcentajeRetener(new BigDecimal(model.getPorcentajeRetener()))
                .valorRetenido(new BigDecimal(model.getValorRetenido()))
                .build();
    }

    private TbDocumentoEntity builderDocumento(String codDocSustento) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(codDocSustento)
                .build();
    }


    public CpImpuestosValoresEntity builderValoresDocumento(TotalImpuesto model,
                                                            BigDecimal tarifa, Long idData, Long idEmpresa) {
        return CpImpuestosValoresEntity.builder()
                .idImpuestosValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(model.getBaseImponible().replace(" ", "")))
                .valor(new BigDecimal(model.getValor().replace(" ", "")))
                .tarifa(tarifa)
                .idEmpresa(idEmpresa)
                .idData(idData)
                .build();
    }

    public CpImpuestosValoresEntity builderValoresNotaDebito(com.calero.lili.core.comprobantes.objetosXml.notaDebito.Impuesto model,
                                                             BigDecimal tarifa, Long idData, Long idEmpresa) {
        return CpImpuestosValoresEntity.builder()
                .idImpuestosValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(model.getBaseImponible().replace(" ", "")))
                .valor(new BigDecimal(model.getValor().replace(" ", "")))
                .tarifa(tarifa)
                .idEmpresa(idEmpresa)
                .idData(idData)
                .build();
    }

    private TbDocumentoEntity builderTipoDocumento(CodigoDocumento item) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(item.getCodigoDocumento())
                .build();
    }

    public GeTerceroEntity builderProveedor(InfoTributaria model, Long idData) {
        return GeTerceroEntity.builder()
                .idTercero(UUID.randomUUID())
                .tipoIdentificacion("R")
                .numeroIdentificacion(model.getRuc())
                .direccion(model.getDirMatriz())
                .idData(idData)
                .tercero(model.getRazonSocial())
                .build();
    }

    public GeTerceroEntity builderClienteEmitidas(Long idData, String identificacion, String razonSocial,
                                                  String tipoIdentificacion, String direccion) {
        return GeTerceroEntity.builder()
                .idTercero(UUID.randomUUID())
                .tipoIdentificacion(tipoIdentificacion)
                .numeroIdentificacion(identificacion)
                .direccion(direccion)
                .idData(idData)
                .tercero(razonSocial)
                .build();
    }

    public GeTerceroEntity builderCliente(InfoTributaria model, Long idData) {
        return GeTerceroEntity.builder()
                .idTercero(UUID.randomUUID())
                .idData(idData)
                .tercero(model.getRazonSocial())
                .numeroIdentificacion(model.getRuc())
                .tipoIdentificacion("R")
                .direccion(model.getDirMatriz())
                .build();
    }

    public CpLiquidacionesReembolsosEntity builderLiquidacionReembolso(Autorizacion model, Factura documento) {

        return CpLiquidacionesReembolsosEntity.builder()
                .idLiquidacionReembolsos(UUID.randomUUID())
                .tipoIdentificacionReemb(TipoIdentificacion.obtenerTipoIdentificacion(documento.getInfoFactura()
                        .getTipoIdentificacionComprador()).name())
                .numeroIdentificacionReemb(documento.getInfoFactura().getIdentificacionComprador())
                .codigoDocumentoReemb("01")
                .serieReemb(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialReemb(documento.getInfoTributaria().getSecuencial())
                .comprobante(model.getComprobante())
                .numeroAutorizacionReemb(model.getNumeroAutorizacion())
                .reembolsosValores(builderListValoresReembolso(documento.getInfoFactura()))
                .fechaEmisionReemb(DateUtils.toLocalDate(documento.getInfoFactura().getFechaEmision()))
                .pais(TbPaisEntity.builder().codigoPais("593").build())
                .build();
    }

    public VtVentaReembolsosEntity builderVentaReembolso(Autorizacion model, Factura documento) {

        return VtVentaReembolsosEntity.builder()
                .idVentaReembolsos(UUID.randomUUID())
                .tipoIdentificacionReemb(TipoIdentificacion.obtenerTipoIdentificacion(documento.getInfoFactura()
                        .getTipoIdentificacionComprador()).name())
                .numeroIdentificacionReemb(documento.getInfoFactura().getIdentificacionComprador())
                .codigoDocumentoReemb("01")
                .serieReemb(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .secuencialReemb(documento.getInfoTributaria().getSecuencial())
                .comprobante(model.getComprobante())
                .numeroAutorizacionReemb(model.getNumeroAutorizacion())
                .reembolsosValores(builderListValoresVentaReembolso(documento.getInfoFactura()))
                .fechaEmisionReemb(DateUtils.toLocalDate(documento.getInfoFactura().getFechaEmision()))
                .pais(TbPaisEntity.builder().codigoPais("593").build())
                .build();
    }

    private List<CpLiquidacionesReembolsosValoresEntity> builderListValoresReembolso(InfoFactura infoFactura) {
        return infoFactura.getTotalImpuesto().stream()
                .map(this::builderReembolsoValores)
                .toList();
    }

    private CpLiquidacionesReembolsosValoresEntity builderReembolsoValores(TotalImpuesto model) {
        return CpLiquidacionesReembolsosValoresEntity.builder()
                .idLiquidacionValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(model.getBaseImponible()))
                .valor(new BigDecimal(model.getValor()))
                .build();
    }


    private List<VtVentaReembolsosValoresEntity> builderListValoresVentaReembolso(InfoFactura infoFactura) {
        return infoFactura.getTotalImpuesto().stream()
                .map(this::builderVentaReembolsoValores)
                .toList();
    }

    private VtVentaReembolsosValoresEntity builderVentaReembolsoValores(TotalImpuesto model) {
        return VtVentaReembolsosValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(model.getBaseImponible()))
                .valor(new BigDecimal(model.getValor()))
                .build();
    }

    public VtVentaEntity builderFacturaVenta(Long idData, Long idEmpresa,
                                             Factura documento,
                                             GeTerceroEntity cliente,
                                             String sucursal, String xmlComprobante) {
        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .comprobante(xmlComprobante)
                .tipoEmision(Integer.parseInt(documento.getInfoTributaria().getTipoEmision()))
                .numeroAutorizacion(documento.getInfoTributaria().getClaveAcceso())
                .claveAcceso(documento.getInfoTributaria().getClaveAcceso())
                .secuencial(documento.getInfoTributaria().getSecuencial())
                .codigoDocumento("18")
                .serie(documento.getInfoTributaria().getEstab() + documento.getInfoTributaria().getPtoEmi())
                .tipoVenta(TipoVenta.FAC.name())
                .estadoDocumento(EstadoDocumento.AUT)
                .fechaEmision(Objects.nonNull(documento.getInfoFactura().getFechaEmision())
                        ? DateUtils.toLocalDate(documento.getInfoFactura().getFechaEmision())
                        : null)
                .ambiente(Objects.nonNull(documento.getInfoTributaria().getAmbiente())
                        ? Ambiente.obtenerAmbiente(Integer.parseInt(documento.getInfoTributaria().getAmbiente()))
                        : null)
                .tipoIngreso(TipoIngreso.VL.name())
                .formatoDocumento(FormatoDocumento.E)
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .subtotal(new BigDecimal(documento.getInfoFactura().getTotalSinImpuestos()))
                .totalDescuento(new BigDecimal(documento.getInfoFactura().getTotalDescuento()))
                .total(new BigDecimal(documento.getInfoFactura().getImporteTotal()))
                .valoresEntity(builderListValoresFactura(documento.getInfoFactura().getTotalImpuesto(), idData, idEmpresa))
                .detalle(builderListDetalleFactura(documento.getDetalle(), idData, idEmpresa))
                .informacionAdicional(Objects.nonNull(documento.getCampoAdicional())
                        ? builderFacturaInfoAddicional(documento.getCampoAdicional())
                        : null)
                .formasPagoSri(Objects.nonNull(documento.getInfoFactura().getPago())
                        ? builderListFormasPago(documento.getInfoFactura().getPago())
                        : null)
                .tercero(cliente)
                .sucursal(sucursal)
                .anulada(Boolean.FALSE)
                .exportacion(builderExportacion(documento))
                .diasCredito(0)
                .fleteInternacional(BigDecimal.ZERO)
                .seguroInternacional(BigDecimal.ZERO)
                .gastosAduaneros(BigDecimal.ZERO)
                .gastosTransporteOtros(BigDecimal.ZERO)
                .build();


    }

    private VtVentaEntity.Exportacion builderExportacion(Factura documento) {
        var info = documento.getInfoFactura();

        boolean todosNull = Stream.of(
                info.getIncoTermFactura(),
                info.getLugarIncoTerm(),
                info.getPaisOrigen(),
                info.getPuertoEmbarque(),
                info.getPuertoDestino(),
                info.getPaisDestino(),
                info.getPaisAdquisicion(),
                info.getIncoTermTotalSinImpuestos()).allMatch(Objects::isNull);

        if (todosNull) {
            return null;
        }

        return VtVentaEntity.Exportacion.builder()
                .incoTermFactura(info.getIncoTermFactura())
                .lugarIncoTerm(info.getLugarIncoTerm())
                .paisOrigen(info.getPaisOrigen())
                .puertoEmbarque(info.getPuertoEmbarque())
                .puertoDestino(info.getPuertoDestino())
                .paisDestino(info.getPaisDestino())
                .paisAdquisicion(info.getPaisAdquisicion())
                .incoTermTotalSinImpuestos(info.getIncoTermTotalSinImpuestos())
                .build();
    }


    public VtVentaEntity builderEmitidaNotaCredito(Long idData, Long idEmpresa, NotaCredito notaCredito, GeTerceroEntity cliente,
                                                   String sucursal, BigDecimal totalImpuesto, String comprobante) {

        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(sucursal)
                .comprobante(comprobante)
                .tipoVenta(TipoVenta.NCR.name())
                .tipoEmision(Integer.parseInt(notaCredito.getInfoTributaria().getTipoEmision()))
                .serie(notaCredito.getInfoTributaria().getEstab() + notaCredito.getInfoTributaria().getPtoEmi())
                .secuencial(notaCredito.getInfoTributaria().getSecuencial())
                .claveAcceso(notaCredito.getInfoTributaria().getClaveAcceso())
                .numeroAutorizacion(notaCredito.getInfoTributaria().getClaveAcceso())
                .fechaEmision(Objects.nonNull(notaCredito.getInfoNotaCredito().getFechaEmision())
                        ? DateUtils.toLocalDate(notaCredito.getInfoNotaCredito().getFechaEmision())
                        : null)
                .tipoIngreso(TipoIngreso.VL.name())
                .codigoDocumento(notaCredito.getInfoTributaria().getCodDoc())
                .ambiente(Objects.nonNull(notaCredito.getInfoTributaria().getAmbiente())
                        ? Ambiente.obtenerAmbiente(Integer.parseInt(notaCredito.getInfoTributaria().getAmbiente()))
                        : null)
                .formatoDocumento(FormatoDocumento.E)
                .modCodigoDocumento(notaCredito.getInfoNotaCredito().getCodDocModificado())
                .valoresEntity(builderListValoresCredito(notaCredito.getInfoNotaCredito().getTotalImpuesto(), idData, idEmpresa))
                .detalle(builderListDetalleNotaCredito(notaCredito.getDetalle(), idData, idEmpresa))
                .informacionAdicional(Objects.nonNull(notaCredito.getCampoAdicional())
                        ? builderFacturaInfoAddicional(notaCredito.getCampoAdicional())
                        : null)
                .tercero(cliente)
                .subtotal(new BigDecimal(notaCredito.getInfoNotaCredito().getTotalSinImpuestos()))
                .totalDescuento(BigDecimal.ZERO)
                .total(new BigDecimal(notaCredito.getInfoNotaCredito().getValorModificacion()))
                .totalImpuesto(totalImpuesto)
                .build();

    }

    private List<VtVentaDetalleEntity> builderListDetalleNotaCredito(List<com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle> detalle,
                                                                     Long idData, Long idEmpresa) {
        return detalle.stream()
                .map(item -> builderDetalleNotaCredito(item, idData, idEmpresa))
                .toList();
    }

    private VtVentaDetalleEntity builderDetalleNotaCredito(com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle detalle, Long idData, Long idEmpresa) {
        return VtVentaDetalleEntity.builder()
                .idVentaDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(detalle.getCodigoInterno())
                .codigoAuxiliar(detalle.getCodigoAdicional())
                .cantidad(new BigDecimal(detalle.getCantidad()))
                .precioUnitario(new BigDecimal(detalle.getPrecioUnitario()))
                .descuento(new BigDecimal(detalle.getDescuento()))
                .impuesto(builderListImpuestoFactura(detalle.getImpuesto()))
                .build();
    }

    private List<VtVentaValoresEntity> builderListValoresCredito(List<TotalImpuesto> totalImpuesto, Long idData, Long idEmpresa) {
        return totalImpuesto.stream()
                .map(item -> builderValoresNotaCredito(item, idData, idEmpresa))
                .toList();
    }

    private VtVentaValoresEntity builderValoresNotaCredito(TotalImpuesto totalImpuesto, Long idData, Long idEmpresa) {
        return VtVentaValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valor(new BigDecimal(totalImpuesto.getValor()))
                .codigoPorcentaje(totalImpuesto.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(totalImpuesto.getBaseImponible()))
                .codigo(totalImpuesto.getCodigo())
                .build();
    }


    private List<FormasPagoSri> builderListFormasPago(List<Pago> pago) {
        return pago.stream()
                .map(this::builderFacturaPago)
                .toList();
    }

    private FormasPagoSri builderFacturaPago(Pago pago) {
        return FormasPagoSri.builder()
                .unidadTiempo(pago.getUnidadTiempo())
                .formaPago(pago.getFormaPago())
                .total(new BigDecimal(pago.getTotal()))
                .plazo(pago.getPlazo())
                .build();
    }


    private List<InformacionAdicional> builderFacturaInfoAddicional(List<CampoAdicional> campoAdicional) {
        return campoAdicional.stream()
                .map(this::builderInfoAdicionalFactura)
                .toList();
    }

    private InformacionAdicional builderInfoAdicionalFactura(CampoAdicional campoAdicional) {
        return InformacionAdicional.builder()
                .nombre(campoAdicional.getNombre())
                .valor(campoAdicional.getValor())
                .build();
    }

    private List<VtVentaDetalleEntity> builderListDetalleFactura(List<Detalle> detalle, Long idData,
                                                                 Long idEmpresa) {
        return detalle.stream()
                .map(x -> builderDetalleFactura(x, idData, idEmpresa))
                .toList();
    }

    private VtVentaDetalleEntity builderDetalleFactura(Detalle detalle, Long idData, Long idEmpresa) {
        return VtVentaDetalleEntity.builder()
                .idVentaDetalle(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .codigoPrincipal(detalle.getCodigoPrincipal())
                .codigoAuxiliar(detalle.getCodigoAuxiliar())
                .cantidad(new BigDecimal(detalle.getCantidad()))
                .precioUnitario(new BigDecimal(detalle.getPrecioUnitario()))
                .descuento(new BigDecimal(detalle.getDescuento()))
                .impuesto(builderListImpuestoFactura(detalle.getImpuesto()))
                .build();
    }

    private List<VtVentaDetalleEntity.Impuestos> builderListImpuestoFactura(List<com.calero.lili.core.comprobantes.objetosXml.factura.Impuesto> impuesto) {
        return impuesto.stream()
                .map(this::builderImpuestoFactura)
                .toList();
    }

    private VtVentaDetalleEntity.Impuestos builderImpuestoFactura(com.calero.lili.core.comprobantes.objetosXml.factura.Impuesto impuesto) {
        return VtVentaDetalleEntity.Impuestos.builder()
                .codigo(impuesto.getCodigo())
                .codigoPorcentaje(impuesto.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(impuesto.getBaseImponible()))
                .valor(new BigDecimal(impuesto.getValor()))
                .tarifa(new BigDecimal(impuesto.getTarifa()))
                .build();
    }

    private List<VtVentaValoresEntity> builderListValoresFactura(List<TotalImpuesto> totalImpuesto,
                                                                 Long idData, Long idEmpresa) {
        return totalImpuesto.stream()
                .map(item -> builderFacturaValor(item, idData, idEmpresa))
                .toList();
    }

    private VtVentaValoresEntity builderFacturaValor(TotalImpuesto totalImpuesto, Long idData, Long idEmpresa) {
        return VtVentaValoresEntity.builder()
                .idVentaValores(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valor(new BigDecimal(totalImpuesto.getValor()))
                .codigoPorcentaje(totalImpuesto.getCodigoPorcentaje())
                .baseImponible(new BigDecimal(totalImpuesto.getBaseImponible()))
                .codigo(totalImpuesto.getCodigo())
                .build();
    }

    public GeItemEntity builderFacturaItem(Detalle detalle, Long idData, Long idEmpresa) {
        return GeItemEntity.builder()
                .idItem(UUID.randomUUID())
                .idEmpresa(idEmpresa)
                .idData(idData)
                .codigoPrincipal(detalle.getCodigoPrincipal())
                .codigoAuxiliar(detalle.getCodigoAuxiliar())
                .build();
    }

    public GeItemEntity builderNotaCreditoItem(com.calero.lili.core.comprobantes.objetosXml.notaCredito.Detalle detalle,
                                               Long idData, Long idEmpresa) {
        return GeItemEntity.builder()
                .idItem(UUID.randomUUID())
                .idEmpresa(idEmpresa)
                .idData(idData)
                .codigoPrincipal(detalle.getCodigoInterno())
                .codigoAuxiliar(detalle.getCodigoAdicional())
                .build();
    }
}
