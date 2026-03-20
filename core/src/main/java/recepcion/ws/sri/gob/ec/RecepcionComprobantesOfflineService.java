package recepcion.ws.sri.gob.ec;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@WebServiceClient(
        name = "RecepcionComprobantesOfflineService",
        targetNamespace = "http://ec.gob.sri.ws.recepcion"
)

public class RecepcionComprobantesOfflineService extends Service {
    private static final QName RECEPCIONCOMPROBANTESOFFLINESERVICE_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

    // CONSTRUCTOR
    public RecepcionComprobantesOfflineService(String ambiente) {
        super(__getWsdlLocation(ambiente), RECEPCIONCOMPROBANTESOFFLINESERVICE_QNAME);
    }

    @WebEndpoint(
            name = "RecepcionComprobantesOfflinePort"
    )
    public RecepcionComprobantesOffline getRecepcionComprobantesOfflinePort() {
        return super.getPort(new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflinePort"), RecepcionComprobantesOffline.class);
    }

    //RecepcionComprobantesOffline  ENVIO DE DOCUMENTOS A AUTORIZAR
    // CELCER PRUEBAS
    // CEL    PRODUCCION

    private static URL __getWsdlLocation(String ambiente) {
            try {
                if(ambiente.equals("1")){
                    return new URI(
                            "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl")
                            .toURL();
                }else{
                    return new URI(
                            "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl")
                            .toURL();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException("Error creando la URL", e);
            }
        }
}
