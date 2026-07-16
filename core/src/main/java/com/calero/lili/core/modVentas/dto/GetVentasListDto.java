package com.calero.lili.core.modVentas.dto;

import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.OrigenEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GetVentasListDto {

    private String sucursal;
    private UUID idVenta;
    private String tipoVenta;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String tipoIngreso;
    private String codigoDocumento;
    private String fechaEmision;
    private UUID idTercero;
    private String terceroNombre;
    private String numeroIdentificacion;
    private LocalDate fechaVencimiento;
    private int numeroItems;
    private Integer diasCredito;
    private Integer cuotas;
    private FormatoDocumento formatoDocumento;
    private EstadoDocumento estadoDocumento;
    private String emailEstado;
    private Boolean anulada;
    private Boolean impresa;
    private BigDecimal total;
    private Integer ambiente;
    private String email;
    private Boolean existeComprobante;
    private OrigenEnum origen;
}
