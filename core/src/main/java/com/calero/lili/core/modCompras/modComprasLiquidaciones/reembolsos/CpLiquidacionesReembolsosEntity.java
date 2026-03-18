package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos;

import com.calero.lili.core.enums.TipoTerceroPerSoc;
import com.calero.lili.core.Auditable;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpLiquidacionesReembolsos")
@Builder
@Where(clause = "deleted = false")
public class CpLiquidacionesReembolsosEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idLiquidacionReembolsos;

    @Column(nullable = false)
    private String tipoIdentificacionReemb;

    @Column(nullable = false)
    private String numeroIdentificacionReemb;

    @Enumerated(EnumType.STRING)
    private TipoTerceroPerSoc tipoProveedorReemb;

    private String codigoDocumentoReemb;

    @Column(nullable = false)
    private String serieReemb;

    @Column(nullable = false)
    private String secuencialReemb;

    @Column(nullable = false)
    private LocalDate fechaEmisionReemb;

    private String numeroAutorizacionReemb;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    @ManyToOne()
    @JoinColumn(name = "codigoPais", referencedColumnName = "codigoPais")
    private TbPaisEntity pais;

    @JoinColumn(name = "idLiquidacionReembolsos", referencedColumnName = "idLiquidacionReembolsos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpLiquidacionesReembolsosValoresEntity> reembolsosValores = new ArrayList<>();

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "sucursal", length = 3)
    private String sucursal;

    @Column(name = "id_liquidacion", insertable = false, updatable = false)
    private UUID idLiquidacion;
}
