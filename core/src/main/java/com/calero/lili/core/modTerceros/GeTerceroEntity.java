package com.calero.lili.core.modTerceros;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
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

    @Column(name = "tipo_cliente_proveedor", length = 2)
    private String tipoClienteProveedor;

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

    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<VtVentaEntity> vtFacturaEntity = new ArrayList<>();

    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<VtClientesConfiguracionesEntity> stEmpresasEntities = new ArrayList<>();

    @OneToMany(mappedBy = "proveedor")
    @JsonIgnore
    private List<CpLiquidacionesEntity> cpLiquidacionesEntity = new ArrayList<>();

    @OneToMany(mappedBy = "tercero")
    @JsonIgnore
    private List<GeTercerosTipoEntity> geTercerosTipoEntities = new ArrayList<>();


    private String placa;
}
