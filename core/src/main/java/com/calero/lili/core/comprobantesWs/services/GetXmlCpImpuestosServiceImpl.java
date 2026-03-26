package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.CpImpuestosFacturasOneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
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
public class GetXmlCpImpuestosServiceImpl {

    // TODO REVISAR

    private final CpImpuestosRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public CpImpuestosXMLFacturaGetDto findXMLFacturaById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toFacturaCompra(entidad);
    }


    public CpImpuestosXMLNotaCreditoGetDto findXMLNotaCreditoById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toNotaCreditoCompra(entidad);
    }

    public CpImpuestosXMLNotaDebitoGetDto findXMLNotaDebitoById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toNotaDebitoCompra(entidad);

    }

}

