package com.calero.lili.core.comprobantes.objetosXml.xsd;

import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class ValidadorEstructuraDocumento {

    private File archivoXSD;
    private File archivoXML;

    public ValidadorEstructuraDocumento() {
    }

    public String validacion() throws SAXException {
        validarArchivo(archivoXSD, "archivoXSD");
        validarArchivo(archivoXML, "archivoXML");
        String mensaje = null;
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        //try {
        schema = schemaFactory.newSchema(archivoXSD);
        //} catch (SAXException e) {
        //    throw new IllegalStateException("Existe un error en la sintaxis del esquema", e);
        //}
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(archivoXML));
        } catch (Exception e) {
            return e.getMessage();
        }
        return mensaje;
    }

    protected void validarArchivo(File archivo, String nombre)
            throws IllegalStateException { // declara la exepcion
        if (null == archivo || archivo.length() <= 0L) {
            throw new IllegalStateException((new StringBuilder()).append(nombre).append(" es nulo o esta vacio").toString()); // lanza la exception
        } else {
            return;
        }
    }

    public File getArchivoXSD() {
        return archivoXSD;
    }

    public void setArchivoXSD(File archivoXSD) {
        this.archivoXSD = archivoXSD;
    }

    public File getArchivoXML() {
        return archivoXML;
    }

    public void setArchivoXML(File archivoXML) {
        this.archivoXML = archivoXML;
    }
}
