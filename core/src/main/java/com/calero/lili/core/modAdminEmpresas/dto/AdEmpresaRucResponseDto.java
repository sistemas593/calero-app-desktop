package com.calero.lili.core.modAdminEmpresas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdEmpresaRucResponseDto {

    private Long idEmpresa;
    private String razonSocial;

}
