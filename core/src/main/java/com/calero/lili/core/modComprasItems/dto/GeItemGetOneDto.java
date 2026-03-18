package com.calero.lili.core.modComprasItems.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GeItemGetOneDto {

    private UUID idItem;
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String codigoBarras;
    private String descripcion;
    private String caracteristicas;

    private UUID idGrupo;

    private List<Impuesto> impuestos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Impuesto {
        private Long idImpuesto;
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal tarifa;
    }

    private List<DetalleAdicional> detallesAdicionales;
    private CategoriaItem categoria;
    private GrupoItem grupo;
    private MarcaItem marca;
    private List<GeMedidasResponseDto> medidas;

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
    @Builder
    public static class Precios {
        private BigDecimal precio1;
        private BigDecimal precio2;
        private BigDecimal precio3;
        private BigDecimal precio4;
        private BigDecimal precio5;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GrupoItem {
        private UUID idGrupoItem;
        private String grupo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarcaItem {
        private UUID idMarca;
        private String marca;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoriaItem {
        private UUID idCategoria;
        private String categoria;
    }
}
