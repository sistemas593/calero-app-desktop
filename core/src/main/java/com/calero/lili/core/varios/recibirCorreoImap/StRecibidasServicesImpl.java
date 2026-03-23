package com.calero.lili.core.varios.recibirCorreoImap;

import com.calero.lili.core.varios.javaMail.SSLUtil;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class StRecibidasServicesImpl {

    private final GuardarComprobante guardarComprobante;
    public void procesar() {
        SSLUtil.disableSslVerification();

//        String host = "imap.gmail.com";
        String host = "mail.software.com.ec";

        String username = "documentos-electronicos@software.com.ec"; // Reemplazar con su correo electrónico
        String password = "JSDJFNMJ67JD$%)JDFJKhgss"; // Reemplazar con su contraseña

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore();
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();

            boolean xmlFound = false;
            System.out.println("Iniciando la búsqueda de archivos XML adjuntos...");
            for (Message message : messages) {
                if (message.getContent() instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) message.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && part.getFileName().toLowerCase().endsWith(".xml")) {
                            System.out.println("Archivo XML encontrado: " + part.getFileName()); //
                            guardarComprobante.guardar(part);
                            xmlFound = true;
                        }
                    }
                }
                message.setFlag(Flags.Flag.DELETED, true);
            }
            if (!xmlFound) {
                System.out.println("No se encontraron archivos XML adjuntos en los correos electrónicos.");
            } else {
                System.out.println("Se encontraron y guardaron archivos XML adjuntos.");
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
