package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import com.calero.lili.core.enums.TipoComprobante;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_comprobante_detalles")
public class TsComprobanteDetallesEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idComprobanteDetalle;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "movimiento")
    private String movimiento;

    @Column(name = "fecha_documento")
    private LocalDate fechaDocumento;

    @Enumerated(EnumType.STRING)
    private TipoComprobante tipoComprobante;

    private String descripcion;

    private BigDecimal valor;


    @ManyToOne()
    @JoinColumn(name = "idEntidad", referencedColumnName = "idEntidad")
    private TsEntidadEntity entidad;


    /*@Column(name = "tipomovbc")
    private String tipomovbc;

    @Column(name = "chejercant")
    private String chejercant;*/


}
