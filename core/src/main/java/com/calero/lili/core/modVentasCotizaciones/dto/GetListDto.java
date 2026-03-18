package com.calero.lili.core.modVentasCotizaciones.dto;

import com.calero.lili.core.modVentasCotizaciones.dto.detalles.DetalleGetDto;
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
    private UUID idCotizacion;
    private String secuencial;

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

    private List<ResponseValoresDto> valores;
    private List<DetalleGetDto> detalle;

}
