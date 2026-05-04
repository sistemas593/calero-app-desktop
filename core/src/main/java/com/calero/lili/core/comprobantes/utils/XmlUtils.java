package com.calero.lili.core.comprobantes.utils;


import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;
import com.calero.lili.core.comprobantes.services.dto.CampoAutorizacionDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtils {


    public static <T> T unmarshalXml(String xmlContent, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new StreamSource(new StringReader(xmlContent)), clazz).getValue();
    }

    public static <T> String convertToXmlString(Class<T> clazz, T objectToConvert) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            marshaller.marshal(objectToConvert, sw);
            return sw.toString();

        } catch (JAXBException e) {
            throw new GeneralException("Error converting object to XML: " + e.getMessage());
        }
    }


    public static String obtenerVersionXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            return document.getDocumentElement().getAttribute("version");

        } catch (Exception e) {
            throw new GeneralException("No se pudo obtener la versión del XML");
        }
    }

    public static Autorizacion readFileXml(MultipartFile file) {
        try {
            JAXBContext context = JAXBContext.newInstance(Autorizacion.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Autorizacion) unmarshaller.unmarshal(file.getInputStream());

        } catch (Exception ex) {

            throw new GeneralException("No se puedo leer el archivo");
        }
    }

    public static String getNameForFile(MultipartFile file) {
        return file.getOriginalFilename();
    }


    public static Factura getFactura(Autorizacion autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public static Factura getFacturaRecibidos(CampoAutorizacionDto autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), Factura.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public static NotaCredito getNotaCredito(Autorizacion autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), NotaCredito.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public static NotaCredito getNotaCreditoRecibida(CampoAutorizacionDto autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), NotaCredito.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public static NotaDebito getNotaDebito(CampoAutorizacionDto autorizacionDto) {
        try {
            return XmlUtils.unmarshalXml(autorizacionDto.getComprobante(), NotaDebito.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public static <T> T readMultipartFileXml(MultipartFile file, Class<T> clazz) {
        try (InputStream is = file.getInputStream()) {

            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (T) unmarshaller.unmarshal(is);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new GeneralException("No se pudo leer el XML");
        }
    }


}
