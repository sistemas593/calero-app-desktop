package autorizacion.ws.sri.gob.ec;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@WebServiceClient(name = "AutorizacionComprobantesOfflineService", targetNamespace = "http://ec.gob.sri.ws.autorizacion")

public class AutorizacionComprobantesOfflineService extends Service {
    private static final QName AUTORIZACIONCOMPROBANTESOFFLINESERVICE_QNAME = new QName(
            "http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

    // CONSTRUCTOR
    public AutorizacionComprobantesOfflineService(String ambiente) {
        super(__getWsdlLocation(ambiente), AUTORIZACIONCOMPROBANTESOFFLINESERVICE_QNAME);
    }

    @WebEndpoint(name = "AutorizacionComprobantesOfflinePort")
    public AutorizacionComprobantesOffline getAutorizacionComprobantesOfflinePort() {
        return super.getPort(new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflinePort"),
                AutorizacionComprobantesOffline.class);
    }

    // AutorizacionComprobantesOffline CONSULTA DE DOCUMENTOS AUTORIZADOS
    // CELCER PRUEBAS
    // CEL PRODUCCION

    private static URL __getWsdlLocation(String ambiente) {
        try {
            if (ambiente.equals("1")) {
                return new URI(
                        "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl")
                        .toURL();
            } else {
                return new URI(
                        "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl")
                        .toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error creando la URL", e);
        }
    }
}
