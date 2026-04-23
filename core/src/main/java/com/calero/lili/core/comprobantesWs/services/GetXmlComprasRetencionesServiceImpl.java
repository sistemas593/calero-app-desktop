package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import com.calero.lili.core.comprobantesPdf.ComprobanteRetencionPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.CpRetencionesOneProjection;
import com.calero.lili.core.modCompras.impuestosXml.VtRetencionesOneProjection;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
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
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetXmlComprasRetencionesServiceImpl {


    private final ComprasRetencionesRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final ComprobanteRetencionPdf facturaPdf;


    public CpComprasXMLRetencionGetDto findXMLRetencionById(Long idData, Long idEmpresa, UUID id) {
        CpRetencionesOneProjection entidad = vtVentaRepository
                .findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toRetencionCompra(entidad);
    }


    public ArchivoDto findPDFRetencionById(Long idData, Long idEmpresa, UUID id, String origenCertificado) {

        System.out.println("Obtener PDF");

        DatosEmpresaDto datosEmpresaDto = null;


        CpRetencionesOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarRetencion(entidad);

        String nombreArchivo = "E-RET-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";

        switch (origenCertificado) {

            case "WEB" -> datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

            case "LOC" -> datosEmpresaDto = buscarDatosEmpresa.obtenerLocalDatosEmpresa(idData, idEmpresa);
        }

        ComprobanteRetencion documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(ComprobanteRetencion.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (ComprobanteRetencion) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (JAXBException ex) {
            System.out.println("error 1");
            throw new GeneralException("No se pudo convetir el comprobante");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(facturaPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        datosEmpresaDto.getImageBytes()))
                .build();
    }


    public ArchivoDto findFileXMLRetencion(Long idData, Long idEmpresa, UUID id) {


        CpRetencionesOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));


        validarRetencion(entidad);
        String nombreArchivo = "E-RET-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

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


    }


    private void validarRetencion(CpRetencionesOneProjection entidad) {
        if (!entidad.getNumeroAutorizacion().startsWith("07", 8)) {
            throw new GeneralException("El documento con id " + entidad.getIdRetencion() + " no es una factura");
        }

        if (Objects.isNull(entidad.getComprobante()) || entidad.getComprobante().isEmpty()) {
            throw new GeneralException("El documento no contiene un comprobante");
        }

        if (!entidad.getEstadoDocumento().equals(EstadoDocumento.AUT.name())) {
            throw new GeneralException("El documento con id {0} no esta autorizado " + entidad.getIdRetencion());
        }
    }

}
