package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.notaDebito.NotaDebito;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;import java.io.File;

public class UnmarshallingNotaDebitoGenerarPdf {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\NotasdeDébito-Sri.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(NotaDebito.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        NotaDebito fc = (NotaDebito) jaxbUnmarshaller.unmarshal(file);

        NotaDebitoPdf notaDebitoPdf = new NotaDebitoPdf();
        //notaDebitoPdf.generarPdf(fc, "121212121212", "01/01/2023");

    }

}
