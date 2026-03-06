package com.calero.lili.core.entities;

import jakarta.persistence.*;
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
public class AdDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idData;
    private String data;
    private LocalDate fechaCreacion;
    private Long siguienteIdEmpresa;

}
