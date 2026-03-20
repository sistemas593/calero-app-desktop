package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion.ComprobanteRetencion;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class UnmarshallingComprobanteRetencionGenerarPdf {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\Comprobanteretencionv2.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(ComprobanteRetencion.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ComprobanteRetencion fc = (ComprobanteRetencion) jaxbUnmarshaller.unmarshal(file);

        ComprobanteRetencionPdf comprobanteRetencionPdf = new ComprobanteRetencionPdf();
       // comprobanteRetencionPdf.generarPdf(fc, "121212121212", "01/01/2023");

    }

}
