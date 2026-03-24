package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDto {

    private UUID idRetencion;
    private String sucursal;
    private String fechaEmision;
    private String serie;
    private String secuencial;

    private UUID idTercero;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;
    private String email;

    private BigDecimal total;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private Integer numeroItems;

    private String fechaVencimiento;
    private String fechaAnulacion;
    private String formaPago;
    private Integer diasCredito;
    private Integer cuotas;


    private String fechaAutorizacion;
    private String claveAcceso;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Valores {
        private String codigo;
        private String codigoPorcentaje;
        private int tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

    private List<GetListDto> listCompraImpuesto;

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String numeroAutorizacionRetencion;
}
