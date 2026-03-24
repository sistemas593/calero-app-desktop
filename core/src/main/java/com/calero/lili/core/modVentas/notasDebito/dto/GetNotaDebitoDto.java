package com.calero.lili.core.modVentas.notasDebito.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormaPago;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modVentas.notasDebito.dto.detalles.DetalleGetDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetNotaDebitoDto {

    private UUID idVenta;
    private String sucursal;
    private String tipoVenta;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private String codigoDocumento;
    private String tipoIngreso;
    private String liquidar;

    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    private UUID idTercero;
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
    private FormaPago formaPago;
    private Integer diasCredito;
    private Integer cuotas;

    private List<DetalleGetDto> detalle;

    private List<InformacionAdicionalDto> informacionAdicional;

    private List<FormasPagoDto> formasPagoSri;

    private String fechaAutorizacion;
    private String claveAcceso;
    private String modCodigoDocumento;
    private String modSerie;
    private String modSecuencial;
    private String modFechaEmision;

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String numeroAutorizacion;

}
