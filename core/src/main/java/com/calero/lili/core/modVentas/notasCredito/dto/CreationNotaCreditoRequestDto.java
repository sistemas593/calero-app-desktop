package com.calero.lili.core.modVentas.notasCredito.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.dtos.ValoresDto;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.Liquidar;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.core.enums.TipoIngreso;
import com.calero.lili.core.modVentas.dto.DetailDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreationNotaCreditoRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;
    @NotNull(message = "El formato documento no puede ser nulo")
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

    //@NotEmpty(message = "No existe el tipo")
    private TipoIngreso tipoIngreso;

    private Liquidar liquidar;

    @NotNull(message = "No existe el nombre del cliente")
    private UUID idTercero;

    private TipoIdentificacion tipoIdentificacion;
    private String relacionado;


    private List<ValoresDto> valores;


    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;

    private Integer numeroItems;
    private String fechaVencimiento;
    private FormaPago formaPago;
    private Integer diasCredito;
    private Integer cuotas;
    private Integer czona;
    private String documentoElectronico;
    private Integer idVendedor;
    private Boolean impresa;

    private List<InformacionAdicionalDto> informacionAdicional;


    @NotEmpty(message = "No existe el motivo de la nota de credito")
    private String concepto;

    @NotEmpty(message = "No existe el codigo documento la que aplica la nota de credito")
    private String modCodigoDocumento;

    @NotEmpty(message = "No existe la serie a la que aplica la nota de credito")
    private String modSerie;

    @NotEmpty(message = "No existe el secuencial a la que aplica la nota de credito")
    private String modSecuencial;

    @NotEmpty(message = "No existe la fecha del documento a la que aplica la nota de credito")
    private String modFechaEmision;

    @Valid
    @NotEmpty(message = "No existen detalle de items")
    private List<DetailDto> detalle;


    private Integer ambiente;
}
