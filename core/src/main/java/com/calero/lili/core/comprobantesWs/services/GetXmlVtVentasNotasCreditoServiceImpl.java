package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.modVentas.VtVentasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlVtVentasNotasCreditoServiceImpl {

    // TODO REVISAR

    private final VtVentasRepository vtVentaRepository;
    //private final SecurityUtils securityUtils;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public VtVentasXMLNotaCreditoGetDto findXMLNotaCreditoById(Long idEmpresa, UUID id) {
       /* UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        VtVentasXMLNotaCreditoGetDto dto = documentosElectronicosComprobanteBuilder.toNotaCreditoVenta(entidad);
        return dto;*/

        return null;
    }


}

