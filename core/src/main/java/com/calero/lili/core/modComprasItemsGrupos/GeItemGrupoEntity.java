package com.calero.lili.core.modComprasItemsGrupos;

import com.calero.lili.core.enums.TipoItemGrupo;
import com.calero.lili.core.Auditable;
import com.calero.lili.core.modComprasItems.GeItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_items_grupos")
@Builder
@Where(clause = "deleted = false")
public class GeItemGrupoEntity extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idGrupo;
         
    @Column(name = "grupo")
    private String grupo;

    @Enumerated(EnumType.STRING)
    private TipoItemGrupo tipoGrupo;

    @Column(name = "id_cuenta_inventario")
    private UUID idCuentaInventario;

    @Column(name = "id_cuenta_ingreso")
    private UUID idCuentaIngreso;

    @Column(name = "id_cuenta_costo")
    private UUID idCuentaCosto;

    @Column(name = "id_cuenta_descuento")
    private UUID idCuentaDescuento;

    @Column(name = "id_cuenta_devolucion")
    private UUID idCuentaDevolucion;

    @Column(name = "id_cuenta_gasto")
    private UUID idCuentaGasto;

    @OneToMany(mappedBy = "grupos" )
    private List<GeItemEntity> geItemsEntities = new ArrayList<>();

}
