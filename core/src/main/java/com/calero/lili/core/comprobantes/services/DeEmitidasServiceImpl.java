package com.calero.lili.core.comprobantes.services;

import com.calero.lili.core.comprobantes.builder.CpImpuestoRecibirBuilder;
import com.calero.lili.core.comprobantes.message.ConstantesDocumento;
import com.calero.lili.core.comprobantes.message.MensajeComprobante;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.builder.CampoAutorizacionBuilder;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.comprobantes.services.dto.DocumentoDto;
import com.calero.lili.core.comprobantes.utils.XmlUtils;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeEmitidasServiceImpl {

    private final DeEmitidasComponentsServiceImpl deEmitidasComponentsService;
    private final CpImpuestoRecibirBuilder cpImpuestoRecibirBuilder;
    private final CampoAutorizacionBuilder campoAutorizacionBuilder;


    public CpImpuestosRecibirListCreationResponseDto createFiles(Long idData, Long idEmpresa,
                                                                 List<MultipartFile> files, String usuario) {
        String sucursal = "001";
        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        for (MultipartFile file : files) {

            String tipoFormato = XmlUtils.validarTipoFormatoDoc(file);
            String nameFile = getNameForFile(file);

            CpImpuestosRecibirResponseDto res = cpImpuestoRecibirBuilder
                    .builder(nameFile, MensajeComprobante.NOT_ERROR, Boolean.TRUE, "");

            try {
                CampoAutorizacionDto model = null;
                if (tipoFormato.equals("1")) {

                    Autorizacion documento = XmlUtils.readFileXml(file);
                    model = campoAutorizacionBuilder.builder(documento);
                    model.setFormatoDocumento(tipoFormato);

                } else if (tipoFormato.equals("2")) {

                    DocumentoDto documento = obtenerTipoDocumento(file, nameFile);
                    switch (documento.getTipoDocumento()) {
                        case "factura": {
                            Factura factura = XmlUtils.readMultipartFileXml(file, Factura.class);
                            model = campoAutorizacionBuilder.builderFactura(factura);
                            model.setFormatoDocumento(tipoFormato);
                            break;
                        }

                        case "notaCredito": {
                            NotaCredito notaCredito = XmlUtils.readMultipartFileXml(file, NotaCredito.class);
                            model = campoAutorizacionBuilder.builderNotaCredito(notaCredito);
                            model.setFormatoDocumento(tipoFormato);
                            break;
                        }

                        case "notaDebito": {
                            NotaDebito notaDebito = XmlUtils.readMultipartFileXml(file, NotaDebito.class);
                            model = campoAutorizacionBuilder.builderNotaDebito(notaDebito);
                            model.setFormatoDocumento(tipoFormato);
                            break;
                        }


                        case "comprobanteRetencion": {
                            if (ConstantesDocumento.VERSION_1_0_0.equals(documento.getVersionDocumento())) {
                                com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion retencionUno = XmlUtils.readMultipartFileXml(file, com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion.class);
                                model = campoAutorizacionBuilder.builderComprobanteRetencionV1(retencionUno);
                                model.setFormatoDocumento(tipoFormato);
                                break;
                            } else {
                                ComprobanteRetencion retencionDos = XmlUtils.readMultipartFileXml(file, ComprobanteRetencion.class);
                                model = campoAutorizacionBuilder.builderComprobanteRetencionV2(retencionDos);
                                model.setFormatoDocumento(tipoFormato);
                                model.setVersionDocumento(documento.getVersionDocumento());
                                break;
                            }
                        }


                        default:
                            throw new GeneralException("Tipo de documento no reconocido en el archivo: " + nameFile);

                    }

                }

                if (!deEmitidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa, model.getNumeroAutorizacion())) {

                    String message = deEmitidasComponentsService.guardarComprobante(idData, idEmpresa, model, sucursal, usuario);
                    res.setClaveAcceso(model.getNumeroAutorizacion());

                    if (!message.isEmpty()) {
                        res = cpImpuestoRecibirBuilder.builder(nameFile, message,
                                Boolean.FALSE, model.getNumeroAutorizacion());
                    }
                } else {
                    res = cpImpuestoRecibirBuilder.builder(nameFile,
                            MensajeComprobante.ERR_DOCUMENTO_EXISTE, Boolean.FALSE, model.getNumeroAutorizacion());
                }

                listaRespuestas.add(res);

            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new GeneralException(ex.getMessage());
            }

        }

        return cpImpuestoRecibirBuilder.builderResponseList(listaRespuestas);
    }

    private String getNameForFile(MultipartFile file) {
        return file.getOriginalFilename();
    }

    private DocumentoDto obtenerTipoDocumento(MultipartFile file, String nombreDocumento) {
        try (InputStream is = file.getInputStream()) {

            DocumentoDto dto = new DocumentoDto();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            dto.setTipoDocumento(doc.getDocumentElement().getNodeName());
            dto.setVersionDocumento(doc.getXmlVersion());
            return dto;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new GeneralException("Error al leer XML con nombre: " + nombreDocumento);
        }
    }


}
