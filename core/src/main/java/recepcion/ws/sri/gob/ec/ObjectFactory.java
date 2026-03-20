package recepcion.ws.sri.gob.ec;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    private static final QName _RespuestaSolicitud_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "RespuestaSolicitud");
    private static final QName _Mensaje_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "mensaje");
    private static final QName _Comprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "comprobante");
    private static final QName _ValidarComprobanteResponse_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobanteResponse");
    private static final QName _ValidarComprobante_QNAME = new QName("http://ec.gob.sri.ws.recepcion", "validarComprobante");

    public ObjectFactory() {
    }

    public RespuestaSolicitud createRespuestaSolicitud() {
        return new RespuestaSolicitud();
    }

    public Comprobante createComprobante() {
        return new Comprobante();
    }

    public ValidarComprobante createValidarComprobante() {
        return new ValidarComprobante();
    }

    public ValidarComprobanteResponse createValidarComprobanteResponse() {
        return new ValidarComprobanteResponse();
    }

    public Mensaje createMensaje() {
        return new Mensaje();
    }

    public RespuestaSolicitud.Comprobantes createRespuestaSolicitudComprobantes() {
        return new RespuestaSolicitud.Comprobantes();
    }

    public Comprobante.Mensajes createComprobanteMensajes() {
        return new Comprobante.Mensajes();
    }

    @XmlElementDecl(
            namespace = "http://ec.gob.sri.ws.recepcion",
            name = "RespuestaSolicitud"
    )
    public JAXBElement<RespuestaSolicitud> createRespuestaSolicitud(RespuestaSolicitud value) {
        return new JAXBElement(_RespuestaSolicitud_QNAME, RespuestaSolicitud.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ec.gob.sri.ws.recepcion",
            name = "mensaje"
    )
    public JAXBElement<Mensaje> createMensaje(Mensaje value) {
        return new JAXBElement(_Mensaje_QNAME, Mensaje.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ec.gob.sri.ws.recepcion",
            name = "comprobante"
    )
    public JAXBElement<Comprobante> createComprobante(Comprobante value) {
        return new JAXBElement(_Comprobante_QNAME, Comprobante.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ec.gob.sri.ws.recepcion",
            name = "validarComprobanteResponse"
    )
    public JAXBElement<ValidarComprobanteResponse> createValidarComprobanteResponse(ValidarComprobanteResponse value) {
        return new JAXBElement(_ValidarComprobanteResponse_QNAME, ValidarComprobanteResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ec.gob.sri.ws.recepcion",
            name = "validarComprobante"
    )
    public JAXBElement<ValidarComprobante> createValidarComprobante(ValidarComprobante value) {
        return new JAXBElement(_ValidarComprobante_QNAME, ValidarComprobante.class, (Class)null, value);
    }
}
