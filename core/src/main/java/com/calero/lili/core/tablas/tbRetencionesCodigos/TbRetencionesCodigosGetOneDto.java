package com.calero.lili.core.tablas.tbRetencionesCodigos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbRetencionesCodigosGetOneDto {

    private String codigoRetencion;
    private String nombreRetencion;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;
    private BigDecimal porcentaje;
    private String codigo;

}
