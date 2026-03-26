package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.CpRetencionesOneProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlComprasRetencionesServiceImpl {


    private final ComprasRetencionesRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public CpComprasXMLRetencionGetDto findXMLRetencionById(Long idData, Long idEmpresa, UUID id) {
        CpRetencionesOneProjection entidad = vtVentaRepository
                .findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toRetencionCompra(entidad);
    }


}
