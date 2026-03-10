package com.calero.lili.api.modRRHH.modRRHHRublos.dto;

import com.calero.lili.core.enums.TipoRubro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RubroRequestDto {


    private String codigo;
    private String rubro;
    private TipoRubro tipo;
    private Boolean afectaIees;
    private Boolean afectaImpuestoRenta;
    private Boolean esObligatorio;

}
