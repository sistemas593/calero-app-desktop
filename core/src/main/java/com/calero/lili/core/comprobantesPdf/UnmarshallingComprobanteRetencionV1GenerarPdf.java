package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1.ComprobanteRetencion;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

public class UnmarshallingComprobanteRetencionV1GenerarPdf {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\ComprobantedeRetención-SriV1.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(ComprobanteRetencion.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ComprobanteRetencion fc = (ComprobanteRetencion) jaxbUnmarshaller.unmarshal(file);

        ComprobanteRetencionV1Pdf comprobanteRetencionV1Pdf = new ComprobanteRetencionV1Pdf();
        //comprobanteRetencionV1Pdf.generarPdf(fc, "121212121212", "01/01/2023");

    }

}
