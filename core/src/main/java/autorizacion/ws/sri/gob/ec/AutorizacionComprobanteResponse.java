package autorizacion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "autorizacionComprobanteResponse",
        propOrder = {"respuestaAutorizacionComprobante"}
)
public class AutorizacionComprobanteResponse {
    @XmlElement(
            name = "RespuestaAutorizacionComprobante"
    )
    protected RespuestaComprobante respuestaAutorizacionComprobante;

    public AutorizacionComprobanteResponse() {
    }

    public RespuestaComprobante getRespuestaAutorizacionComprobante() {
        return this.respuestaAutorizacionComprobante;
    }

    public void setRespuestaAutorizacionComprobante(RespuestaComprobante value) {
        this.respuestaAutorizacionComprobante = value;
    }
}
