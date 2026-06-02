package com.calero.lili.core.modVentas.modVentasImpuestos.builder;

import com.calero.lili.core.builder.FormasPagoBuilder;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.OrigenEnum;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.facturas.builder.VtVentaValoresBuilder;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.CreationVentaImpuestoRequestDto;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.VentaImpuestoResponseDto;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class VtVentasImpuestoBuilder {

    private final VtVentaValoresBuilder vtVentaValoresBuilder;
    private final FormasPagoBuilder formasPagoBuilder;

    public VtVentaEntity builderEntity(CreationVentaImpuestoRequestDto model, Long idData, Long idEmpresa) {
        return VtVentaEntity.builder()
                .idVenta(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .valoresEntity(vtVentaValoresBuilder.builderList(model.getValores(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .tipoVenta(model.getTipoVenta().name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso().name())
                .codigoDocumento(model.getCodigoDocumento())
                .liquidar(Objects.nonNull(model.getLiquidar())
                        ? model.getLiquidar().name()
                        : Liquidar.N.name())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .email(model.getEmail())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(Boolean.FALSE)
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie("")
                .modSecuencial("")
                .fechaEmision(DateUtils.toLocalDateFechaEmision(model.getFechaEmision()))
                .totalImpuesto(model.getTotalImpuesto())
                .existeComprobante(Boolean.FALSE)
                .estadoDocumento(EstadoDocumento.AUT)
                .ambiente(2)
                .origen(OrigenEnum.IMP)
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .build();
    }

    public VtVentaEntity builderUpdateEntity(CreationVentaImpuestoRequestDto model, VtVentaEntity item) {
        return VtVentaEntity.builder()
                .idVenta(item.getIdVenta())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .valoresEntity(vtVentaValoresBuilder.builderList(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .sucursal(model.getSucursal())
                .tipoVenta(model.getTipoVenta().name())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .tipoIngreso(model.getTipoIngreso().name())
                .codigoDocumento(model.getCodigoDocumento())
                .liquidar(Objects.nonNull(model.getLiquidar())
                        ? model.getLiquidar().name()
                        : Liquidar.N.name())
                .fechaAnulacion(item.getFechaAnulacion())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .formatoDocumento(model.getFormatoDocumento())
                .emailEstado(EmailEstado.NO_ENTREGADO.getTipo())
                .email(model.getEmail())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .anulada(item.getAnulada())
                .relacionado(model.getRelacionado())
                .concepto(model.getConcepto())
                .modSerie("")
                .modSecuencial("")
                .fechaEmision(DateUtils.toLocalDateFechaEmision(model.getFechaEmision()))
                .totalImpuesto(model.getTotalImpuesto())
                .existeComprobante(item.getExisteComprobante())
                .estadoDocumento(EstadoDocumento.AUT)
                .ambiente(2)
                .existeComprobante(Boolean.FALSE)
                .origen(OrigenEnum.IMP)
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .build();
    }

    public VentaImpuestoResponseDto builderResponse(VtVentaEntity model) {
        return VentaImpuestoResponseDto.builder()
                .idVenta(model.getIdVenta())
                .sucursal(model.getSucursal())
                .tipoVenta(model.getTipoVenta())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .codigoDocumento(model.getCodigoDocumento())
                .tipoIngreso(model.getTipoIngreso())
                .liquidar(model.getLiquidar())
                .guiaRemisionSerie(model.getGuiaRemisionSerie())
                .guiaRemisionSecuencial(model.getGuiaRemisionSecuencial())
                .idTercero(Objects.nonNull(model.getTercero()) ? model.getTercero().getIdTercero() : null)
                .terceroNombre(Objects.nonNull(model.getTercero()) ? model.getTercero().getTercero() : null)
                .numeroIdentificacion(Objects.nonNull(model.getTercero()) ? model.getTercero().getNumeroIdentificacion() : null)
                .relacionado(model.getRelacionado())
                .email(model.getEmail())
                .valores(vtVentaValoresBuilder.builderListValoresDto(model.getValoresEntity()))
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .totalImpuesto(model.getTotalImpuesto())
                .anulada(model.getAnulada())
                .fechaAnulacion(Objects.nonNull(model.getFechaAnulacion())
                        ? model.getFechaAnulacion().toString()
                        : null)
                .fechaAutorizacion(Objects.nonNull(model.getFechaAutorizacion())
                        ? DateUtils.toLocalDateTimeString(model.getFechaAutorizacion())
                        : null)
                .claveAcceso(model.getClaveAcceso())
                .concepto(model.getConcepto())
                .formatoDocumento(model.getFormatoDocumento())
                .existeComprobante(model.getExisteComprobante())
                .existeComprobante(model.getExisteComprobante())
                .formasPagoSri(formasPagoBuilder.builderListDto(model.getFormasPagoSri()))
                .build();
    }
}
