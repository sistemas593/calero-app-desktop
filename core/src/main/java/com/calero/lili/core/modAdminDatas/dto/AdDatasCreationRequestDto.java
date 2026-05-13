package com.calero.lili.core.modAdminDatas.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdDatasCreationRequestDto {

    private String data;
    private UUID idConfiguracion;
    private List<Long> idsModulos;
}
