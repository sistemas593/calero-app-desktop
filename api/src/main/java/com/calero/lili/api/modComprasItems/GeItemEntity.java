package com.calero.lili.api.modComprasItems;

import com.calero.lili.core.Auditable;
import com.calero.lili.api.modComprasItemsCategorias.GeItemsCategoriaEntity;
import com.calero.lili.api.modComprasItemsGrupos.GeItemGrupoEntity;
import com.calero.lili.api.modComprasItemsMarcas.GeItemsMarcasEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_items")
@Builder
@Where(clause = "deleted = false")
public class GeItemEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idItem;

    @Size(min = 3, max = 3)
    private String tipoItem; // GUARDAR PRODUCTOS (PRO),SERVICIOS (SER),GASTOS (GAS), ACTIVOS FIJOS (ACF)

    @Column(name = "codigo_principal")
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar")
    private String codigoAuxiliar;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "caracteristicas")
    private String caracteristicas;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "ordenador")
    private Long ordenador;

    @Column(name = "ultima_compra")
    private LocalDate ultimaCompra;

    @Column(name = "ultima_venta")
    private LocalDate ultimaVenta;

    @Column(name = "estado")
    private Long estado;

    @JoinColumn(name = "id_item", referencedColumnName = "idItem")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeItemsPreciosEntity> geItemsPreciosEntities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo")
    private GeItemGrupoEntity grupos;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<DetalleAdicional> detallesAdicionales;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private GeItemsMarcasEntity marcas;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private GeItemsCategoriaEntity categorias;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DetalleAdicional {
        private String nombre;
        @Column(length = 300)
        private String valor;
    }

    @ManyToMany()
    @JoinTable(name = "ge_items_impuestos",
            joinColumns = @JoinColumn(name = "id_item"),
            inverseJoinColumns = @JoinColumn(name = "id_impuesto"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_item", "id_impuesto"})})
    private List<GeImpuestosEntity> impuestos;


    @JoinColumn(name = "id_item", referencedColumnName = "idItem")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeMedidasItemsEntity> medidas;

}
