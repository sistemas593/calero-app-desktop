package com.calero.lili.core.modCompras.modComprasRetenciones;

import com.calero.lili.core.comprobantes.services.ComprobanteServiceImpl;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesEntity;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasRetenciones.dto.CreationRetencionRequestDto;
import com.calero.lili.core.utils.ValidacionDocumentosGeneral;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CpRetencionPersistenceService {

    private final ComprasRetencionesRepository comprasRetencionesRepository;
    private final CpImpuestosServiceImpl cpImpuestosService;
    private final ComprobanteServiceImpl comprobanteService;
    private final ValidacionDocumentosGeneral validacionDocumentosGeneral;

    @Transactional
    public CpRetencionesEntity guardarRetencion(CpRetencionesEntity entidad, AdEmpresaEntity empresa,
                                                AdEmpresasSeriesEntity serie, Long idData, Long idEmpresa,
                                                CreationRetencionRequestDto request) {


        if (Objects.isNull(entidad.getSecuencialRetencion())) {
            String secuencial = validacionDocumentosGeneral.generarSecuencial(idData, idEmpresa, serie.getIdSerie(),
                    TipoDocumentoSerie.CRT);
            entidad.setSecuencialRetencion(secuencial);
        }

        comprobanteService.getComprobanteXmlRetencion(idData, empresa, serie, entidad, request);
        return comprasRetencionesRepository.save(entidad);

    }

    @Transactional
    public CpRetencionesEntity actualizarRetencion(CpRetencionesEntity entidad, CreationRetencionRequestDto request) {
        return comprasRetencionesRepository.save(entidad);
    }





}
