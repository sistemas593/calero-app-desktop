package com.calero.lili.api.modComprasItemsCategorias;

import com.calero.lili.api.modAuditoria.Auditable;
import com.calero.lili.api.modComprasItems.GeItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ge_categorias")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class GeItemsCategoriaEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCategoria;

    @Column(name = "id_data")
    private Long idData;

    private String categoria;

    private String nivel;

    @OneToMany(mappedBy = "categorias" )
    private List<GeItemEntity> geItemsEntities = new ArrayList<>();

}
