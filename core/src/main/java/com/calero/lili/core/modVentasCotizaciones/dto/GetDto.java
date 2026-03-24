package com.calero.lili.core.modVentasCotizaciones.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.modVentasCotizaciones.dto.detalles.DetalleGetDto;
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
public class GetDto {

    private UUID idCotizacion;
    private String sucursal;
    private String fechaEmision;
    private String secuencial;

    private UUID idTercero;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;
    private String email;

    private ClienteDatos terceroDatos; // ABAJO DEFINO LA LISTA CLIENTEDATOS

    // Clase interna para encapsular los datos del cliente
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClienteDatos {
        private String direccion;
        private String ciudad;
        private String telefonos;
    }

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

    private List<InformacionAdicionalDto> informacionAdicional;
}
