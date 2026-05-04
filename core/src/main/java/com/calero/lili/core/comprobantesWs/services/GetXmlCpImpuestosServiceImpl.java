package com.calero.lili.core.comprobantesWs.services;


import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantesPdf.FacturaPdf;
import com.calero.lili.core.comprobantesPdf.NotaCreditoPdf;
import com.calero.lili.core.comprobantesPdf.NotaDebitoPdf;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.builder.DocumentosElectronicosComprobanteBuilder;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modCompras.impuestosXml.CpImpuestosFacturasOneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosRepository;
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
public class GetXmlCpImpuestosServiceImpl {

    // TODO REVISAR

    private final CpImpuestosRepository vtVentaRepository;
    private final DocumentosElectronicosComprobanteBuilder documentosElectronicosComprobanteBuilder;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
    private final FacturaPdf facturaPdf;
    private final NotaCreditoPdf notaCreditoPdf;
    private final NotaDebitoPdf notaDebitoPdf;


    /*public CpImpuestosXMLFacturaGetDto findXMLFacturaById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toFacturaCompra(entidad);
    }


    public CpImpuestosXMLNotaCreditoGetDto findXMLNotaCreditoById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toNotaCreditoCompra(entidad);
    }

    public CpImpuestosXMLNotaDebitoGetDto findXMLNotaDebitoById(Long idData, Long idEmpresa, UUID id) {
        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id).orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));
        return documentosElectronicosComprobanteBuilder.toNotaDebitoCompra(entidad);

    }*/


    // FACTURAS

    public ArchivoDto findPDFFacturaById(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Obtener PDF");

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarFactura(entidad);

        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "FAC" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";

        Factura documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(Factura.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (Factura) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: " + documento.getInfoFactura().getComercioExterior());
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
                        null))
                .build();
    }


    public ArchivoDto findFileXMLFactura(Long idData, Long idEmpresa, UUID id) {

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarFactura(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "FAC" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

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

    // NOTAS CREDITO


    public ArchivoDto findPDFNotaCreditoById(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Obtener PDF");

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaCredito(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "NCR" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";


        NotaCredito documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(NotaCredito.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (NotaCredito) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (JAXBException ex) {
            System.out.println("error 1");
            throw new GeneralException("No se pudo convetir el comprobante");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(notaCreditoPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        null))
                .build();
    }


    public ArchivoDto findFileXMLNotaCredito(Long idData, Long idEmpresa, UUID id) {


        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaCredito(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "NCR" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

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


    // NOTAS DEBITO


    public ArchivoDto findPDFNotaDebitoById(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Obtener PDF");

        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaDebito(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "NDB" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".pdf";

        NotaDebito documento = null;
        JAXBContext jaxbContext1 = null;
        Unmarshaller jaxbUnmarshaller1 = null;

        try {
            jaxbContext1 = JAXBContext.newInstance(NotaDebito.class);
            jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
            documento = (NotaDebito) jaxbUnmarshaller1.unmarshal(new StringReader(entidad.getComprobante()));
            System.out.println("Si se pudo leer el String y convertirlo en objeto Factura: ");
        } catch (JAXBException ex) {
            System.out.println("error 1");
            throw new GeneralException("No se pudo convetir el comprobante");
        }


        return ArchivoDto.builder()
                .nombre(nombreArchivo)
                .contenido(notaDebitoPdf.generarPdf(
                        documento,
                        entidad.getNumeroAutorizacion() == null ? "" : entidad.getNumeroAutorizacion(),
                        entidad.getFechaAutorizacion() == null ? "" : entidad.getFechaAutorizacion(),
                        null))
                .build();
    }


    public ArchivoDto findFileXMLNotaDebito(Long idData, Long idEmpresa, UUID id) {


        CpImpuestosFacturasOneProjection entidad = vtVentaRepository.findXMLById(idData, idEmpresa, id)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("Id {0} no exists", id)));

        validarNotaDebito(entidad);


        String nombreArchivo = "R-" + entidad.getNumeroIdentificacion() + "-" + "NDB" + "-" + entidad.getSerie() + "-" + entidad.getSecuencial() + ".xml";

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

    private void validacionComprobante(CpImpuestosFacturasOneProjection entidad) {
        if (Objects.isNull(entidad.getComprobante()) || entidad.getComprobante().isEmpty()) {
            throw new GeneralException("El documento no contiene un comprobante");
        }
    }

    private void validarFactura(CpImpuestosFacturasOneProjection entidad) {

        if (Objects.isNull(entidad.getNumeroAutorizacion()) || entidad.getNumeroAutorizacion().isEmpty()) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no tiene un número de autorización válido", entidad.getNumeroAutorizacion()));
        }

        if (!entidad.getNumeroAutorizacion().startsWith("01", 8)) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no corresponde a una factura", entidad.getIdImpuestos()));
        }
        validacionComprobante(entidad);
    }

    private void validarNotaCredito(CpImpuestosFacturasOneProjection entidad) {

        if (Objects.isNull(entidad.getNumeroAutorizacion()) || entidad.getNumeroAutorizacion().isEmpty()) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no tiene un número de autorización válido", entidad.getNumeroAutorizacion()));
        }

        if (!entidad.getNumeroAutorizacion().startsWith("04", 8)) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no corresponde a una nota de credito", entidad.getIdImpuestos()));
        }
        validacionComprobante(entidad);
    }

    private void validarNotaDebito(CpImpuestosFacturasOneProjection entidad) {

        if (Objects.isNull(entidad.getNumeroAutorizacion()) || entidad.getNumeroAutorizacion().isEmpty()) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no tiene un número de autorización válido", entidad.getNumeroAutorizacion()));
        }

        if (!entidad.getNumeroAutorizacion().startsWith("05", 8)) {
            throw new GeneralException(MessageFormat.format("El documento con id {0} no corresponde a una nota de debito", entidad.getIdImpuestos()));
        }
        validacionComprobante(entidad);
    }
}







