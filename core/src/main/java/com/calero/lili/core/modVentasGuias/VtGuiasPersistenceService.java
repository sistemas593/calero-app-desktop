package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class VtGuiasPersistenceService {


    private final VtGuiasRepository vtGuiasRepository;
    private final ComprobanteServiceImpl comprobanteService;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;

    @Transactional
    public VtGuiaEntity guardarGuiaRemision(VtGuiaEntity guiaEntity, AdEmpresaEntity empresa,
                                            AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa) {


        if (Objects.isNull(guiaEntity.getSecuencial())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(),
                    TipoDocumentoSerie.GRM);
            guiaEntity.setSecuencial(secuencial);
        }

        comprobanteService.getComprobanteXmlGuiaRemision(idData, guiaEntity, empresa, serie);
        return vtGuiasRepository.save(guiaEntity);
    }

}
