package com.calero.lili.core.modCompras.modCompras.builder;


import com.calero.lili.core.modCompras.modCompras.CpComprasEntity;
import com.calero.lili.core.modCompras.modCompras.dto.CompraRequestDto;
import com.calero.lili.core.modCompras.modCompras.dto.GetDto;
import com.calero.lili.core.modCompras.modCompras.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CpComprasBuilder {

    private final CpComprasValoresBuilder cpComprasValoresBuilder;
    private final CpComprasDetalleBuilder cpComprasDetalleBuilder;

    public CpComprasEntity builderEntity(CompraRequestDto model, Long idData, Long idEmpresa) {
        return CpComprasEntity.builder()
                .valoresEntity(cpComprasValoresBuilder.builderListValores(model.getValores(), idData, idEmpresa))
                .detalle(cpComprasDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
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
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modTipoVenta(model.getModTipoVenta())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idCompra(UUID.randomUUID())
                .build();
    }


    public CpComprasEntity builderUpdateEntity(CompraRequestDto model, CpComprasEntity item) {
        return CpComprasEntity.builder()
                .valoresEntity(cpComprasValoresBuilder.builderListValores(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(cpComprasDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdData()))
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaAnulacion(item.getFechaAnulacion())
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
                .anulada(item.getAnulada())
                .impresa(model.getImpresa())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modTipoVenta(model.getModTipoVenta())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idCompra(item.getIdCompra())
                .build();
    }


    public GetDto builderGetDto(CpComprasEntity model) {
        return GetDto.builder()
                .idCompra(model.getIdCompra())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .idTercero(model.getTercero().getIdTercero())
                .relacionado(model.getRelacionado())
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
                .valores(cpComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .relacionado(model.getRelacionado())
                .total(model.getTotal())
                .totalDescuento(model.getTotalDescuento())
                .build();
    }

    public GetListDto builderListDto(CpComprasEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idCompra(model.getIdCompra())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(model.getFechaEmision().toString())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(model.getTercero().getIdTercero())
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .valores(cpComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .build();
    }


    public GetListDto builderGetListDto(CpComprasEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idCompra(model.getIdCompra())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .fechaEmision(model.getFechaEmision().toString())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(model.getTercero().getIdTercero())
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .valores(cpComprasValoresBuilder.builderResponseListValores(model.getValoresEntity()))
                .detalle(cpComprasDetalleBuilder.builderResponseList(model.getDetalle()))
                .build();
    }


}
