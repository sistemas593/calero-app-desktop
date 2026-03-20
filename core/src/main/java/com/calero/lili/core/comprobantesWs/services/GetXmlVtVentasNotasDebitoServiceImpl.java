package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.modCompras.impuestosXml.VtVentasFacturaOneProjection;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.errors.exceptions.GeneralException;
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
public class GetXmlVtVentasNotasDebitoServiceImpl {

    // TODO REVISAR

    private final VtVentasRepository vtVentaRepository;
    // private final SecurityUtils securityUtils;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;


    public VtVentasXMLNotaDebitoGetDto findXMLNotaDebitoById(Long idEmpresa, UUID id) {
        /*UsuarioSecurity user = securityUtils.getUser();
        String area = user.getArea();
        Long idData = user.getData();
        VtVentasFacturaOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        VtVentasXMLNotaDebitoGetDto dto = documentosElectronicosComprobanteBuilder.toNotaDebitoVenta(entidad);
        return dto;*/

        return null;
    }


}

