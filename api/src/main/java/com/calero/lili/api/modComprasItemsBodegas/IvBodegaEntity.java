package com.calero.lili.api.modComprasItemsBodegas;

import com.calero.lili.api.modAuditoria.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@Entity
@Table(name = "iv_bodegas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
public class IvBodegaEntity extends Auditable {

        @Column(name = "id_data")
        private Long idData;

        @Column(name = "id_empresa")
        private Long idEmpresa;

        @Column(name = "sucursal")
        private String sucursal;

        @Id
        @Column(unique = true, updatable = false, nullable = false)
        private UUID idBodega;

        @Column(name = "bodega")
        private String bodega;


}
