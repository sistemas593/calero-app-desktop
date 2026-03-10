package com.calero.lili.api.modVentasPedidos.builder;

import com.calero.lili.api.builder.InformacionAdicionalBuilder;
import com.calero.lili.api.modVentasPedidos.VtPedidoEntity;
import com.calero.lili.api.modVentasPedidos.dto.CreationComprasPedidosRequestDto;
import com.calero.lili.api.modVentasPedidos.dto.GetDto;
import com.calero.lili.api.modVentasPedidos.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtPedidoBuilder {

    private final VtPedidoDetalleBuilder vtPedidoDetalleBuilder;
    private final VtPedidoValoresBuilder vtPedidoValoresBuilder;
    private final InformacionAdicionalBuilder informacionAdicionalBuilder;

    public VtPedidoEntity builderEntity(CreationComprasPedidosRequestDto model, Long idData, Long idEmpresa) {
        return VtPedidoEntity.builder()
                .idPedido(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toLocalDate(model.getFechaAnulacion()) : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento()) : null)
                .emailEstado(model.getEmailEstado())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .idVendedor(model.getIdVendedor())
                .anulada(Objects.nonNull(model.getAnulada())
                        ? model.getAnulada()
                        : Boolean.FALSE)
                .impresa(Objects.nonNull(model.getImpresa())
                        ? model.getImpresa()
                        : Boolean.FALSE)
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .concepto(model.getConcepto())
                .detalle(vtPedidoDetalleBuilder.builderList(model.getDetalle()))
                .valoresEntity(vtPedidoValoresBuilder.builderList(model.getValores()))
                .build();

    }


    public VtPedidoEntity builderUpdateEntity(CreationComprasPedidosRequestDto model, VtPedidoEntity item) {
        return VtPedidoEntity.builder()
                .idPedido(item.getIdPedido())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toLocalDate(model.getFechaAnulacion()) : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento()) : null)
                .emailEstado(model.getEmailEstado())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .idVendedor(model.getIdVendedor())
                .anulada(Objects.nonNull(model.getAnulada())
                        ? model.getAnulada()
                        : Boolean.FALSE)
                .impresa(Objects.nonNull(model.getImpresa())
                        ? model.getImpresa()
                        : Boolean.FALSE)
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .concepto(model.getConcepto())
                .detalle(vtPedidoDetalleBuilder.builderList(model.getDetalle()))
                .valoresEntity(vtPedidoValoresBuilder.builderList(model.getValores()))
                .build();

    }

    public GetDto builderResponse(VtPedidoEntity model) {
        return GetDto.builder()
                .idPedido(model.getIdPedido())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaEmision(model.getFechaEmision().toString())
                .tipoIdentificacion(model.getTipoIdentificacion().name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .email(model.getEmail())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .numeroItems(model.getNumeroItems())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .valores(vtPedidoValoresBuilder.builderListResponse(model.getValoresEntity()))
                .detalle(vtPedidoDetalleBuilder.builderListResponse(model.getDetalle()))
                .build();
    }

    public GetListDto builderPaginateResponse(VtPedidoEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idPedido(model.getIdPedido())
                .secuencial(model.getSecuencial())
                .fechaEmision(model.getFechaEmision().toString())
                .terceroNombre(model.getTerceroNombre())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .numeroItems(model.getNumeroItems())
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .emailEstado(model.getEmailEstado())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .valores(vtPedidoValoresBuilder.builderListResponse(model.getValoresEntity()))
                .detalle(vtPedidoDetalleBuilder.builderListResponse(model.getDetalle()))
                .build();
    }


}
