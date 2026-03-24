package com.calero.lili.core.modComprasItems.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
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
public class GeItemRequestDto {

    @NotEmpty(message = "No existe el código principal")
    private String codigoPrincipal;

    private String codigoAuxiliar;
    private String codigoBarras;

    @NotEmpty(message = "No existe la descripción")
    private String descripcion;
    private String tipoItem;
    private String cmarca;
    private String medida;
    private UUID idGrupo;

    private List<Impuesto> impuestos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Impuesto {
        private Long idImpuesto;
    }

    private List<DetalleAdicional> detallesAdicionales;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleAdicional {
        private String nombre;
        @Column(length = 300)
        private String valor;
    }

    private List<Precios> precios;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Precios {
        private BigDecimal precio1;
        private BigDecimal precio2;
        private BigDecimal precio3;
        private BigDecimal precio4;
        private BigDecimal precio5;
    }



    // PARA CREACION EN LISTA
    private String codigoIva;

    private String nombreDetalleAdicional1;
    private String valorDetalleAdicional1;
    private String nombreDetalleAdicional2;
    private String valorDetalleAdicional2;
    private String nombreDetalleAdicional3;
    private String valorDetalleAdicional3;

    private UUID idMarca;
    private UUID idCategoria;
    private List<GeMedidasItemsDto> medidas;

    private String caracteristicas;
}
