package com.calero.lili.core.modAdminDatas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdDataResponseConfiguracionDto {

    private Long idData;
    private String clave;
}
