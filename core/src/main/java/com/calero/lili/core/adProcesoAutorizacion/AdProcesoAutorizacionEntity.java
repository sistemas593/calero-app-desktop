package com.calero.lili.core.adProcesoAutorizacion;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_proceso_autorizacion")
@Builder
public class AdProcesoAutorizacionEntity {

    @Id
    private String claveAcceso;

}
