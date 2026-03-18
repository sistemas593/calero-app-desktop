package com.calero.lili.core.modVentas.facturas.builder;

import com.calero.lili.core.builder.FormasPagoBuilder;
import com.calero.lili.core.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.facturas.dto.GetFacturaDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtFacturasBuilder {


    private final VtVentaValoresBuilder vtVentaValoresBuilder;
    private final VtVentaDetalleBuilder vtVentaDetalleBuilder;
    private final VtReembolsosResponseBuilder vtReembolsosResponseBuilder;
    private final InformacionAdicionalBuilder informacionAdicionalBuilder;
    private final FormasPagoBuilder formasPagoBuilder;


    public VtVentaEntity builderEntity(CreationFacturaRequestDto model, Long idData, Long idEmpresa) {
        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtVentaValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .detalle(vtVentaDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .sustitutivaGuiaRemision(builderSustitutivaGuiaRemision(model.getSustitutivaGuiaRemision()))
                .exportacion(builderExportacion(model.getExportacion()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.FAC.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso().name())
                .codigoDocumento(model.getCodigoDocumento())
                .liquidar(Objects.nonNull(model.getLiquidar())
                        ? model.getLiquidar().name()
                        : Liquidar.N.name())
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(Boolean.FALSE)
                .impresa(model.getImpresa())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie("")
                .modSecuencial("")
                .fleteInternacional(model.getFleteInternacional())
                .seguroInternacional(model.getSeguroInternacional())
                .gastosAduaneros(model.getGastosAduaneros())
                .gastosTransporteOtros(model.getGastosTransporteOtros())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .totalImpuesto(model.getTotalImpuesto())
                .build();
    }


    public VtVentaEntity builderUpdateEntity(CreationFacturaRequestDto model, VtVentaEntity item) {
        return VtVentaEntity.builder()
                .idVenta(item.getIdVenta())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtVentaValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(vtVentaDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .sustitutivaGuiaRemision(builderSustitutivaGuiaRemision(model.getSustitutivaGuiaRemision()))
                .exportacion(builderExportacion(model.getExportacion()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.FAC.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso().name())
                .codigoDocumento(model.getCodigoDocumento())
                .liquidar(Objects.nonNull(model.getLiquidar())
                        ? model.getLiquidar().name()
                        : Liquidar.N.name())
                .fechaAnulacion(item.getFechaAnulacion())
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                //  .idVendedor(model.getIdVendedor())
                .anulada(item.getAnulada())
                .impresa(model.getImpresa())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie("")
                .modSecuencial("")
                .fleteInternacional(model.getFleteInternacional())
                .seguroInternacional(model.getSeguroInternacional())
                .gastosAduaneros(model.getGastosAduaneros())
                .gastosTransporteOtros(model.getGastosTransporteOtros())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .totalImpuesto(model.getTotalImpuesto())
                .reembolsosEntity(item.getReembolsosEntity())
                .anulada(item.getAnulada())
                .build();
    }


    private VtVentaEntity.Exportacion builderExportacion(CreationFacturaRequestDto.Exportacion model) {
        if (Objects.isNull(model)) return null;
        return VtVentaEntity.Exportacion.builder()
                .comercioExterior(model.getComercioExterior())
                .incoTermFactura(model.getIncoTermFactura())
                .lugarIncoTerm(model.getLugarIncoTerm())
                .paisOrigen(model.getPaisOrigen())
                .puertoEmbarque(model.getPuertoEmbarque())
                .puertoDestino(model.getPuertoDestino())
                .paisDestino(model.getPaisDestino())
                .paisAdquisicion(model.getPaisAdquisicion())
                .incoTermTotalSinImpuestos(model.getIncoTermTotalSinImpuestos())
                .build();
    }


    private VtVentaEntity.SustitutivaGuiaRemision builderSustitutivaGuiaRemision(CreationFacturaRequestDto.SustitutivaGuiaRemision model) {

        if (Objects.isNull(model)) return null;
        return VtVentaEntity.SustitutivaGuiaRemision.builder()
                .idTransportista(model.getIdTransportista())
                .dirPartida(model.getDirPartida())
                .dirDestinatario(model.getDirDestinatario())
                .fechaIniTransporte(model.getFechaIniTransporte())
                .fechaFinTransporte(model.getFechaFinTransporte())
                .destinos(builderListDestinos(model.getDestinos()))
                .build();
    }


    private List<VtVentaEntity.Destino> builderListDestinos(List<CreationFacturaRequestDto.Destino> list) {
        return list.stream()
                .map(this::builderDestino)
                .toList();
    }

    private VtVentaEntity.Destino builderDestino(CreationFacturaRequestDto.Destino model) {
        return VtVentaEntity.Destino.builder()
                .motivoTraslado(model.getMotivoTraslado())
                .ruta(model.getRuta())
                .docAduaneroUnico(model.getDocAduaneroUnico())
                .codEstabDestino(model.getCodEstabDestino())
                .build();
    }


    private GetFacturaDto.SustitutivaGuiaRemision builderSustitutivaGuiaRemisionDto(VtVentaEntity.SustitutivaGuiaRemision model,
                                                                                    GeTerceroEntity item) {
        if (Objects.isNull(model)) return null;
        return GetFacturaDto.SustitutivaGuiaRemision.builder()
                .idTransportista(model.getIdTransportista())
                .dirPartida(model.getDirPartida())
                .dirDestinatario(model.getDirDestinatario())
                .fechaIniTransporte(model.getFechaIniTransporte())
                .fechaFinTransporte(model.getFechaFinTransporte())
                .razonSocialTransportista(item.getTercero())
                .tipoIdentificacionTransportista(item.getTipoIdentificacion())
                .numeroIdentificacionTransportista(item.getNumeroIdentificacion())
                .placa(item.getPlaca())
                .destinos(builderListDestinosDto(model.getDestinos()))
                .build();
    }


    private List<GetFacturaDto.Destino> builderListDestinosDto(List<VtVentaEntity.Destino> list) {
        if (Objects.isNull(list)) return new ArrayList<>();
        return list.stream()
                .map(this::builderDestinoDto)
                .toList();
    }

    private GetFacturaDto.Destino builderDestinoDto(VtVentaEntity.Destino model) {
        return GetFacturaDto.Destino.builder()
                .motivoTraslado(model.getMotivoTraslado())
                .ruta(model.getRuta())
                .docAduaneroUnico(model.getDocAduaneroUnico())
                .codEstabDestino(model.getCodEstabDestino())
                .build();
    }

    private GetFacturaDto.Exportacion builderExportacionDto(VtVentaEntity.Exportacion model) {
        if (Objects.isNull(model)) return null;
        return GetFacturaDto.Exportacion.builder()
                .comercioExterior(model.getComercioExterior())
                .incoTermFactura(model.getIncoTermFactura())
                .lugarIncoTerm(model.getLugarIncoTerm())
                .paisOrigen(model.getPaisOrigen())
                .puertoEmbarque(model.getPuertoEmbarque())
                .puertoDestino(model.getPuertoDestino())
                .paisDestino(model.getPaisDestino())
                .paisAdquisicion(model.getPaisAdquisicion())
                .incoTermTotalSinImpuestos(model.getIncoTermTotalSinImpuestos())
                .build();
    }


    public GetFacturaDto builderResponse(VtVentaEntity model) {
        return GetFacturaDto.builder()
                .valores(vtVentaValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .reembolsos(vtReembolsosResponseBuilder.builderListDto(model.getReembolsosEntity()))
                .idTercero(model.getTercero().getIdTercero())
                .idVenta(model.getIdVenta())
                .sucursal(model.getSucursal())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .secuencial(model.getSecuencial())
                .codigoDocumento(model.getCodigoDocumento())
                .tipoIngreso(model.getTipoIngreso())
                .liquidar(model.getLiquidar())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .terceroNombre(model.getTerceroNombre())
                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .email(model.getEmail())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .numeroItems(model.getNumeroItems())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .detalle(vtVentaDetalleBuilder.builderListDto(model.getDetalle()))
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderListDto(model.getFormasPagoSri()))
                .fechaAutorizacion(model.getFechaAutorizacion())
                .claveAcceso(model.getClaveAcceso())
                .exportacion(builderExportacionDto(model.getExportacion()))
                .fleteInternacional(model.getFleteInternacional())
                .seguroInternacional(model.getSeguroInternacional())
                .gastosAduaneros(model.getGastosAduaneros())
                .gastosTransporteOtros(model.getGastosTransporteOtros())
                .fechaEmision(model.getFechaEmision().toString())
                .formatoDocumento(model.getFormatoDocumento())
                .ambiente(model.getAmbiente())
                .totalImpuesto(model.getTotalImpuesto())
                .build();
    }

    public GetFacturaDto builderResponseWithGuiaSustitutiva(VtVentaEntity model, GeTerceroEntity item) {
        return GetFacturaDto.builder()
                .valores(vtVentaValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .reembolsos(vtReembolsosResponseBuilder.builderListDto(model.getReembolsosEntity()))
                .idTercero(model.getTercero().getIdTercero())
                .idVenta(model.getIdVenta())
                .sucursal(model.getSucursal())
                .tipoVenta(model.getTipoVenta())
                .serie(model.getSerie())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .secuencial(model.getSecuencial())
                .codigoDocumento(model.getCodigoDocumento())
                .tipoIngreso(model.getTipoIngreso())
                .liquidar(model.getLiquidar())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .terceroNombre(model.getTerceroNombre())
                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .email(model.getEmail())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .numeroItems(model.getNumeroItems())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .detalle(vtVentaDetalleBuilder.builderListDto(model.getDetalle()))
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderListDto(model.getFormasPagoSri()))
                .fechaAutorizacion(model.getFechaAutorizacion())
                .claveAcceso(model.getClaveAcceso())
                .exportacion(builderExportacionDto(model.getExportacion()))
                .fleteInternacional(model.getFleteInternacional())
                .seguroInternacional(model.getSeguroInternacional())
                .gastosAduaneros(model.getGastosAduaneros())
                .gastosTransporteOtros(model.getGastosTransporteOtros())
                .sustitutivaGuiaRemision(builderSustitutivaGuiaRemisionDto(model.getSustitutivaGuiaRemision(), item))
                .fechaEmision(model.getFechaEmision().toString())
                .formatoDocumento(model.getFormatoDocumento())
                .ambiente(model.getAmbiente())
                .totalImpuesto(model.getTotalImpuesto())
                .build();
    }

}
