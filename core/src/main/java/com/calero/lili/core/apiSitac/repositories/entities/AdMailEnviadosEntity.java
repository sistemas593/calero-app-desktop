package com.calero.lili.core.apiSitac.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ad_mails_enviados")
@RequiredArgsConstructor
public class AdMailEnviadosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 13)
    private String clave1;
    @Column(length = 2)
    private String codigoDocumento;
    @Column(length = 6)
    private String serie;
    @Column(length = 9)
    private String secuencial;
    @Column(length = 180)
    private String mailTo;
    private LocalDateTime fecha;
    private Long total;
}
