package com.calero.lili.core.comprobantes.objetosXml;

import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class XmlToObject {
    public static void main(String[] args) throws JAXBException {

//        File file = new File("Factura.xml");
//
//        JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        Factura fc = (Factura) jaxbUnmarshaller.unmarshal(file);
//        System.out.println("Si se pudo leer el XML y convertirlo en Objeto Factura: "+fc.getInfoFactura().getComercioExterior());

        File file = new File("GuiaRemision.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(GuiaRemision.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GuiaRemision fc = (GuiaRemision) jaxbUnmarshaller.unmarshal(file);
        System.out.println("Si se pudo leer el XML y convertirlo en Objeto Factura: ");

    }

}
