package com.calero.lili.api.modVentasPedidos;

import com.calero.lili.api.modComprasItems.GeItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtPedidosDetalle") //invoice_details
@Builder
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class VtPedidoDetalleEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idPedidoDetalle;

    @Column
    private String codigoPrincipal;

    @Column
    private String codigoAuxiliar;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column
    private String descripcion;

    @Column
    private BigDecimal cantidad;

    @Column
    private BigDecimal precioUnitario;

    private BigDecimal descuento;

    private BigDecimal dsctoItem;

    private BigDecimal subtotalItem;

    @Column(name = "id_bodega")
    private Integer idBodega;

    @Column(name = "item_orden")
    private Integer itemOrden;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "id_vendedor")
    private Integer idVendedor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Impuestos> impuesto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Impuestos {
        private String codigo;
        private String codigoPorcentaje;
        private Integer tarifa;
        private BigDecimal baseImponible;
        private BigDecimal valor;
    }

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<DetalleAdicional> detAdicional;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleAdicional {
        private String nombre;
        @Column(length = 300)
        private String valor;
    }

    @ManyToOne(fetch = FetchType.LAZY) // , optional = false
    @JoinColumn(name = "id_item", nullable = false) // NOMBRE DEL CAMPO RELACIONADO, PONER EL MIMO NOMBRE QUE TIENE LA TABLA PRINCIPAL
    private GeItemEntity items; // NOMBRE DE LA RELACION, ESTE VA EN LA TABLA PRINCIPAL

}
