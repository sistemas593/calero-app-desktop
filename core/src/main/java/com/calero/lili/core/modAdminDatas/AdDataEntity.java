package com.calero.lili.core.modAdminDatas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modTerceros.GeTerceroEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@Entity
@Table(name = "ad_datas")
@AllArgsConstructor
@NoArgsConstructor
public class AdDataEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idData;

    private String data;

    private LocalDate fechaCreacion;

    private Long siguienteIdEmpresa;

    private String clave;
}
