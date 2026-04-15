package com.calero.lili.core.adProcesoAutorizacion;

import org.springframework.stereotype.Component;

@Component
public class AdProcesoAutorizacionBuilder {

    public AdProcesoAutorizacionEntity builder(String claveAcceso) {
        return AdProcesoAutorizacionEntity.builder()
                .claveAcceso(claveAcceso)
                .build();
    }
}
