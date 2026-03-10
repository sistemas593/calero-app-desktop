package com.calero.lili.api.modTesoreria.modTesoreriaEstadosCuenta;

import com.calero.lili.api.modAuditoria.Auditable;
import com.calero.lili.api.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@Entity
@Table(name = "ts_estados_cuenta")
@Where(clause = "deleted = false")
public class TsEstadosCuentaEntity extends Auditable {
         
    @Column(name = "id_data")
    private Long idData;
         
    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idEstadoCuenta;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;
         
    @Column(name = "tipo_documento")
    private String tipoDocumento;
         
    @Column(name = "numero_documento")
    private String numeroDocumento;
         
    @Column(name = "valor")
    private BigDecimal valor;
         
    @Column(name = "detalle")
    private String detalle;
         
    @Column(name = "utilizada")
    private Boolean utilizada;
         
    @Column(name = "id_conciliacion")
    private UUID idConciliacion;
         
    @Column(name = "movimiento")
    private String movimiento;

   // private UUID idEntidad;
    @ManyToOne()
    @JoinColumn(name = "idEntidad", referencedColumnName = "idEntidad")
    private TsEntidadEntity tsEntidadEntity;



}
