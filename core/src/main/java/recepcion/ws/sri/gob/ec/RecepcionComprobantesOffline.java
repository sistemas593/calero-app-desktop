package recepcion.ws.sri.gob.ec;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(
        name = "RecepcionComprobantesOffline",
        targetNamespace = "http://ec.gob.sri.ws.recepcion"
)
@XmlSeeAlso({ObjectFactory.class})
public interface RecepcionComprobantesOffline {
    @WebMethod
    @WebResult(
            name = "RespuestaRecepcionComprobante",
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "validarComprobante",
            targetNamespace = "http://ec.gob.sri.ws.recepcion",
            className = "recepcion.ws.sri.gob.ec.ValidarComprobante"
    )
    @ResponseWrapper(
            localName = "validarComprobanteResponse",
            targetNamespace = "http://ec.gob.sri.ws.recepcion",
            className = "recepcion.ws.sri.gob.ec.ValidarComprobanteResponse"
    )
    RespuestaSolicitud validarComprobante(@WebParam(name = "xml",targetNamespace = "") byte[] var1);
}
