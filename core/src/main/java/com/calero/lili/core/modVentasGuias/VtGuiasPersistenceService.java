package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modVentasGuias.dto.CreationRequestGuiaRemisionDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.MessageFormat;

@Service
@AllArgsConstructor
public class VtGuiasPersistenceService {


    private final VtGuiasRepository vtGuiasRepository;
    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;


    @Transactional
    public VtGuiaEntity guardarGuiaRemision(VtGuiaEntity guiaEntity, CreationRequestGuiaRemisionDto request,
                                            Long idData, Long idEmpresa) {

        VtGuiaEntity saved = vtGuiasRepository.save(guiaEntity);

        AdEmpresasSeriesDocumentosEntity documentosEntity = adEmpresasSeriesDocumentosRepository
                .findBySerieAndDocumento(idData, idEmpresa, request.getSerie(), "GRM")
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Serie {0}, documento {1} no existe", request.getSerie(), request.getSecuencial())));


        int nuevo = Integer.parseInt(request.getSecuencial()) + 1;
        String sec = request.getSecuencial();
        DecimalFormat df = new DecimalFormat(sec.replaceAll("[1-9]", "0"));
        documentosEntity.setSecuencial(df.format(nuevo));

        return saved;

    }

}
