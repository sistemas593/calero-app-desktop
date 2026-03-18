package com.calero.lili.core.modTerceros;

import com.calero.lili.core.modComprasProveedoresGrupos.CpProveedoresGruposEntity;
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
@Table(name = "ge_terceros_grupo_proveedores")
@Builder
public class GeTercerosGruposProveedoresEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idTerceroGrupoProveedor;

    private Long idData;
    private Long idEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo")
    private CpProveedoresGruposEntity grupo;

}
