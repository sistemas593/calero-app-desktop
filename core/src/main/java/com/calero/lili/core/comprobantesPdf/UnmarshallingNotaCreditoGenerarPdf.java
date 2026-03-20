package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.notaCredito.NotaCredito;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class UnmarshallingNotaCreditoGenerarPdf {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\Web\\calero-app-back\\Fegenerados\\nc.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(NotaCredito.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        NotaCredito fc = (NotaCredito) jaxbUnmarshaller.unmarshal(file);

        NotaCreditoPdf notaCreditoPdf = new NotaCreditoPdf();
        //notaCreditoPdf.generarPdf(fc, "121212121212", "01/01/2023");

    }

}
