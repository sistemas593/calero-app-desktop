package com.calero.lili.core.comprobantes.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampoAutorizacionDto {

    private String comprobante;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
}
