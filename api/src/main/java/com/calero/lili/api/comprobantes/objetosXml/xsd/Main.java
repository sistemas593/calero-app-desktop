package com.calero.lili.api.comprobantes.objetosXml.xsd;

import org.xml.sax.SAXException;

public class Main {
    public static void main(String args[]) {

        String claveAcceso="000101001001000000171";
        String carpeta="C:\\java\\workspace\\caleroApp\\feGenerados\\";
        String XSD="factura.xsd";
        String xsdPath="C:\\java\\workspace\\caleroApp\\feXsd\\";
        String res;

        try {
            res = ValidaArchivoXSD.validaArchivoXSD(XSD, carpeta+claveAcceso+".xml",xsdPath);
            System.out.println(res); // null si cumple la estructura
        } catch (SAXException ex) {
            System.out.println("Existe un error en la sintaxis del esquema");
        }

    }
}
