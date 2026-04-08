package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cpImpuestosReembolsos")
@Where(clause = "deleted = false")
public class CpImpuestosReembolsosEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idImpuestosReembolsos;

    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private String tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;


    @Builder.Default
    @JoinColumn(name = "id_impuestosReembolsos", referencedColumnName = "idImpuestosReembolsos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CpImpuestosReembolsosValoresEntity> reembolsosValores = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "codigoPais", referencedColumnName = "codigoPais")
    private TbPaisEntity pais;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;


}
