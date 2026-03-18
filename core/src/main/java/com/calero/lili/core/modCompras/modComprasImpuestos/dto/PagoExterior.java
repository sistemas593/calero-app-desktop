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

    private String paisEfecPago;
    private String aplicConvDobTrib;
    private String pagExtSujRetNorLeg;

}
