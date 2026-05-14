package com.calero.lili.core.modTerceros;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.enums.EstadoCivilEnum;
import com.calero.lili.core.enums.OrigenIngresosEnum;
import com.calero.lili.core.enums.SexoEnum;
import com.calero.lili.core.enums.TipoClienteProveedor;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ge_terceros")
@Builder
@Where(clause = "deleted = false")
public class GeTerceroEntity extends Auditable implements Serializable {

    @Column(name = "id_data")
    private Long idData;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idTercero;

    @Column(name = "tipo_identificacion", length = 1)
//    @Enumerated(EnumType.STRING)
    private String tipoIdentificacion;

    @Column(name = "numero_identificacion", length = 15) //unique=true
    private String numeroIdentificacion;

    @NotNull
    @NotEmpty
    @Column(name = "tercero", columnDefinition = "varchar (300)")
    private String tercero;

    @Column(name = "web")
    private String web;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente_proveedor")
    private TipoClienteProveedor tipoClienteProveedor;

    @Column(name = "ciudad", length = 60)
    private String ciudad;

    @Column(name = "direccion", columnDefinition = "varchar (300)")
    private String direccion;

    @Column(name = "telefonos", length = 30)
    private String telefonos;

    @Column(name = "contacto", length = 30)
    private String contacto;

    @Column(name = "email", length = 150)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private SexoEnum sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_ingresos")
    private OrigenIngresosEnum origenIngresos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil")
    private EstadoCivilEnum estadoCivil;

    private Boolean datosAdicionales;

    @Builder.Default
    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<VtVentaEntity> vtFacturaEntity = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<VtClientesConfiguracionesEntity> stEmpresasEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "proveedor")
    @JsonIgnore
    private List<CpLiquidacionesEntity> cpLiquidacionesEntity = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<GeTercerosTipoEntity> geTercerosTipoEntities = new ArrayList<>();


    private String placa;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_parroquia")
    private ParroquiaEntity parroquia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_canton")
    private CantonEntity canton;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_provincia")
    private ProvinciaEntity provincia;

    @ManyToOne()
    @JoinColumn(name = "codigoPais", referencedColumnName = "codigoPais")
    private TbPaisEntity pais;



    // no esta en tercero la relacion

}
