package com.calero.lili.core.comprobantes.objetosXml;

import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;

public class ObjectToString {

    public static void main(String[] arg) {

        Autorizacion aut = Autorizacion.builder()
                .comprobante("") //"<![CDATA[" + + "]]>"
                .fechaAutorizacion("")
                .estado("")
                .estado("AUTORIZADO")
                .build();

        try {
            JAXBContext context = JAXBContext.newInstance(
                    new Class[]{Autorizacion.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
//            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(aut, stringWriter);

            System.out.println(stringWriter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
