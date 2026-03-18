package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.builder.CpImpuestoRecibirBuilder;
import com.calero.lili.core.comprobantes.message.MensajeComprobante;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeRecibidasServiceImpl {


    private final DeRecibidasComponentsServiceImpl deRecibidasComponentsService;
    private final CpImpuestoRecibirBuilder cpImpuestoRecibirBuilder;

    public CpImpuestosRecibirListCreationResponseDto createFiles(Long idData, Long idEmpresa,
                                                                 List<MultipartFile> files) {


        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();
        for (MultipartFile file : files) {
            String nameFile = XmlUtils.getNameForFile(file);


            CpImpuestosRecibirResponseDto res = cpImpuestoRecibirBuilder
                    .builder(nameFile, MensajeComprobante.NOT_ERROR, Boolean.TRUE, "");

            try {
                Autorizacion documento = XmlUtils.readFileXml(file);

                if (!deRecibidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa,
                        documento.getNumeroAutorizacion())) {

                    String message = deRecibidasComponentsService.guardarComprobante(idData, idEmpresa, documento);
                    res.setClaveAcceso(documento.getNumeroAutorizacion());

                    if (!message.isEmpty()) {
                        res = cpImpuestoRecibirBuilder
                                .builder(nameFile, message, Boolean.FALSE, documento.getNumeroAutorizacion());
                    }

                } else {
                    res = cpImpuestoRecibirBuilder
                            .builder(nameFile, MensajeComprobante.ERR_DOCUMENTO_EXISTE, Boolean.FALSE, documento.getNumeroAutorizacion());
                }

                listaRespuestas.add(res);

            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new GeneralException(ex.getMessage());
            }
        }

        return cpImpuestoRecibirBuilder.builderResponseList(listaRespuestas);
    }


}
