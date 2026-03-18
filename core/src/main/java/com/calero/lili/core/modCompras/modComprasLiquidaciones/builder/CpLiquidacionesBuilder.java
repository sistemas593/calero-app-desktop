package com.calero.lili.core.modCompras.modComprasLiquidaciones.builder;


import com.calero.lili.core.builder.FormasPagoBuilder;
import com.calero.lili.core.builder.InformacionAdicionalBuilder;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.builder.CpLiquidacionesReembolsosBuilder;
import com.calero.lili.core.enums.Ambiente;
import com.calero.lili.core.enums.EmailEstado;
import com.calero.lili.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CpLiquidacionesBuilder {

    private final CpLiquidacionesValoresBuilder cpLiquidacionesValoresBuilder;
    private final CpLiquidacionesDetalleBuilder cpLiquidacionesDetalleBuilder;
    private final CpLiquidacionesReembolsosBuilder cpLiquidacionesReembolsosBuilder;
    private final FormasPagoBuilder formasPagoBuilder;
    private final InformacionAdicionalBuilder informacionAdicionalBuilder;

    public CpLiquidacionesEntity builderEntity(CreationRequestLiquidacionCompraDto model, Long idData, Long idEmpresa) {
        return CpLiquidacionesEntity.builder()
                .valoresEntity(cpLiquidacionesValoresBuilder.builderListValores(model.getValores(), idData, idEmpresa))
                .detalle(cpLiquidacionesDetalleBuilder.builderList(model.getDetalle(), idData, idEmpresa))
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
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
                .anulada(Boolean.FALSE)
                .impresa(model.getImpresa())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .concepto(model.getConcepto())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(idData)
                .idEmpresa(idEmpresa)
                .idLiquidacion(UUID.randomUUID())
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .anulada(Boolean.FALSE)
                .build();
    }


    public CpLiquidacionesEntity builderUpdateEntity(CreationRequestLiquidacionCompraDto model, CpLiquidacionesEntity item) {
        return CpLiquidacionesEntity.builder()
                .valoresEntity(cpLiquidacionesValoresBuilder.builderListValores(model.getValores(), item.getIdData(), item.getIdEmpresa()))
                .detalle(cpLiquidacionesDetalleBuilder.builderList(model.getDetalle(), item.getIdData(), item.getIdEmpresa()))
                .sucursal(model.getSucursal())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
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
                .anulada(item.getAnulada())
                .impresa(model.getImpresa())
                .tipoIdentificacion(model.getTipoIdentificacion())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .tipoProveedor(model.getTipoProveedor())
                .relacionado(model.getRelacionado())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .informacionAdicional(informacionAdicionalBuilder.builderList(model.getInformacionAdicional()))
                .formasPagoSri(formasPagoBuilder.builderList(model.getFormasPagoSri()))
                .concepto(model.getConcepto())
                .fechaEmision(DateUtils.toLocalDate(model.getFechaEmision()))
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .idLiquidacion(item.getIdLiquidacion())
                .ambiente(Objects.nonNull(model.getAmbiente())
                        ? Ambiente.obtenerAmbiente(model.getAmbiente())
                        : null)
                .reembolsosEntity(item.getReembolsosEntity())
                .anulada(item.getAnulada())
                .build();
    }


    public GetDto builderGetDto(CpLiquidacionesEntity model) {
        return GetDto.builder()
                .idLiquidacion(model.getIdLiquidacion())
                .sucursal(model.getSucursal())
                .fechaEmision(DateUtils.toString(model.getFechaEmision()))
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .idTercero(model.getProveedor().getIdTercero())
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
                .fechaAutorizacion(model.getFechaAutorizacion())
                .claveAcceso(model.getClaveAcceso())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .valores(cpLiquidacionesValoresBuilder.builderListResponseValores(model.getValoresEntity()))
                .formasPagoSri(formasPagoBuilder.builderListDto(model.getFormasPagoSri()))
                .informacionAdicional(informacionAdicionalBuilder.builderListDto(model.getInformacionAdicional()))
                .reembolsos(cpLiquidacionesReembolsosBuilder.builderListResponse(model.getReembolsosEntity()))
                .detalle(cpLiquidacionesDetalleBuilder.builderListResponse(model.getDetalle()))
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .idTercero(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getIdTercero()
                        : null)
                .terceroNombre(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getTercero()
                        : null)
                .concepto(model.getConcepto())
                .build();
    }

    public GetListDto builderListDto(CpLiquidacionesEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idLiquidacion(model.getIdLiquidacion())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaEmision(model.getFechaEmision().toString())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getIdTercero()
                        : null)
                .terceroNombre(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getTercero()
                        : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .estadoDocumento(model.getEstadoDocumento().name())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .valores(cpLiquidacionesValoresBuilder.builderListResponseValores(model.getValoresEntity()))
                .reembolsos(cpLiquidacionesReembolsosBuilder.builderListResponse(model.getReembolsosEntity()))
                .detalle(cpLiquidacionesDetalleBuilder.builderListResponse(model.getDetalle()))
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .email(model.getEmail())
                .build();
    }

    public GetListDto builderGetListDto(CpLiquidacionesEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idLiquidacion(model.getIdLiquidacion())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaEmision(model.getFechaEmision().toString())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getIdTercero()
                        : null)
                .terceroNombre(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getTercero()
                        : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .estadoDocumento(model.getEstadoDocumento().name())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .valores(cpLiquidacionesValoresBuilder.builderListResponseValores(model.getValoresEntity()))
                .reembolsos(cpLiquidacionesReembolsosBuilder.builderListResponse(model.getReembolsosEntity()))
                .detalle(cpLiquidacionesDetalleBuilder.builderListResponse(model.getDetalle()))
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .build();
    }


    public GetListDto builderAnuladaGetListDto(CpLiquidacionesEntity model) {
        return GetListDto.builder()
                .sucursal(model.getSucursal())
                .idLiquidacion(model.getIdLiquidacion())
                .serie(model.getSerie())
                .secuencial(model.getSecuencial())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .fechaEmision(model.getFechaEmision().toString())
                .numeroIdentificacion(model.getNumeroIdentificacion())
                .idTercero(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getIdTercero()
                        : null)
                .terceroNombre(Objects.nonNull(model.getProveedor())
                        ? model.getProveedor().getTercero()
                        : null)
                .fechaVencimiento(model.getFechaVencimiento())
                .numeroItems(model.getNumeroItems())
                .diasCredito(model.getDiasCredito())
                .cuotas(model.getCuotas())
                .estadoDocumento(model.getEstadoDocumento().name())
                .emailEstado(model.getEmailEstado().toString())
                .anulada(model.getAnulada())
                .impresa(model.getImpresa())
                .subtotal(model.getSubtotal())
                .totalDescuento(model.getTotalDescuento())
                .total(model.getTotal())
                .codDocReembolso(model.getCodDocReembolso())
                .totalComprobantesReembolso(model.getTotalComprobantesReembolso())
                .totalBaseImponibleReembolso(model.getTotalBaseImponibleReembolso())
                .totalImpuestoReembolso(model.getTotalImpuestoReembolso())
                .valores(cpLiquidacionesValoresBuilder.builderAnuladaListResponseValores(model.getValoresEntity()))
                .reembolsos(cpLiquidacionesReembolsosBuilder.builderListResponse(model.getReembolsosEntity()))
                .detalle(cpLiquidacionesDetalleBuilder.builderListResponse(model.getDetalle()))
                .ambiente(model.getAmbiente())
                .formatoDocumento(model.getFormatoDocumento())
                .build();
    }

}
