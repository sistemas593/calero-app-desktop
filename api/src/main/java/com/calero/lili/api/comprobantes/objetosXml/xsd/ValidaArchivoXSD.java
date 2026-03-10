package com.calero.lili.api.comprobantes.objetosXml.xsd;

import org.xml.sax.SAXException;

import java.io.File;

public class ValidaArchivoXSD {
    public static String validaArchivoXSD(String nombreXsd, String pathArchivoXML, String pathArchivoXsd) throws SAXException {
        String respuestaValidacion = null;
        ValidadorEstructuraDocumento validador = new ValidadorEstructuraDocumento();
        String pathArchivoXSD = (new StringBuilder()).append(pathArchivoXsd).append(nombreXsd).toString();
        if (pathArchivoXML != null) {
            validador.setArchivoXML(new File(pathArchivoXML));
            validador.setArchivoXSD(new File(pathArchivoXSD));
            respuestaValidacion = validador.validacion();
        }
        return respuestaValidacion;
    }
}
