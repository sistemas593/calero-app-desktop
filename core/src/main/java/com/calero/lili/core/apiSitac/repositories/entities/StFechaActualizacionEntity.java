package com.calero.lili.core.apiSitac.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "st_fecha_actualizacion")
public class StFechaActualizacionEntity {

    @Id
    @Column(name = "id_sistema")
    private String idSistema;

    @Column(name = "fecha_actualizacion")
    private String fechaActualizacion;

    @Column(name = "link")
    private String link;
}
