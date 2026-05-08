package com.calero.lili.core.modVentas.notasDebito.builder;

import com.calero.lili.core.builder.FormasPagoBuilder;
import com.calero.lili.core.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.enums.TipoVenta;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import com.calero.lili.core.modVentas.notasDebito.dto.GetNotaDebitoDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtNotasDebitoBuilder {


    private final VtNotasDebitoValoresBuilder vtNotasDebitoValoresBuilder;
    private final VtNotaDebitoDetalleBuilder vtNotaDebitoDetalleBuilder;
    private final InformacionAdicionalBuilder informacionAdicionalBuilder;
    private final FormasPagoBuilder formasPagoBuilder;


    public VtVentaEntity builderEntity(CreationNotaDebitoRequestDto model, Long idData, Long idEmpresa) {
        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtNotasDebitoValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .detalle(vtNotaDebitoDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.NDB.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(Objects.nonNull(model.getTipoIngreso())
                        ? model.getTipoIngreso().name()
                        : TipoIngreso.VL.name())
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
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .impresa(model.getImpresa())
                .relacionado(model.getRelacionado())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDateFechaEmision(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento()) ? model.getCodigoDocumento() : "05")
                .modCodigoDocumento(model.getModCodigoDocumento())
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toLocalDate(model.getModFechaEmision())
                        : null)
                .anulada(Boolean.FALSE)
                .concepto(model.getConcepto())
                .build();
    }


    public VtVentaEntity builderUpdateEntity(CreationNotaDebitoRequestDto model, VtVentaEntity item) {
        return VtVentaEntity.builder()
                .idVenta(item.getIdVenta())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtNotasDebitoValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(vtNotaDebitoDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .sucursal(model.getSucursal())
                .tipoVenta(TipoVenta.NDB.name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(Objects.nonNull(model.getTipoIngreso())
                        ? model.getTipoIngreso().name()
                        : TipoIngreso.VL.name())
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
                .email(model.getEmail())
                .numeroItems(model.getNumeroItems())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .impresa(model.getImpresa())
                .relacionado(model.getRelacionado())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toLocalDateFechaEmision(model.getFechaEmision()))
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .codigoDocumento(Objects.nonNull(model.getCodigoDocumento()) ? model.getCodigoDocumento() : "05")
                .reembolsosEntity(new ArrayList<>()) // Validacion para evitar error del null
                .modCodigoDocumento(model.getModCodigoDocumento())
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toLocalDate(model.getModFechaEmision())
                        : null)
                .anulada(item.getAnulada())
                .concepto(model.getConcepto())
                .build();
    }


    public GetNotaDebitoDto builderResponse(VtVentaEntity model) {
        return GetNotaDebitoDto.builder()
                .valores(vtNotasDebitoValoresBuilder.builderListValoresDto(model.getValoresEntity()))
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
                .detalle(vtNotaDebitoDetalleBuilder.builderListDto(model.getDetalle()))
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderListDto(model.getFormasPagoSri()))
                .fechaAutorizacion(Objects.nonNull(model.getFechaAutorizacion())
                        ? DateUtils.toLocalDateTimeString(model.getFechaAutorizacion()) : null)
                .claveAcceso(model.getClaveAcceso())
                .modSerie(model.getModSerie())
                .modSecuencial(model.getModSecuencial())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .modFechaEmision(Objects.nonNull(model.getModFechaEmision())
                        ? DateUtils.toString(model.getModFechaEmision())
                        : null)
                .modCodigoDocumento(model.getModCodigoDocumento())
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .build();
    }
}
