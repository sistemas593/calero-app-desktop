package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlCpImpuestosServiceImpl {

    // TODO REVISAR

    private final CpImpuestosRepository vtVentaRepository;
    //  private final SecurityUtils securityUtils;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public CpImpuestosXMLFacturaGetDto findXMLFacturaById(Long idEmpresa, UUID id) {
       /* UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        CpImpuestosXMLFacturaGetDto dto = documentosElectronicosComprobanteBuilder.toFacturaCompra(entidad);
        return dto;*/

        return null;
    }


    public CpImpuestosXMLNotaCreditoGetDto findXMLNotaCreditoById(Long idEmpresa, UUID id) {
       /* UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        CpImpuestosXMLNotaCreditoGetDto dto = documentosElectronicosComprobanteBuilder.toNotaCreditoCompra(entidad);

        return dto;*/
        return null;
    }

    public CpImpuestosXMLNotaDebitoGetDto findXMLNotaDebitoById(Long idEmpresa, UUID id) {
      /*  UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        CpImpuestosXMLNotaDebitoGetDto dto = documentosElectronicosComprobanteBuilder.toNotaDebitoCompra(entidad);

        return dto;*/
        return null;
    }

}

