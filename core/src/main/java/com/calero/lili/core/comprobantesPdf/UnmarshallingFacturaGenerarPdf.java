package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.factura.Factura;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UnmarshallingFacturaGenerarPdf {
    public static void main(String[] args) throws JAXBException {

        File fileLogo = new File("logo.jpg");

        byte[] imageBytes = null;
        try {
            imageBytes = Files.readAllBytes(fileLogo.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File fileXml = new File("000101001002000002847.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Factura fc = (Factura) jaxbUnmarshaller.unmarshal(fileXml);

        FacturaPdf facturaPdf = new FacturaPdf();
        byte[] pdf = facturaPdf.generarPdf(fc, "121212121212", "01/01/2023", imageBytes);

        Path path = Paths.get("factura.pdf");
        try {
            Files.write(path, pdf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
