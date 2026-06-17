package com.calero.lili.core.modComprasOrden.builder;


import com.calero.lili.core.modComprasOrden.CpOrdenComprasEntity;
import com.calero.lili.core.modComprasOrden.dto.GetComprasOrdenDto;
import com.calero.lili.core.modComprasOrden.dto.GetComprasOrdenListDto;
import com.calero.lili.core.modComprasOrden.dto.OrdenCompraRequestDto;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CpOrdenComprasBuilder {

    private final CpOrdenComprasValoresBuilder cpOrdenComprasValoresBuilder;
    private final CpOrdenComprasDetalleBuilder cpOrdenComprasDetalleBuilder;

    public CpOrdenComprasEntity builderEntity(OrdenCompraRequestDto model, Long idData, Long idEmpresa) {
        return CpOrdenComprasEntity.builder()
                .valoresEntity(cpOrdenComprasValoresBuilder.builderListValores(model.getValores(), idData, idEmpresa))
                .detalle(cpOrdenComprasDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(Boolean.FALSE)
                .impresa(model.getImpresa())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idCompra(UUID.randomUUID())
                .anulada(Boolean.FALSE)
                .build();
    }


    public CpOrdenComprasEntity builderUpdateEntity(OrdenCompraRequestDto model, CpOrdenComprasEntity item) {
        return CpOrdenComprasEntity.builder()
                .valoresEntity(cpOrdenComprasValoresBuilder.builderListValores(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(cpOrdenComprasDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdData()))
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaAnulacion(item.getFechaAnulacion())
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : item.getFechaVencimiento())
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(Boolean.FALSE)
                .impresa(model.getImpresa())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idCompra(item.getIdCompra())
                .anulada(item.getAnulada())
                .build();
    }


    public GetComprasOrdenDto builderGetDto(CpOrdenComprasEntity model) {
        return GetComprasOrdenDto.builder()
                .idCompra(model.getIdCompra())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .concepto(model.getConcepto())
                .terceroDatos(builderResponseTerceroOne(model.getTercero()))
                .valores(cpOrdenComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpOrdenComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .build();
    }

    private GetComprasOrdenDto.TercerosDatos builderResponseTerceroOne(GeTerceroEntity tercero) {
        if (Objects.isNull(tercero)) return null;
        return GetComprasOrdenDto.TercerosDatos.builder()
                .idTercero(tercero.getIdTercero())
                .tercero(tercero.getTercero())
                .build();
    }


    public GetComprasOrdenListDto builderListDto(CpOrdenComprasEntity model) {
        return GetComprasOrdenListDto.builder()
                .idCompra(model.getIdCompra())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .concepto(model.getConcepto())
                .terceroDatos(builderResponseTerceroList(model.getTercero()))
                .valores(cpOrdenComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpOrdenComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .build();
    }

    private GetComprasOrdenListDto.TercerosDatos builderResponseTerceroList(GeTerceroEntity tercero) {
        if (Objects.isNull(tercero)) return null;
        return GetComprasOrdenListDto.TercerosDatos.builder()
                .idTercero(tercero.getIdTercero())
                .tercero(tercero.getTercero())
                .build();
    }


    public GetComprasOrdenListDto builderGetListDto(CpOrdenComprasEntity model) {
        return GetComprasOrdenListDto.builder()
                .idCompra(model.getIdCompra())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .concepto(model.getConcepto())
                .terceroDatos(builderResponseTerceroList(model.getTercero()))
                .valores(cpOrdenComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpOrdenComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .build();
    }


    public GetComprasOrdenListDto builderAnuladoGetListDto(CpOrdenComprasEntity model) {
        return GetComprasOrdenListDto.builder()
                .idCompra(model.getIdCompra())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? model.getFechaVencimiento().toString()
                        : null)
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .concepto(model.getConcepto())
                .terceroDatos(builderResponseTerceroList(model.getTercero()))
                .valores(cpOrdenComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpOrdenComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .build();
    }



}
