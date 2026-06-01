package com.calero.lili.core.modVentas.modVentasImpuestos.dto;

import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.enums.TipoVenta;
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
public class CreationVentaImpuestoRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    @NotNull(message = "No existe el formato del documento")
    private FormatoDocumento formatoDocumento;

    @NotEmpty(message = "No existe la serie")
    private String serie;

    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    private String numeroAutorizacion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    @NotEmpty(message = "No existe el codigo documento")
    private String codigoDocumento;

    @NotNull(message = "No existe el tipo de ingreso")
    private TipoIngreso tipoIngreso;

    @NotNull(message = "No existe Liquidar")
    private Liquidar liquidar;

    @NotNull(message = "No existe Tipo de venta")
    private TipoVenta tipoVenta;


    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    private UUID idTercero;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;
    private String direccion;
    private String email;

    private String relacionado;

    private String concepto;

    private List<ValoresDto> valores;

    @NotNull(message = "No existe el subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "No existe el total descuento")
    private BigDecimal totalDescuento;

    private BigDecimal total;

    private BigDecimal totalImpuesto;

    @NotNull(message = "Cuentas por cobrar no existe")
    private Boolean cuentaPorCobrar;
}
