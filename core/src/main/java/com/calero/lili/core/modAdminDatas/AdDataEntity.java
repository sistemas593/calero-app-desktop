package com.calero.lili.core.modAdminDatas;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.modAdModulos.AdModulosEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
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


    @ManyToMany
    @JoinTable(
            name = "ad_datas_modulos",
            joinColumns = @JoinColumn(name = "id_data"),
            inverseJoinColumns = @JoinColumn(name = "id_modulo"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_data", "id_modulo"})})
    private List<AdModulosEntity> modulos;
}
