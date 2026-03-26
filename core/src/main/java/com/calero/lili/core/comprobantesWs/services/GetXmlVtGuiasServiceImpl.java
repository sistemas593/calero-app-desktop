package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLGuiaRemisionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.VtGuiaRemisionOneProjection;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
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
public class GetXmlVtGuiasServiceImpl {


    private final VtGuiasRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;

    public VtVentasXMLGuiaRemisionGetDto findXMLGuiaById(Long idData, Long idEmpresa, UUID id) {
        VtGuiaRemisionOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toGuiaRemision(entidad);

    }

}

