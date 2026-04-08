package com.calero.lili.core.modVentas.reembolsos;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vtVentasReembolsos")
@Builder
@Where(clause = "deleted = false")
public class VtVentaReembolsosEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idVentaReembolsos;

    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    @Enumerated(EnumType.STRING)
    private TipoTerceroPerSoc tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private LocalDate fechaEmisionReemb;
    private String numeroAutorizacionReemb;

    @Column(name = "comprobante", columnDefinition = "TEXT")
    private String comprobante;

    @ManyToOne()
    @JoinColumn(name = "codigoPais", referencedColumnName = "codigoPais")
    private TbPaisEntity pais;

    @Builder.Default
    @JoinColumn(name = "id_ventaReembolsos", referencedColumnName = "idVentaReembolsos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VtVentaReembolsosValoresEntity> reembolsosValores = new ArrayList<>();

    @Column(name = "id_venta", insertable = false, updatable = false)
    private UUID idVenta;

}
