package com.calero.lili.core.modComprasOrden.dto;

import com.calero.lili.core.modComprasOrden.dto.detalles.DetalleGetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetListDto {

    private UUID idCompra;

    private String sucursal;

    private String secuencial;

    private String fechaEmision;

    private String fechaAnulacion;

    private String formaPago;

    private Integer diasCredito;

    private String fechaVencimiento;

    private Integer cuotas;

    private Integer numeroItems;

    private BigDecimal subtotal;

    private BigDecimal totalDescuento;

    private BigDecimal total;

    private Boolean anulada;

    private Boolean impresa;

    private String concepto;

    private TercerosDatos terceroDatos;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TercerosDatos {
        private UUID idTercero;
        private String tercero;
    }

    private List<DetalleGetDto> detalle;
    private List<ResponseValoresDto> valores;


}
