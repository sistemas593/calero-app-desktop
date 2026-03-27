package com.calero.lili.core.modAdminDatas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdDatasDto {

    private Long idData;
    private String data;
    private UUID idConfiguracion;

}
