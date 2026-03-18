package com.calero.lili.core.modVentas.notasCredito.builder;

import com.calero.lili.core.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.core.modVentas.notasCredito.dto.GetNotaCreditoDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtNotasCreditoBuilder {


    private final VtNotasCreditoValoresBuilder vtNotasCreditoValoresBuilder;
    private final VtNotaCreditoDetalleBuilder vtNotaCreditoDetalleBuilder;
    private final InformacionAdicionalBuilder informacionAdicionalBuilder;



    public VtVentaEntity builderEntity(CreationNotaCreditoRequestDto model, Long idData, Long idEmpresa) {
        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtNotasCreditoValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .detalle(vtNotaCreditoDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.NCR.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(Objects.nonNull(model.getTipoIngreso())
                        ? model.getTipoIngreso().name()
                        : TipoIngreso.VL.name())
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
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .impresa(model.getImpresa())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())

                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento()) ? model.getCodigoDocumento() : "04")
                .modCodigoDocumento(model.getModCodigoDocumento())
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toLocalDate(model.getModFechaEmision())
                        : null)
                .concepto(model.getConcepto())
                .fleteInternacional(BigDecimal.ZERO)
                .gastosAduaneros(BigDecimal.ZERO)
                .gastosTransporteOtros(BigDecimal.ZERO)
                .guiaRemisionSerie("")
                .guiaRemisionSecuencial("")
                .seguroInternacional(BigDecimal.ZERO)
                .anulada(Boolean.FALSE)
                .build();
    }


    public VtVentaEntity builderUpdateEntity(CreationNotaCreditoRequestDto model, VtVentaEntity item) {
        return VtVentaEntity.builder()
                .idVenta(item.getIdVenta())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtNotasCreditoValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(vtNotaCreditoDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.NCR.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(Objects.nonNull(model.getTipoIngreso())
                        ? model.getTipoIngreso().name()
                        : TipoIngreso.VL.name())
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
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .impresa(model.getImpresa())
                .tipoIdentificacion(Objects.nonNull(model.getTipoIdentificacion())
                        ? model.getTipoIdentificacion().name()
                        : TipoIdentificacion.C.name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoCliente(model.getTipoCliente())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento()) ? model.getCodigoDocumento() : "04")
                .reembolsosEntity(new ArrayList<>()) // Validacion para evitar error del null
                .modCodigoDocumento(model.getModCodigoDocumento())
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toLocalDate(model.getModFechaEmision())
                        : null)
                .concepto(model.getConcepto())
                .fleteInternacional(BigDecimal.ZERO)
                .gastosAduaneros(BigDecimal.ZERO)
                .gastosTransporteOtros(BigDecimal.ZERO)
                .guiaRemisionSerie("")
                .guiaRemisionSecuencial("")
                .seguroInternacional(BigDecimal.ZERO)
                .anulada(item.getAnulada())
                .build();
    }


    public GetNotaCreditoDto builderResponse(VtVentaEntity model) {
        return GetNotaCreditoDto.builder()
                .valores(vtNotasCreditoValoresBuilder.builderListValoresDto(model.getValoresEntity()))
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
                .detalle(vtNotaCreditoDetalleBuilder.builderListDto(model.getDetalle()))
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .fechaAutorizacion(model.getFechaAutorizacion())
                .claveAcceso(model.getClaveAcceso())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(model.getFechaEmision().toString())
                .modCodigoDocumento(model.getModCodigoDocumento())
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toString(model.getModFechaEmision())
                        : null)
                .concepto(model.getConcepto())
                .formatoDocumento(model.getFormatoDocumento())
                .ambiente(model.getAmbiente())
                .build();
    }
}
