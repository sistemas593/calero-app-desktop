package xades4j;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.*;
import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectFormatProperty;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;
import xades4j.providers.impl.KeyStoreKeyingDataProvider;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;

/**
 * Clase principal para firmar un documento XML usando XAdES-BES
 * con un certificado digital PKCS12 (.p12).
 * Compatible con el SRI (Servicio de Rentas Internas) de Ecuador.
 */
public class SignXmlDocument
{
    // ========== CONFIGURACION ==========
    private static final String KEYSTORE_PATH = "1751436138001.p12";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String XML_INPUT = "xml.xml";
    private static final String XML_OUTPUT = "xml-firmado.xml";
    // ====================================

    public static void main(String[] args)
    {
        try
        {
            // No establecer ignoreLineBreaks para permitir saltos de linea en base64

            System.out.println("=== XAdES4j - Firma de Documento XML (SRI Ecuador) ===");
            System.out.println();

            // 1. Cargar el keystore PKCS12 con el certificado digital
            System.out.println("[1/4] Cargando certificado digital desde: " + KEYSTORE_PATH);
            FileSystemKeyStoreKeyingDataProvider keyingProvider = FileSystemKeyStoreKeyingDataProvider
                    .builder("PKCS12", KEYSTORE_PATH, KeyStoreKeyingDataProvider.SigningCertificateSelector.single())
                    .storePassword(new SimplePasswordProvider(KEYSTORE_PASSWORD))
                    .entryPassword(new SimplePasswordProvider(KEYSTORE_PASSWORD))
                    .fullChain(false)
                    .build();
            System.out.println("       Certificado cargado exitosamente.");

            // 2. Parsear el documento XML de entrada
            System.out.println("[2/4] Leyendo documento XML desde: " + XML_INPUT);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new File(XML_INPUT));
            Element elementToSign = doc.getDocumentElement();
            System.out.println("       Elemento raiz: <" + elementToSign.getTagName() + ">");

            // Registrar el atributo "id" como ID XML (necesario para URI="#comprobante")
            elementToSign.setIdAttribute("id", true);

            // 3. Configurar firma XAdES-BES compatible con SRI Ecuador (SHA-1)
            System.out.println("[3/4] Firmando documento con XAdES-BES (SHA-1, SRI)...");

            // Algoritmos SHA-1 (requerido por el SRI)
            SignatureAlgorithms algorithms = new SignatureAlgorithms()
                    .withSignatureAlgorithm("RSA", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1)
                    .withDigestAlgorithmForDataObjectReferences(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1)
                    .withDigestAlgorithmForReferenceProperties(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

            // Opciones: incluir clave publica RSA y firmar el KeyInfo
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
            System.out.println("       Documento firmado exitosamente.");

            // 4. Guardar el documento XML firmado
            System.out.println("[4/4] Guardando documento firmado en: " + XML_OUTPUT);
            try (FileOutputStream os = new FileOutputStream(new File(XML_OUTPUT)))
            {
                TransformerFactory tf = TransformerFactory.newInstance();
                tf.newTransformer().transform(new DOMSource(doc), new StreamResult(os));
            }

            System.out.println();
            System.out.println("=== FIRMA COMPLETADA EXITOSAMENTE ===");
            System.out.println("Archivo firmado: " + XML_OUTPUT);
        }
        catch (Exception e)
        {
            System.err.println();
            System.err.println("ERROR al firmar el documento: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Proveedor de contraseña simple para el KeyStore y las entradas de clave.
     */
    static class SimplePasswordProvider implements KeyStoreKeyingDataProvider.KeyStorePasswordProvider,
            KeyStoreKeyingDataProvider.KeyEntryPasswordProvider
    {
        private final char[] password;

        public SimplePasswordProvider(String password)
        {
            this.password = password.toCharArray();
        }

        @Override
        public char[] getPassword()
        {
            return password;
        }

        @Override
        public char[] getPassword(String alias, X509Certificate certificate)
        {
            return password;
        }
    }
}
