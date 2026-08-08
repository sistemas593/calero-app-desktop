package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.EnvioCorreoModeloDto;
import com.calero.lili.core.apiSitac.dtos.contacto.ContactoRequestDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StCorreoRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerarBody {

    public EnvioCorreoModeloDto generarModelCorreoDocumentos(StCorreoRequestDto request, AdMailConfigEntity adConfigMailEntity) {


        try {

            Resource resource = new ClassPathResource("templates/documento-electronico.html");

            //log.info("Correos a enviar: {}", emailsValidos);

            EnvioCorreoModeloDto email = new EnvioCorreoModeloDto();
            setearInicialesYNombreDocumento(request.getCodigoDocumento(), email);
            email.setSubject("Adjunto documento electrónico: " + email.getInicialesDocumento() + "-" + request.getSerie() + "-" + request.getSecuencia());

            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            html = html.replace("{{nombreReceptor}}", request.getNombreReceptor())
                    .replace("{{nombreEmisor}}", request.getNombreEmisor())
                    .replace("{{rucEmisor}}", request.getRucEmisor())
                    .replace("{{nombreDocumento}}", email.getNombreDocumento())
                    .replace("{{serie}}", request.getSerie())
                    .replace("{{secuencia}}", request.getSecuencia())
                    .replace("{{fechaEmision}}", String.valueOf(request.getFechaEmision()))
                    .replace("{{claveAcceso}}", request.getClaveAcceso())
                    .replace("{{mailFrom}}", request.getMailFrom());

            email.setBody(html);
            email.setPdf(request.getPdf());
            email.setXml(request.getXml());
            email.setEmailFrom(adConfigMailEntity.getEmailFrom());
            email.setTokenApi(adConfigMailEntity.getConsumerKey());
            email.setEmailTo(request.getTo());
            email.setClaveAcceso(request.getClaveAcceso());
            return email;

        } catch (Exception exception) {
            throw new GeneralException("Error al generar el body del correo: " + exception.getMessage());
        }


    }

    public EnvioCorreoModeloDto generarModelCorreoNuevoContacto(ContactoRequestDto request, AdMailConfigEntity adConfigMailEntity) {


        try {

            Resource resource = new ClassPathResource("templates/nuevo-contacto.html");

            //log.info("Correos a enviar: {}", emailsValidos);

            EnvioCorreoModeloDto email = new EnvioCorreoModeloDto();
            email.setSubject("");

            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            html = html
                    .replace("{{name}}", request.getName())
                    .replace("{{email}}", request.getEmail())
                    .replace("{{number}}", request.getNumber())
                    .replace("{{message}}", request.getMessage());


            email.setEmailTo(request.getEmail());
            email.setEmailFrom(adConfigMailEntity.getEmailFrom());
            email.setTokenApi(adConfigMailEntity.getConsumerKey());
            email.setBody(html);
            return email;

        } catch (Exception exception) {
            throw new GeneralException("Error al generar el body del correo: " + exception.getMessage());
        }


    }


    private void setearInicialesYNombreDocumento(String codigoDocumento, EnvioCorreoModeloDto email) {
        String inicialesDocumento = switch (codigoDocumento) {
            case "01" -> "FC";
            case "03" -> "LC";
            case "04" -> "NC";
            case "05" -> "ND";
            case "06" -> "GR";
            case "07" -> "CR";
            default -> "";
        };

        String nombreDocumento = switch (codigoDocumento) {
            case "01" -> "Factura";
            case "03" -> "Liquidación de compras";
            case "04" -> "Nota de crédito";
            case "05" -> "Nota de débito";
            case "06" -> "Guía de remisión";
            case "07" -> "Comprobante de retención";
            default -> "";
        };

        email.setInicialesDocumento(inicialesDocumento);
        email.setNombreDocumento(nombreDocumento);
    }


    // TODO PENDIENTE REVISAR COMO ENVIAR EL CORREO EN API Y DESKTOP

    //String user = adConfigMailEntity.getUsuario();
    //String password = adConfigMailEntity.getPassword();
    //https://api.turbo-smtp.com/api/mail/send


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


}
