package autorizacion.ws.sri.gob.ec;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(
        name = "AutorizacionComprobantesOffline",
        targetNamespace = "http://ec.gob.sri.ws.autorizacion"
)
@XmlSeeAlso({ObjectFactory.class})
public interface AutorizacionComprobantesOffline {
    @WebMethod
    @WebResult(
            name = "RespuestaAutorizacionComprobante",
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "autorizacionComprobante",
            targetNamespace = "http://ec.gob.sri.ws.autorizacion",
            className = "autorizacion.ws.sri.gob.ec.AutorizacionComprobante"
    )
    @ResponseWrapper(
            localName = "autorizacionComprobanteResponse",
            targetNamespace = "http://ec.gob.sri.ws.autorizacion",
            className = "autorizacion.ws.sri.gob.ec.AutorizacionComprobanteResponse"
    )
    RespuestaComprobante autorizacionComprobante(@WebParam(name = "claveAccesoComprobante",targetNamespace = "") String var1);

    @WebMethod
    @WebResult(
            name = "RespuestaAutorizacionLote",
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "autorizacionComprobanteLote",
            targetNamespace = "http://ec.gob.sri.ws.autorizacion",
            className = "autorizacion.ws.sri.gob.ec.AutorizacionComprobanteLote"
    )
    @ResponseWrapper(
            localName = "autorizacionComprobanteLoteResponse",
            targetNamespace = "http://ec.gob.sri.ws.autorizacion",
            className = "autorizacion.ws.sri.gob.ec.AutorizacionComprobanteLoteResponse"
    )
    RespuestaLote autorizacionComprobanteLote(@WebParam(name = "claveAccesoLote",targetNamespace = "") String var1);
}
