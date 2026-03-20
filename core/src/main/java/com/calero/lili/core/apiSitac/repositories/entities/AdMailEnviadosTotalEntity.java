package com.calero.lili.core.apiSitac.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "ad_mails_enviados_total")
@RequiredArgsConstructor
public class AdMailEnviadosTotalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 7)
    private String periodo;
    @Column(length = 13)
    private String clave1;

    private Long total;

}
