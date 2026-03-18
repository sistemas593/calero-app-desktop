package com.calero.lili.core.modRRHH.modRRHHRublos.dto;

import com.calero.lili.core.enums.TipoRubro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RubroResponseDto {

    private UUID idRubro;
    private String codigo;
    private Long idEmpresa;
    private Long idData;
    private String rubro;
    private TipoRubro tipo;
    private Boolean afectaIees;
    private Boolean afectaImpuestoRenta;
    private Boolean esObligatorio;

}
