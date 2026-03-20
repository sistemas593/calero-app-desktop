package com.calero.lili.core.comprobantesPdf;

import com.calero.lili.core.comprobantes.objetosXml.liquidacionCompras.LiquidacionCompra;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class UnmarshallingLiquidacionCompraGenerarPdf {
    public static void main(String[] args) throws JAXBException {
        String xsd;
        xsd = "<xml></xml>";
        File file = new File("C:\\java\\workspace\\caleroApp\\Fegenerados\\Liquidación-Sri.xml");

        JAXBContext jaxbContext = JAXBContext.newInstance(LiquidacionCompra.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        LiquidacionCompra fc = (LiquidacionCompra) jaxbUnmarshaller.unmarshal(file);

        LiquidacionCompraPdf liquidacionCompraPdf = new LiquidacionCompraPdf();
        //liquidacionCompraPdf.generarPdf(fc, "121212121212", "01/01/2023");

    }

}
