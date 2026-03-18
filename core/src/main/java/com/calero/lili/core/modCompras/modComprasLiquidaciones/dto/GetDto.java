package com.calero.lili.core.modCompras.modComprasLiquidaciones.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDto {

    private UUID idLiquidacion;
    private String sucursal;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private UUID idTercero;
    private String terceroNombre;

    private String relacionado;
    private String email;

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private Integer numeroItems;

    private String fechaVencimiento;
    private String fechaAnulacion;
    private String formaPago;
    private Integer diasCredito;
    private Integer cuotas;

    private List<DetalleGetDto> detalle;

    private List<InformacionAdicionalDto> informacionAdicional;
    private List<FormasPagoDto> formasPagoSri;


    private String fechaAutorizacion;
    private String claveAcceso;

    private String codDocReembolso;
    private BigDecimal totalComprobantesReembolso;
    private BigDecimal totalBaseImponibleReembolso;
    private BigDecimal totalImpuestoReembolso;

    private List<GetReembolsoDto> reembolsos;

    private List<GetListDto> listCompraImpuesto;

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;

    private String concepto;

}
