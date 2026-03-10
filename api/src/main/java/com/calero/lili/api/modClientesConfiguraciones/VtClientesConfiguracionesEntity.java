package com.calero.lili.api.modClientesConfiguraciones;

import com.calero.lili.core.Auditable;
import com.calero.lili.api.modTerceros.GeTerceroEntity;
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
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vt_clientes_configuraciones")
@Builder
@Where(clause = "deleted = false")
public class VtClientesConfiguracionesEntity extends Auditable {

    @Column(name = "id_data")
    private Long idData;

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

}
