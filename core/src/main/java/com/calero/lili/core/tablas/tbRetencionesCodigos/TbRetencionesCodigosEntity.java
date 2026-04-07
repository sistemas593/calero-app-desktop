package com.calero.lili.core.tablas.tbRetencionesCodigos;

import com.calero.lili.core.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tb_retenciones_codigos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionesCodigosEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoRetencion;
    private String nombreRetencion;
    private String codigo;
    //1 RETENCION RENTA
    //2 RETENCION IVA
    //3 ISD
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;
    private BigDecimal porcentaje;

}