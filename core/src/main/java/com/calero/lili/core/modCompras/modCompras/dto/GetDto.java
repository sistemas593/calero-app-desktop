package com.calero.lili.core.modCompras.modCompras.dto;

import com.calero.lili.core.modCompras.modCompras.dto.detalles.DetalleGetDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDto {

    private UUID idCompra;
    private String sucursal;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private UUID idTercero;
    private String relacionado;

    private String terceroNombre;
    private String numeroIdentificacion;


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

    private List<GetListDto> listCompraImpuesto;

}
