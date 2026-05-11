package com.calero.lili.core.modCompras.modComprasLiquidaciones.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetListDto {

    private String sucursal;
    private UUID idLiquidacion;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String fechaEmision;
    private UUID idTercero;
    private String terceroNombre;
    private String numeroIdentificacion;
    private LocalDate fechaVencimiento;
    private int numeroItems;
    private Integer diasCredito;
    private Integer cuotas;
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
    private List<GetReembolsoDto> reembolsos;
    private List<DetalleGetDto> detalle;

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String email;
    private Boolean existeComprobante;

}
