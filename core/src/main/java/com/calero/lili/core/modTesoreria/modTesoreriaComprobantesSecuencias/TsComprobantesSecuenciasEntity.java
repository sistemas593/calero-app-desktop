package com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.enums.TipoComprobante;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_comprobantes_secuencias")
@Builder
@Where(clause = "deleted = false")
public class TsComprobantesSecuenciasEntity extends Auditable {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idComprobanteSecuencia;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Enumerated(EnumType.STRING)
    private TipoComprobante tipo;

    private Integer anio;

    private Integer ultimoNumero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cajas")
    private TsCajasEntity cajas;

}
