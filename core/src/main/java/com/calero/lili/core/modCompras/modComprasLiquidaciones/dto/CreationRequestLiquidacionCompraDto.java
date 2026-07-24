package com.calero.lili.core.modCompras.modComprasLiquidaciones.dto;

import com.calero.lili.core.dtos.DetallesDto;
import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreationRequestLiquidacionCompraDto {

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

    private UUID idTercero;

    private String terceroNombre;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;
    private String tipoProveedor;
    private String relacionado;

    private String concepto;

    private List<ValoresDto> valores;

    @NotNull(message = "No existe el subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "No existe el total descuento")
    private BigDecimal totalDescuento;

    private BigDecimal total;
    private Integer numeroItems;
    private String fechaVencimiento;
    private String formaPago;
    private Integer diasCredito;
    private Integer cuotas;
    private Integer czona;
    private String documentoElectronico;
    private String emailEstado;
    private Integer idVendedor;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;
    @Valid
    private List<FormasPagoDto> formasPagoSri;

    private String motivo;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetallesDto> detalle;

    private DocumentoEnum codDocReembolso;
    private BigDecimal totalComprobantesReembolso;
    private BigDecimal totalBaseImponibleReembolso;
    private BigDecimal totalImpuestoReembolso;

    private List<UUID> listIdLiquidacionesReembolso;

    private List<CreationCompraImpuestoRequestDto> compraImpuestos;


    private Integer ambiente;

}
