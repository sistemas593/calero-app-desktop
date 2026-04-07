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

    private String paisEfecPagoGen;
    private String nombrePaisEfecPagoGen;

    private String paisEfecPagoParFis;
    private String nombrePaisEfecPagoParFis;

    private String denopagoRegFis;

    private String paisEfecPago;
    private String nombrePaisEfecPago;

    private Boolean pagoRegFis = false;
    private Boolean aplicConvDobTrib;
    private Boolean pagExtSujRetNorLeg;


}
