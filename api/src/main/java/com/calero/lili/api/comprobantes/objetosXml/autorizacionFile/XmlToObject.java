package com.calero.lili.api.comprobantes.objetosXml.autorizacionFile;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class XmlToObject {
    public static void main(String[] args) throws JAXBException {
//        File file = new File("FacturaAutorizada.xml");
//        JAXBContext jaxbContext = JAXBContext.newInstance(Autorizacion.class);
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        Autorizacion au = (Autorizacion) jaxbUnmarshaller.unmarshal(file);
//        System.out.println("Si se pudo leer el XML y convertirlo en Objeto Autorizacion: ");
//        System.out.println(au.getEstado());

        File file = new File("GuiaRemisionAutorizada.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(Autorizacion.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Autorizacion au = (Autorizacion) jaxbUnmarshaller.unmarshal(file);
        System.out.println("Si se pudo leer el XML y convertirlo en Objeto Autorizacion: ");
        System.out.println(au.getEstado());

    }
}
