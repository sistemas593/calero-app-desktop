package xades4j;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.*;
import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectFormatProperty;
import xades4j.providers.impl.DirectKeyingDataProvider;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.xml.sax.InputSource;

/**
 * Clase para firmar un XML recibido como String usando XAdES-BES
 * con un certificado digital PKCS12 (.p12).
 * Recibe el XML como String y devuelve el XML firmado como String.
 * Compatible con el SRI (Servicio de Rentas Internas) de Ecuador.
 */
public class SignXmlString
{
    /**
     * Firma un XML recibido como String y devuelve el XML firmado como String.
     *
     * @param xmlContent       el contenido XML a firmar
     * @param keystoreStream   InputStream del certificado PKCS12 (.p12)
     * @param keystorePassword contrasena del certificado
     * @return el XML firmado como String
     * @throws Exception si ocurre un error durante la firma
     */
    public static String sign(String xmlContent, InputStream keystoreStream, String keystorePassword) throws Exception
    {
        // 1. Cargar el keystore PKCS12 desde el InputStream
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keystoreStream, keystorePassword.toCharArray());

        // Obtener la clave privada y el certificado del primer alias
        Enumeration<String> aliases = keyStore.aliases();
        if (!aliases.hasMoreElements())
        {
            throw new Exception("El keystore no contiene ninguna entrada");
        }
        String alias = aliases.nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

        DirectKeyingDataProvider keyingProvider = new DirectKeyingDataProvider(certificate, privateKey);

        // 2. Parsear el String XML a Document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        InputSource is = new InputSource(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        Document doc = dbf.newDocumentBuilder().parse(is);
        Element elementToSign = doc.getDocumentElement();

        // Registrar el atributo "id" como ID XML (necesario para URI="#comprobante")
        elementToSign.setIdAttribute("id", true);

        // 3. Configurar firma XAdES-BES compatible con SRI Ecuador (SHA-1)
        SignatureAlgorithms algorithms = new SignatureAlgorithms()
                .withSignatureAlgorithm("RSA", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1)
                .withDigestAlgorithmForDataObjectReferences(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1)
                .withDigestAlgorithmForReferenceProperties(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

        BasicSignatureOptions basicOptions = new BasicSignatureOptions()
                .includePublicKey(true)
                .signKeyInfo(true);

        XadesSigner signer = new XadesBesSigningProfile(keyingProvider)
                .withSignatureAlgorithms(algorithms)
                .withBasicSignatureOptions(basicOptions)
                .newSigner();

        // Referencia al documento por id="comprobante" con DataObjectFormat
        DataObjectFormatProperty format = new DataObjectFormatProperty("text/xml")
                .withDescription("contenido comprobante");

        DataObjectDesc ref = new DataObjectReference("#comprobante")
                .withTransform(new EnvelopedSignatureTransform())
                .withDataObjectFormat(format);

        signer.sign(new SignedDataObjects(ref), elementToSign);

        // 4. Convertir el Document firmado a String
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.newTransformer().transform(new DOMSource(doc), new StreamResult(baos));
        return baos.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args)
    {
        try
        {
            String keystorePath = "1714406236001caducado.p12";
            String keystorePassword = "Tikky565656";

            // Ejemplo: XML de prueba como String
            String xmlInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<notaDebito id=\"comprobante\" version=\"1.0\">\n"
                    + "    <infoTributaria>\n"
                    + "        <ambiente>1</ambiente>\n"
                    + "        <razonSocial>Javier Cálero</razonSocial>\n"
                    + "        <ruc>1714406236001</ruc>\n"
                    + "    </infoTributaria>\n"
                    + "</notaDebito>";

            System.out.println("=== XAdES4j - Firma de XML String ===");
            System.out.println();
            System.out.println("XML de entrada:");
            System.out.println(xmlInput);
            System.out.println();

            try (InputStream ks = new java.io.FileInputStream(keystorePath))
            {
                String xmlFirmado = sign(xmlInput, ks, keystorePassword);
                System.out.println("XML firmado:");
                System.out.println(xmlFirmado);
            }

            System.out.println();
            System.out.println("=== FIRMA COMPLETADA EXITOSAMENTE ===");
        }
        catch (Exception e)
        {
            System.err.println();
            System.err.println("ERROR al firmar el documento: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
