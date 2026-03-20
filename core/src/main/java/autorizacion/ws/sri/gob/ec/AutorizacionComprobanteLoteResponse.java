package autorizacion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "autorizacionComprobanteLoteResponse",
        propOrder = {"respuestaAutorizacionLote"}
)
public class AutorizacionComprobanteLoteResponse {
    @XmlElement(
            name = "RespuestaAutorizacionLote"
    )
    protected RespuestaLote respuestaAutorizacionLote;

    public AutorizacionComprobanteLoteResponse() {
    }

    public RespuestaLote getRespuestaAutorizacionLote() {
        return this.respuestaAutorizacionLote;
    }

    public void setRespuestaAutorizacionLote(RespuestaLote value) {
        this.respuestaAutorizacionLote = value;
    }
}
