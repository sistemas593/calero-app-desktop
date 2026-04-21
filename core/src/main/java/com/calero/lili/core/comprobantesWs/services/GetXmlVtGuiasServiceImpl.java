package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import com.calero.lili.core.comprobantesPdf.GuiaRemisionPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLGuiaRemisionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.VtGuiaRemisionOneProjection;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlVtGuiasServiceImpl {


    private final VtGuiasRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final GuiaRemisionPdf guiaRemisionPdf;

    public VtVentasXMLGuiaRemisionGetDto findXMLGuiaById(Long idData, Long idEmpresa, UUID id) {
        VtGuiaRemisionOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toGuiaRemision(entidad);

    }


    public ArchivoDto findPDFGuiaById(Long idData, Long idEmpresa, UUID id, String origenCertificado) {

        System.out.println("Obtener PDF");

        DatosEmpresaDto datosEmpresaDto = null;


        VtGuiaRemisionOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        if (!entidad.getEstadoDocumento().equals(EstadoDocumento.AUT.name())) {
            throw new GeneralException("El documento con id {0} no esta autorizado " + id);
        }

        String nombreArchivo = "GRM-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";

        switch (origenCertificado) {

            case "WEB" -> datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

            case "LOC" -> datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(idData, idEmpresa);
        }

        GuiaRemision documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(GuiaRemision.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (GuiaRemision) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (JAXBException ex) {
            System.out.println("error 1");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(guiaRemisionPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        datosEmpresaDto.getImageBytes()))
                .build();
    }


    public ArchivoDto findFileXMLGuia(Long idData, Long idEmpresa, UUID id) {


        VtGuiaRemisionOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));


        if (entidad.getEstadoDocumento().equals(EstadoDocumento.AUT.name())) {

            String nombreArchivo = "GRM-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

            Autorizacion aut = new Autorizacion();
            aut.setComprobante(entidad.getComprobante()); //"<![CDATA[" + + "]]>"
            aut.setFechaAutorizacion(entidad.getFechaAutorizacion());
            aut.setNumeroAutorizacion(entidad.getNumeroAutorizacion());
            aut.setEstado("AUTORIZADO");

            try {
                JAXBContext context = JAXBContext.newInstance(new Class[]{Autorizacion.class});
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.encoding", "UTF-8");
                marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(aut, stringWriter);

                return ArchivoDto.builder()
                        .nombre(nombreArchivo)
                        .contenido(stringWriter.toString().getBytes())
                        .build();

            } catch (Exception ex) {
                throw new GeneralException("Existe un error: " + ex.getMessage());
            }

        } else {
            throw new GeneralException(MessageFormat.format("El documento con id {0} " +
                    "no esta autorizado ", id));
        }
    }
}

