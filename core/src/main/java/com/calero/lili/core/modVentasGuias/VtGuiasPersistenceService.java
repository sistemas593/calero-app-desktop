package com.calero.lili.core.modVentasGuias;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class VtGuiasPersistenceService {


    private final VtGuiasRepository vtGuiasRepository;


    @Transactional
    public VtGuiaEntity guardarGuiaRemision(VtGuiaEntity guiaEntity) {
        return vtGuiasRepository.save(guiaEntity);
    }

}
