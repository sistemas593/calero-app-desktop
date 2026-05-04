package com.calero.lili.core.modVentas.dto;

import com.calero.lili.core.dtos.ImpuestoItemsDto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto {

    private UUID idItem;
    private int itemOrden;
    @NotEmpty(message = "No existe el codigo principal")
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String codigoBarras;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private BigDecimal cantidad;
    private BigDecimal dsctoItem;
    private BigDecimal descuento;
    private BigDecimal subtotalItem;

    private List<ImpuestoItemsDto> impuesto;

    private List<DetalleAdicional> detAdicional;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetalleAdicional {
        private String nombre;
        @Column(length = 300)
        private String valor;
    }

    private UUID idCentroCostos;

}
