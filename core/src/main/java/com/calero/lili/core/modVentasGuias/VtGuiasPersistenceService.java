package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.MessageFormat;

@Service
@AllArgsConstructor
public class VtGuiasPersistenceService {


    private final VtGuiasRepository vtGuiasRepository;
    private final ComprobanteServiceImpl comprobanteService;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    @Transactional
    public VtGuiaEntity guardarGuiaRemision(VtGuiaEntity guiaEntity, AdEmpresaEntity empresa,
                                            AdEmpresasSeriesEntity serie, Long idData) {

        comprobanteService.getComprobanteXmlGuiaRemision(idData, guiaEntity, empresa, serie);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, guiaEntity.getIdEmpresa(), guiaEntity.getSerie(), TipoDocumentoSerie.GRM.name())
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe",
                        guiaEntity.getSerie(), guiaEntity.getSecuencial())));


        int nuevo = Integer.parseInt(guiaEntity.getSecuencial());
        documentosEntity.setSecuencial(nuevo);


        return vtGuiasRepository.save(guiaEntity);
    }

}
