package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlComprasRetencionesServiceImpl {


     // TODO REVISAR
    private final ComprasRetencionesRepository vtVentaRepository;
    // private final SecurityUtils securityUtils;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public CpComprasXMLRetencionGetDto findXMLRetencionById(Long idEmpresa, UUID id) {
       /* UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        CpRetencionesOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists",  id )));
        CpComprasXMLRetencionGetDto dto = documentosElectronicosComprobanteBuilder.toRetencionCompra(entidad);
        return dto;*/
        return null;
    }


}
