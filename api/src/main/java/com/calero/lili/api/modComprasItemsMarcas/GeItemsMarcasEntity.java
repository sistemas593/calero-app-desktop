package com.calero.lili.api.modComprasItemsMarcas;

import com.calero.lili.core.Auditable;
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
@Table(name = "ge_marcas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class GeItemsMarcasEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idMarca;

    @Column(name = "id_data")
    private Long idData;

    private String marca;

    @OneToMany(mappedBy = "marcas" )
    private List<GeItemEntity> geItemsEntities = new ArrayList<>();

}
