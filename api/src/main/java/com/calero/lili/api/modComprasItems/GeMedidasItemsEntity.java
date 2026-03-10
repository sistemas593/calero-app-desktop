package com.calero.lili.api.modComprasItems;

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

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_items_medidas")
public class GeMedidasItemsEntity {

    // MOVER A MOD ITEMS

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idItemMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item")
    private GeItemEntity geItemEntity;

    private UUID idUnidadMedida;

    private Integer factor;


}
