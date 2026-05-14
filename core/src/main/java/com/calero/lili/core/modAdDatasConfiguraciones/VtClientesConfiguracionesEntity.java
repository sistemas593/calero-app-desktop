package com.calero.lili.core.modAdDatasConfiguraciones;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modAdModulos.AdModulosEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vt_clientes_configuraciones")
@Builder
@Where(clause = "deleted = false")
public class VtClientesConfiguracionesEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idConfiguracion;

    private String clave;

    private LocalDate fechaVencimiento;

    private String ruc;
    private String enviarCorreos;

    private BigDecimal valorRenovacion;
    private String facturaEmite;
    private LocalDate fechaEmitirFactura;
    private LocalDate fechaLlamar;
    private String respaldosResponsable;
    private LocalDate respaldoUltimoOficina;

    @Column(name = "conexion_base", columnDefinition = "TEXT")
    private String conexionBase;

    @Column(name = "configuraciones", columnDefinition = "TEXT")
    private String configuraciones;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    private String usuarios;

    @Column(name = "modulos", columnDefinition = "TEXT")
    private String modulos;

    @Column(name = "rucs_activados", columnDefinition = "TEXT")
    private String rucsActivados;

    @Column(name = "claves_pcs", columnDefinition = "TEXT")
    private String clavesPcs;

    @Column(name = "tipo_blo", length = 1)
    private Long tipoBlo;

    private LocalDate fechaBlo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercero")
    private GeTerceroEntity tercero;


    @ManyToMany
    @JoinTable(
            name = "ad_datas_configuraciones_modulos",
            joinColumns = @JoinColumn(name = "id_configuracion"),
            inverseJoinColumns = @JoinColumn(name = "id_modulo"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_configuracion", "id_modulo"})})
    private List<AdModulosEntity> modulosList;

}
