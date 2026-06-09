package com.calero.lili.core.adInfoAdicional;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdInformacionAdicional {

    private String nombre;
    private String expresion;

}
