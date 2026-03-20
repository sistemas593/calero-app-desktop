package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLLiquidacionesGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.LiquidacionesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlLiquidacionesServiceImpl {

    // TODO REVISAR

    private final LiquidacionesRepository vtVentaRepository;
    //  private final SecurityUtils securityUtils;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;

    public CpComprasXMLLiquidacionesGetDto findXMLLiquidaccionById(Long idEmpresa, UUID id) {
     /*   UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        CpLiquidacionOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists",  id )));
        CpComprasXMLLiquidacionesGetDto dto = documentosElectronicosComprobanteBuilder.toLiquidacion(entidad);
        return dto;*/
        return null;
    }


}

