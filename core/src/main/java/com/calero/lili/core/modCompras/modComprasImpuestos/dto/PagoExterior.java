package com.calero.lili.core.modCompras.modComprasImpuestos.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoExterior {



    private String tipoRegi; // validar si es 01,02,03

    private String paisEfecPagoGen; // 01
    private String paisEfecPagoParFis; // 02 1
    private String denoPagoRegFis;// 03
    private String paisEfecPago;
    private String nombrePaisEfecPago;

    private Boolean pagoRegFis; // siempre ser falso
    private Boolean aplicConvDovTrib;
    private Boolean pagoExtSujRetNorLeg;


    // Validar en base al tipoRegi

    // todo nombres de los paises paisEfecPagoGen y paisEfecPagoParFis

}
