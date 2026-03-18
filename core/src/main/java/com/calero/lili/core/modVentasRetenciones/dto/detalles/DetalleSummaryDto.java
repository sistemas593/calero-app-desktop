package com.calero.lili.core.modVentasRetenciones.dto.detalles;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class DetalleSummaryDto {

    private Long idVenta;
    private String serie;
    private String secuencial;
    private String idTercero;
    private String cliente;
    private String numeroIdentificacion;
    private String codigoItem;
    private String item;
    private BigDecimal precioUnitario;

}
