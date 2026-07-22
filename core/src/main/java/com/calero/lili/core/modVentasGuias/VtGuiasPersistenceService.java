package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class VtGuiasPersistenceService {


    private final VtGuiasRepository vtGuiasRepository;
    private final ComprobanteServiceImpl comprobanteService;

    @Transactional
    public VtGuiaEntity guardarGuiaRemision(VtGuiaEntity guiaEntity, AdEmpresaEntity empresa,
                                            AdEmpresasSeriesEntity serie, Long idData) {


        comprobanteService.getComprobanteXmlGuiaRemision(idData, guiaEntity, empresa, serie);
        return vtGuiasRepository.save(guiaEntity);
    }

}
