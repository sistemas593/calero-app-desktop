package com.calero.lili.core.modAdminDatas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdDataResponseConfiguracionDto {

    private Long idData;
    private UUID idConfiguracion;
}
