package com.calero.lili.core.modCompras.modCompras.dto;

import com.calero.lili.core.modCompras.modCompras.dto.detalles.DetalleGetDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GetListDto {

    private String sucursal;
    private UUID idCompra;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String fechaEmision;
    private String terceroNombre;
    private String numeroIdentificacion;
    private UUID idTercero;
    private LocalDate fechaVencimiento;
    private int numeroItems;
    private Integer diasCredito;
    private Integer cuotas;
    private String formatoDocumento;
    private String estadoDocumento;
    private String emailEstado;
    private Boolean anulada;
    private Boolean impresa;
    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;
    private String codDocReembolso;
    private BigDecimal totalComprobantesReembolso;
    private BigDecimal totalBaseImponibleReembolso;
    private BigDecimal totalImpuestoReembolso;
    private List<ResponseValoresDto> valores;
    private List<com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto> listCompraImpuesto;
    private List<DetalleGetDto> detalle;


}
