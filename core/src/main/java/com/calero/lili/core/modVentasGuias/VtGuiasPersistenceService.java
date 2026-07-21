package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modVentasGuias.projection.OneProjection;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

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

        Optional<OneProjection> existingFactura = vtGuiasRepository.findExistBySecuencial(idData, idEmpresa,
                serie.getSerie(), guiaEntity.getSecuencial());


        if (existingFactura.isPresent()) {
            throw new GeneralException(MessageFormat.format("La Guia de remisión " +
                    "ya existe: Serie: {0} Secuencia: {1}", serie.getSerie(), guiaEntity.getSecuencial()));
        }

        comprobanteService.getComprobanteXmlGuiaRemision(idData, guiaEntity, empresa, serie);
        return vtGuiasRepository.save(guiaEntity);
    }

}
