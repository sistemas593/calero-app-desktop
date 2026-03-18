package com.calero.lili.core.modCompras.modComprasLiquidaciones.comprobantes;

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
public class DocumentoRecibidosServiceImpl {



    private final CpImpuestoRecibirBuilder cpImpuestoRecibirBuilder;
    private final ReembolsoRecibidaServiceImpl reembolsoRecibidaService;


    public CpImpuestosRecibirListCreationResponseDto createFilesLiqReembolso(Long idData, List<MultipartFile> files) {

        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        for (MultipartFile file : files) {

            String nameFile = XmlUtils.getNameForFile(file);

            CpImpuestosRecibirResponseDto res = cpImpuestoRecibirBuilder
                    .builder(nameFile, MensajeComprobante.NOT_ERROR, Boolean.TRUE, "");


            try {

                Autorizacion documento = XmlUtils.readFileXml(file);

                if (!reembolsoRecibidaService.verificarExisteDocumentoElectronicoLiqReembolsoBdd(documento
                        .getNumeroAutorizacion())) {

                    if (!reembolsoRecibidaService.guardarComprobanteLiqReembolso(documento)) {
                        res = cpImpuestoRecibirBuilder
                                .builder(nameFile, MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO, Boolean.FALSE, documento.getNumeroAutorizacion());
                    }
                } else {
                    res = cpImpuestoRecibirBuilder.builder(nameFile,
                            MensajeComprobante.ERR_DOCUMENTO_EXISTE, Boolean.FALSE, documento.getNumeroAutorizacion());
                }

                listaRespuestas.add(res);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                throw new GeneralException(MensajeComprobante.ERR_GENERAL_DOCUMENTO);
            }
        }
        return cpImpuestoRecibirBuilder.builderResponseList(listaRespuestas);
    }


    public CpImpuestosRecibirListCreationResponseDto createFilesVentaReembolso(List<MultipartFile> files) {

        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        for (MultipartFile file : files) {

            String nameFile = XmlUtils.getNameForFile(file);

            CpImpuestosRecibirResponseDto res = cpImpuestoRecibirBuilder
                    .builder(nameFile, MensajeComprobante.NOT_ERROR, Boolean.TRUE, "");

            try {

                Autorizacion documento = XmlUtils.readFileXml(file);

                if (!reembolsoRecibidaService.verificarExisteDocumentoElectronicoVentaReembolsoBdd(documento
                        .getNumeroAutorizacion())) {

                    if (!reembolsoRecibidaService.guardarComprobanteVentaReembolso(documento)) {
                        res = cpImpuestoRecibirBuilder
                                .builder(nameFile, MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO, Boolean.FALSE,
                                        documento.getNumeroAutorizacion())
                        ;
                    }
                } else {
                    res = cpImpuestoRecibirBuilder.builder(nameFile,
                            MensajeComprobante.ERR_DOCUMENTO_EXISTE, Boolean.FALSE, documento.getNumeroAutorizacion());
                }

                listaRespuestas.add(res);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                throw new GeneralException(MensajeComprobante.ERR_GENERAL_DOCUMENTO);
            }
        }
        return cpImpuestoRecibirBuilder.builderResponseList(listaRespuestas);
    }

}
