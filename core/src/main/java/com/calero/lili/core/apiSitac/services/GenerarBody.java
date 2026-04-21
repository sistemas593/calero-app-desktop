package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.modClientesConfiguraciones.dto.StCorreoRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerarBody {

    public String generarBodyCorreo(StCorreoRequestDto request, AdMailConfigEntity adConfigMailEntity) {


        //log.info("Correos a enviar: {}", emailsValidos);
        String nombreDocumento = request.getCodigoDocumento();
        String inicialesDocumento = request.getCodigoDocumento();
        switch (request.getCodigoDocumento()) {
            case "01":
                nombreDocumento = "Factura";
                inicialesDocumento = "FC";
                break;
            case "03":
                nombreDocumento = "Liquidación de compras";
                inicialesDocumento = "LC";
                break;
            case "04":
                nombreDocumento = "Nota de crédito";
                inicialesDocumento = "NC";
                break;
            case "05":
                nombreDocumento = "Nota de débito";
                inicialesDocumento = "ND";
                break;
            case "06":
                nombreDocumento = "Guía de remisión";
                inicialesDocumento = "ND";
                break;
            case "07":
                nombreDocumento = "Comprobante de retención";
                inicialesDocumento = "CR";
                break;
        }

        // TODO PENDIENTE REVISAR COMO ENVIAR EL CORREO EN API Y DESKTOP

        //String user = adConfigMailEntity.getUsuario();
        //String password = adConfigMailEntity.getPassword();
        //https://api.turbo-smtp.com/api/mail/send

        String mailFrom = adConfigMailEntity.getEmailFrom();

        // Specify Credentials

//            String BODY_HTML = "<html>"
//                    + "<head></head>"
//                    +"<body style=\"width: 100%; display: flex; justify-content: start; align-items: start; margin: 0; padding: 0;\">\n" +
//                    "<div style=\"width: 100%; text-align: center; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\">\n" +
//                    "    <div style=\"width: 100%; background-color: #373CEF; padding-inline: 1rem; padding-block: 2rem;\">\n" +
//                    "        <h1 style=\"margin: 0; color: #FFFFFF; font-size: 2rem;\">Adjunto documento electronico</h1>\n" +
//                    "    </div>\n" +
//                    "    <div style=\"color: #333333; display: flex; flex-direction: column; gap: 0.5rem; margin-top: 2rem;\">\n" +
//                    "        <p style=\"font-size: 1rem; margin: 0; padding-inline: 1rem;\">Si desea puede responder al remitente al siguiente correo:</p>\n" +
//                    "        <p style=\"margin: 0; color: #000000; font-weight: 600; padding-inline: 1rem;\"><a mailto=\""+request.getMailFrom()+"  \" style=\"color: #333333;\">"+request.getMailFrom()+"</a></p>\n" +
//                    "        <p style=\"margin: 0; margin-top: 1rem; padding-inline: 1rem;\">De: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getNombreEmisor() +
//                    "        <p style=\"margin: 0; padding-inline: 1rem;\">Para: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getNombreReceptor() +
//                    "        <p style=\"margin: 0; margin-top: 1rem; padding-inline: 1rem;\">Tipo documento: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+nombreDocumento +
//                    "        <p style=\"margin: 0; padding-inline: 1rem;\">Serie: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getSerie() +
//                    "        <p style=\"margin: 0; padding-inline: 1rem;\">Secuencia: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getSecuencia() +
//                    "        <p style=\"margin: 0; padding-inline: 1rem;\">Fecha emision <small style=\"font-size: 0.9rem; font-weight: 100;\">(aaaa/mm/dd)</small>: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getFechaEmision() +
//                    "        <p style=\"margin: 0; padding-inline: 1rem;\">Clave de acceso: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600; font-size: 1rem; display: block;\">"+request.getClaveAcceso() +
//                    "        <div style=\"width: 90%; height: 1px; background-color: #333333; margin: auto; margin-top: 6rem; opacity: 0.3;\"></div>\n" +
//                    "        <p style=\"margin: 0; margin-top: 2rem; font-size: 0.9rem; font-weight: 100; padding-inline: 1rem;\">Factura electronica por: www.software.com.ec</p>\n" +
//                    "        <p style=\"margin: 0; font-size: 0.8rem; font-weight: 100; opacity: 0.7; padding-inline: 1rem;\">Este correo ha sido enviado cumpliendo la Ley de Comercio Electrónico del Ecuador y su Reglamento publicado en el Registro Oficial 735 del 31 de diciembre de 2002, Decreto No.3496, Artículo 22.</p>\n" +
//                    "\n" +
//                    "    </div>\n" +
//                    "</div>\n" +
//                    "\n" +
//                    "</body>\n"
//                    + "</html>";


//            String BODY_HTML2 = """
//                    <html>
//                    <head></head>
//                    <body style=\"width: 100%; display: flex; justify-content: start; align-items: start; margin: 0; padding: 0;\">\n" +
//                    <div style=\"width: 100%; text-align: center; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\">\n
//                        <div style=\"width: 100%; background-color: #373CEF; padding-inline: 1rem; padding-block: 2rem;\">\n
//                            <h1 style=\"margin: 0; color: #FFFFFF; font-size: 2rem;\">Adjunto documento electronico</h1>\n
//                        </div>\n
//                        <div style=\"color: #333333; display: flex; flex-direction: column; gap: 0.5rem; margin-top: 2rem;\">\n
//                            <p style=\"font-size: 1rem; margin: 0; padding-inline: 1rem;\">Si desea puede responder al remitente al siguiente correo:</p>\n
//                            <p style=\"margin: 0; color: #000000; font-weight: 600; padding-inline: 1rem;\"><a mailto=\"  {request.getMailFrom()}+"  \" style=\"color: #333333;\">"+request.getMailFrom()+"</a></p>\n
//                            <p style=\"margin: 0; margin-top: 1rem; padding-inline: 1rem;\">De: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getNombreEmisor() +
//                            <p style=\"margin: 0; padding-inline: 1rem;\">Para: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getNombreReceptor() +
//                            <p style=\"margin: 0; margin-top: 1rem; padding-inline: 1rem;\">Tipo documento: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+nombreDocumento +
//                            <p style=\"margin: 0; padding-inline: 1rem;\">Serie: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getSerie() +
//                            <p style=\"margin: 0; padding-inline: 1rem;\">Secuencia: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getSecuencia() +
//                            <p style=\"margin: 0; padding-inline: 1rem;\">Fecha emision <small style=\"font-size: 0.9rem; font-weight: 100;\">(aaaa/mm/dd)</small>: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600;\">"+request.getFechaEmision() +
//                            <p style=\"margin: 0; padding-inline: 1rem;\">Clave de acceso: <span style=\"text-transform: uppercase; color: #000000; font-weight: 600; font-size: 1rem; display: block;\">"+request.getClaveAcceso() +
//                            <div style=\"width: 90%; height: 1px; background-color: #333333; margin: auto; margin-top: 6rem; opacity: 0.3;\"></div>\n" +
//                            <p style=\"margin: 0; margin-top: 2rem; font-size: 0.9rem; font-weight: 100; padding-inline: 1rem;\">Factura electronica por: www.software.com.ec</p>\n" +
//                            <p style=\"margin: 0; font-size: 0.8rem; font-weight: 100; opacity: 0.7; padding-inline: 1rem;\">Este correo ha sido enviado cumpliendo la Ley de Comercio Electrónico del Ecuador y su Reglamento publicado en el Registro Oficial 735 del 31 de diciembre de 2002, Decreto No.3496, Artículo 22.</p>\n" +
//                    \n" +
//                        </div>\n" +
//                    </div>\n" +
//                    \n" +
//                    </body>\n"
//                    </html>""";

//             Plantilla con StringTemplate
//            String title = "Bienvenidos a la Tecnología Avanza";
//            String content = "Suscríbete a nuestro canal de YouTube";
//            ST htmlTemplate = new ST("""
//            <html>
//            <head>
//                <title><title></title>
//            </head>
//            <body>
//                <p><content></p>
//            </body>
//            </html>
//            """);
//            htmlTemplate.add("title", title);
//            htmlTemplate.add("content", content);
//            String html_content = htmlTemplate.render();
//            System.out.println(html_content);

        String subject = "  \"subject\":\"Adjunto documento electrónico: " + inicialesDocumento + "-" + request.getSerie() + "-" + request.getSecuencia() + ".\",\n";
        String jsonBody = "{\n" +
                "  \"from\":\"" + mailFrom + "\",\n" +
                "  \"to\":\"" + request.getTo() + "\",\n" +
                subject +
                "  \"html_content\":\"------Adjunto documento electronico------\\n " +
                "  <p>Para: " + request.getNombreReceptor() + "</p>" +
                "  <p>De: " + request.getNombreEmisor() + "</p>" +
                "  <p>RUC: " + request.getRucEmisor() + "</p>" +
                "  <p>Tipo: " + nombreDocumento + "</p>" +
                "  <p>Serie: " + request.getSerie() + "</p>" +
                "  <p>Secuencial: " + request.getSecuencia() + "</p>" +
                "  <p>Fecha emision: " + request.getFechaEmision() + "</p>" +
                "  <p>Clave de acceso: " + request.getClaveAcceso() + "</p>" +
                "  <p>Responder al correo: " + request.getMailFrom() + "</p>" +
                "  <p>--------------------------------------------</p>" +
                "  <p>Factura electronica por : SITAC Plus </p>" +
                "  <p>--------------------------------------------</p>\",\n" +
                "  \"attachments\": [\n" +
                "    {\n" +
                "      \"content\": \"" + request.getPdf() + "\",\n" +
                "      \"name\": \"" + request.getClaveAcceso() + ".pdf\",\n" +
                "      \"type\": \"pdf\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"content\": \"" + request.getXml() + "\",\n" +
                "      \"name\": \"" + request.getClaveAcceso() + ".xml\",\n" +
                "      \"type\": \"xml\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return jsonBody;


    }
}
