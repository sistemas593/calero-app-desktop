package com.calero.lili.core.modTesoreria.modTesoreriaCajas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras.TsEntidadEntity;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ts_cajas")
@Builder
@Where(clause = "deleted = false")
public class TsCajasEntity extends Auditable {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idCaja;


    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;


    private String codigoCaja;

    private String nombre;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entidad")
    private TsEntidadEntity entidad;
}
