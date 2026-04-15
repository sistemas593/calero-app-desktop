package com.calero.lili.core.adProcesoAutorizacion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdProcesoAutorizacionService {

    private final AdProcesoAutorizacionRepository repository;
    private final AdProcesoAutorizacionBuilder adProcesoAutorizacionBuilder;

    public void create(String claveAcceso) {
        repository.save(adProcesoAutorizacionBuilder.builder(claveAcceso));
    }

    public Boolean existsByClaveAcceso(String claveAcceso) {
        return repository.existsById(claveAcceso);
    }

    public void deleteByClaveAcceso(String claveAcceso) {
        repository.deleteById(claveAcceso);
    }


}
