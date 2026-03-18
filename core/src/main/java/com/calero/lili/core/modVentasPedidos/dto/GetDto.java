package com.calero.lili.core.modVentasPedidos.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.modVentasPedidos.dto.detalles.DetalleGetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDto {

    private UUID idPedido;
    private String sucursal;
    private String fechaEmision;
    private String secuencial;
    private UUID idTercero;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;
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

    private String fechaAutorizacion;
    private String claveAcceso;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Valores {
        private String codigo;
        private String codigoPorcentaje;
        private int tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }
}
