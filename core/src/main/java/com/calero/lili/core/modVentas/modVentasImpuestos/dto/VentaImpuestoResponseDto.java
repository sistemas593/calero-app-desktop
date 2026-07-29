package com.calero.lili.core.modVentas.modVentasImpuestos.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modVentas.facturas.dto.ResponseValoresDto;
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
public class VentaImpuestoResponseDto {

    private UUID idVenta;
    private String sucursal;
    private String tipoVenta;
    private String fechaEmision;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;

    private DocumentoEnum codigoDocumento;
    private String tipoIngreso;
    private String liquidar;

    private String guiaRemisionSerie;
    private String guiaRemisionSecuencial;

    private UUID idTercero;
    private String terceroNombre;
    private String numeroIdentificacion;

    private String relacionado;
    private String email;

    private List<ResponseValoresDto> valores;

    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal total;
    private BigDecimal totalImpuesto;

    private Boolean anulada;
    private String fechaAnulacion;

    private String fechaAutorizacion;
    private String claveAcceso;

    private String concepto;

    private FormatoDocumento formatoDocumento;
    private Boolean existeComprobante;

    private List<FormasPagoDto> formasPagoSri;
}
