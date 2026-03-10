package com.calero.lili.api.modComprasItems;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "geItemsPrecios")
@Builder
public class GeItemsPreciosEntity {

    // mover a mod compra items
    @Column(name = "id_data")
    private Long idData;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idItemsPrecio;

    private BigDecimal precio1;
    private BigDecimal precio2;
    private BigDecimal precio3;
    private BigDecimal precio4;
    private BigDecimal precio5;
}
