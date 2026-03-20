package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.guiaRemision.GuiaRemision;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UnmarshallingGuiaRemisionGenerarPdf {
    public static void main(String[] args) throws JAXBException {

        File fileLogo = new File("logo.jpg");

        byte[] imageBytes = null;
        try {
            imageBytes = Files.readAllBytes(fileLogo.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\GuíasdeRemisión-Sri.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(GuiaRemision.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GuiaRemision fc = (GuiaRemision) jaxbUnmarshaller.unmarshal(file);

        GuiaRemisionPdf guiaRemisionPdf = new GuiaRemisionPdf();
        guiaRemisionPdf.generarPdf(fc, "121212121212", "01/01/2023", imageBytes);

    }

}
