package com.calero.lili.core.varios;

//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
//import com.amazonaws.services.simpleemail.model.*;

import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;

@Service
public class AwsMailService {
    private final static String MY_EMAIL = "noresponder@software.com.ec";
    private final static String TO_EMAIL = "sistemas_593@hotmail.com";

//    @Autowired
//    private AmazonSimpleEmailServiceClient client;
//
//    @Autowired
//    private TemplateEngine templateEngine;

    private static String ATTACHMENT = "C:\\web\\calero-app-back\\PDFS\\Factura.pdf";

//    public void sendTxt() {
//        Destination destination = new Destination()
//                .withToAddresses(TO_EMAIL);
//
//        Message message = new Message()
//                .withSubject(new Content("Adjunto documento electronico"))
//                .withBody(new Body(new Content("----------------------")));
//
//        SendEmailRequest emailRequest = new SendEmailRequest()
//                .withSource(MY_EMAIL)
//                .withDestination(destination)
//                .withMessage(message);
//
//        // no permite adjuntos
//        client.sendEmail(emailRequest);
//    }

//    public void sendHtml(StCorreoRequestDto request, String xml, String pdf) throws MessagingException {
//        System.out.println("hoy enviando");
//        Context context = new Context();
//        context.setVariable("mailFrom", request.getMailFrom());
//        context.setVariable("mailTo", request.getTo());
//        context.setVariable("nombreEmisor", request.getNombreEmisor());
////        context.setVariable("rucEmisor", request.getRucEmisor());
//        context.setVariable("nombreReceptor", request.getNombreReceptor());
//        String nombreDocumento="";
//        switch (request.getCodigoDocumento()) {
//            case "01":
//                nombreDocumento="Factura";
//                break;
//            case "03":
//                nombreDocumento="Liquidación de compras";
//                break;
//            case "04":
//                nombreDocumento="Nota de crédito";
//                break;
//            case "05":
//                nombreDocumento="Nota de débito";
//                break;
//            case "07":
//                nombreDocumento="Comprobante de retención";
//                break;
//        }
//        context.setVariable("nombreDocumento", nombreDocumento);
//        context.setVariable("serie", request.getSerie());
//        context.setVariable("secuencia", request.getSecuencia());
//        context.setVariable("fechaEmision", request.getFechaEmision());
//        context.setVariable("claveAcceso", request.getClaveAcceso());
//        context.setVariable("nombreReceptor", request.getNombreReceptor());
//        context.setVariable("nombreReceptor", request.getNombreReceptor());
//
//        String html = templateEngine.process("hello", context);
//
//        Destination destination = new Destination()
//                .withToAddresses(TO_EMAIL);
//        Content htmlContent = new Content().withData(html);
//        Content textContent = new Content().withData("Versión en Html.");
//        Body body = new Body().withHtml(htmlContent).withText(textContent);
//
//        Message message = new Message()
//                .withSubject(new Content("Adjunto documento electronico"))
//                .withBody(body);
//
//
//        SendEmailRequest emailRequest = new SendEmailRequest()
//                .withSource(MY_EMAIL)
//                .withDestination(destination)
//                .withMessage(message);
//
//        client.sendEmail(emailRequest);
//    }

//    public void sendTemplate() {
//        Destination destination = new Destination()
//                .withToAddresses(TO_EMAIL);
//
//        Map<String, String> data = new HashMap<>();
//        data.put("name", "Carlos");
//        data.put("domain", "https://todotic.pe");
//
//        try {
//            String templateData = new ObjectMapper().writeValueAsString(data);
//
//            SendTemplatedEmailRequest emailRequest = new SendTemplatedEmailRequest()
//                    .withSource(MY_EMAIL)
//                    .withDestination(destination)
//                    .withTemplate("MiPlantillaSES")
//                    .withTemplateData(templateData);
//
//            client.sendTemplatedEmail(emailRequest);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }



}