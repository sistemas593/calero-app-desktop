package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_bancos_movimientos")
@Where(clause = "deleted = false")
public class TsBancosMovimentosEntity extends Auditable {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idMovimiento;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    private String sucursal;

    @Column(name = "fisico")
    private Boolean fisico;

    @Column(name = "codigo_serie")
    private String codigoSerie;

    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;

    @Column(name = "tipo_documento")
    private String tipoDocumento; //

    @Column(name = "numero_documento")
    private String numeroDocumento; //

    @Column(name = "movimiento")
    private String movimiento;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;

    @Column(name = "valor")
    private BigDecimal valor; //

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "id_conciliacion")
    private UUID idConciliacion;

    @Column(name = "tipomovbc")
    private String tipomovbc;

    @Column(name = "chejercant")
    private String chejercant;

    @ManyToOne()
    @JoinColumn(name = "idEntidad", referencedColumnName = "idEntidad")
    private TsEntidadEntity tsEntidadEntity;

    @ManyToOne()
    @JoinColumn(name = "idTercero", referencedColumnName = "idTercero")
    private GeTerceroEntity vtClienteEntity;

}
