package com.calero.lili.core.modVentas.reembolsos;

import com.calero.lili.core.comprobantes.builder.CpImpuestoRecibirBuilder;
import com.calero.lili.core.comprobantes.message.MensajeComprobante;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.comprobantes.ReembolsoRecibidaServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class VentasReembolsoRecibidosServiceImpl {


    private final CpImpuestoRecibirBuilder cpImpuestoRecibirBuilder;
    private final ReembolsoRecibidaServiceImpl reembolsoRecibidaService;


    public CpImpuestosRecibirListCreationResponseDto createFilesVentaReembolso(List<MultipartFile> files,
                                                                               Long idData, Long idEmpresa, String usuario) {

        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        for (MultipartFile file : files) {

            String nameFile = XmlUtils.getNameForFile(file);

            CpImpuestosRecibirResponseDto res = cpImpuestoRecibirBuilder
                    .builder(nameFile, MensajeComprobante.NOT_ERROR, Boolean.TRUE, "");

            try {

                Autorizacion documento = XmlUtils.readFileXml(file);

                if (!reembolsoRecibidaService.verificarExisteDocumentoElectronicoVentaReembolsoBdd(idData, idEmpresa, documento
                        .getNumeroAutorizacion())) {

                    res.setClaveAcceso(documento.getNumeroAutorizacion());

                    if (!reembolsoRecibidaService.guardarComprobanteVentaReembolso(documento, idData, idEmpresa, usuario)) {
                        res = cpImpuestoRecibirBuilder
                                .builder(nameFile, MensajeComprobante.ERR_LEER_DOCUMENTO_INTERNO, Boolean.FALSE,
                                        documento.getNumeroAutorizacion());
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
