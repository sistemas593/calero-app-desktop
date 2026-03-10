package com.calero.lili.api.modVentasCotizaciones.builder;

import com.calero.lili.api.builder.InformacionAdicionalBuilder;
import com.calero.lili.api.modVentasCotizaciones.VtCotizacionEntity;
import com.calero.lili.api.modVentasCotizaciones.dto.CreationVentasCotizacionesRequestDto;
import com.calero.lili.api.modVentasCotizaciones.dto.GetDto;
import com.calero.lili.api.modVentasCotizaciones.dto.GetListDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtCotizacionBuilder {

    private final InformacionAdicionalBuilder informacionAdicionalBuilder;
    private final VtCotizacionValoresBuilder vtCotizacionValoresBuilder;
    private final VtCotizacionDetalleBuilder vtCotizacionDetalleBuilder;

    public VtCotizacionEntity builderEntity(CreationVentasCotizacionesRequestDto model, Long idData, Long idEmpresa) {
        return VtCotizacionEntity.builder()
                .idCotizacion(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtCotizacionValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .detalle(vtCotizacionDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toLocalDate(model.getFechaAnulacion())
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .emailEstado(model.getEmailEstado())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .idVendedor(model.getIdVendedor())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .concepto(model.getConcepto())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .build();
    }


    public VtCotizacionEntity builderUpdateEntity(CreationVentasCotizacionesRequestDto model, VtCotizacionEntity item) {
        return VtCotizacionEntity.builder()
                .idCotizacion(item.getIdCotizacion())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtCotizacionValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(vtCotizacionDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toLocalDate(model.getFechaAnulacion())
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toLocalDate(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .emailEstado(model.getEmailEstado())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .idVendedor(model.getIdVendedor())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .concepto(model.getConcepto())
                .fechaEmision(Objects.nonNull(model.getFechaEmision())
                        ? DateUtils.toLocalDate(model.getFechaEmision())
                        : null)
                .build();
    }



    public GetDto builderResponse(VtCotizacionEntity model) {
        return GetDto.builder()
                .idCotizacion(model.getIdCotizacion())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? DateUtils.toString(model.getFechaAnulacion())
                        : null)
                .formaPago(model.getFormaPago())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(Objects.nonNull(model.getFechaVencimiento())
                        ? DateUtils.toString(model.getFechaVencimiento())
                        : null)
                .cuotas(model.getCuotas())
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .tipoIdentificacion(model.getTipoIdentificacion().name())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .terceroNombre(model.getTerceroNombre())
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .detalle(vtCotizacionDetalleBuilder.builderListDto(model.getDetalle()))
                .valores(vtCotizacionValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .build();
    }

    public GetListDto builderListResponse(VtCotizacionEntity model) {
        return GetListDto.builder()
                .idCotizacion(model.getIdCotizacion())
                .sucursal(model.getSucursal())
                .secuencial(model.getSecuencial())
                .diasCredito(model.getDiasCredito())
                .fechaVencimiento(model.getFechaVencimiento())
                .cuotas(model.getCuotas())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(model.getAnulada())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .terceroNombre(model.getTerceroNombre())
                .fechaEmision(model.getFechaEmision().toString())
                .valores(vtCotizacionValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .detalle(vtCotizacionDetalleBuilder.builderListDto(model.getDetalle()))
                .build();
    }


}
