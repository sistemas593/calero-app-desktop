package com.calero.lili.api.tablas.tbSustentos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_sustentos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbSustentosEntity {
         
    @Id
    private String codigoSustento;
    private String sustento;

}