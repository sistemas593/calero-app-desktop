package com.calero.lili.api.modTerceros;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_terceros_tipo")
@Builder
public class GeTercerosTipoEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idTerceroTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    private Integer tipo;

}
