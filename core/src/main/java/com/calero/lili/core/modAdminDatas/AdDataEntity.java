package com.calero.lili.core.modAdminDatas;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
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

    private UUID idConfiguracion;
}
