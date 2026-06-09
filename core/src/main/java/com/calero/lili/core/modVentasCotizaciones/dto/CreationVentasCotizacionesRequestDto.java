package com.calero.lili.core.modVentasCotizaciones.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.modVentas.dto.DetailDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationVentasCotizacionesRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    private UUID idTercero;

    private String concepto;

    private List<ValoresDto> valores;

    @NotNull(message = "No existe el subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "No existe el total descuento")
    private BigDecimal totalDescuento;


    private BigDecimal total;

    private Integer numeroItems;
    private String fechaVencimiento;
    private String fechaAnulacion;
    private String formaPago;
    private Integer diasCredito;
    private Integer cuotas;
    private Integer czona;
    private String documentoElectronico;
    private String emailEstado;
    private Integer idVendedor;
    private Boolean anulada;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;
    private String motivo;
    private String modTipoVenta;
    private String modSerie;
    private String modSecuencial;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;


}
