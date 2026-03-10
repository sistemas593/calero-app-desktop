package com.calero.lili.api.tablas.tbRetenciones;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_retenciones")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionEntity {
         
    @Id
    private String codigo;
    //1 RETENCION RENTA
    //2 RETENCION IVA
    //3 ISD

    private String nombreRetencion;

}