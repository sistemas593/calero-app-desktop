package com.calero.lili.core.comprobantes.objetosXml.factura;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class UnmarshallingFactura {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\test01.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Factura fc = (Factura) jaxbUnmarshaller.unmarshal(file);

    }

}
